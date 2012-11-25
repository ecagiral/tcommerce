package controllers;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import models.User;
import play.cache.Cache;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Util;
import play.mvc.Scope.Params;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class Auth extends Controller{
	
	private final static String ORIGINAL_URL_SESSION_KEY = "original_url";
	static ServiceBuilder sb = new ServiceBuilder()
    .provider(TwitterApi.class)
    .apiKey("?")
    .apiSecret("?");
	
    @Before(unless = { "authenticate", "logout", "login", "signup", "register", "twitterCallback", "registerWithTwitter"}, priority = 0)
    static void before() {
        Long userId = Cache.get(session.getId(), Long.class);
        if (userId == null) {
            flash.put(ORIGINAL_URL_SESSION_KEY, request.url);
            login();
        }
        renderArgs.put("user", getCurrentUser());
    }
    
    public static void login() {
        if (Cache.get(session.getId()) != null) {
            redirect("/");
        }
        flash.keep(ORIGINAL_URL_SESSION_KEY);
        if (request.isAjax()) {
            forbidden();
        }
        render();
    }
    
    public static void logout() {
        Long userId = Cache.get(session.getId(), Long.class);
        // see NotificationHelper, get method
        Cache.delete(userId + "_n");
        Cache.delete(session.getId());
        session.clear();
        redirect("/");
    }
    
    public static void signup() {
        render();
    }
    
    public static void register(User user) {

        if (User.findByEmailOrUsername(user.email) != null) {
            flash.error(Messages.get("signup.emailInUse"));
            Auth.signup();
        }

        user = new User(user.email, user.screenName, user.password);
        try {
            user.save();
        } catch (Exception e) {
            error();
        }
        afterLogin(user.id, session.getId());
        Auth.login();
    }
    
    public static void authenticate(User user) {
        User fetched = User.findByEmailOrUsername(user.screenName);
        if (fetched == null) {
            flash.error(Messages.get("invalid.user.name"));
            Auth.login();
        } else {
            boolean checkPassword = fetched.checkPassword(user.password);
            if(checkPassword){
                afterLogin(fetched.id, session.getId());
            }else{
                flash.error(Messages.get("invalid.password"));
                Auth.login();
            }
        }
        
    }

    @Util
    private static void afterLogin(Long userId, String sessionId) {
        // cache replaces may be unnecessary ?
        if (Cache.get(sessionId) == null) {
            Cache.add(sessionId, userId);
        } else {
            Cache.replace(sessionId, userId);
        }
        redirect(flash.get(ORIGINAL_URL_SESSION_KEY) != null ? flash.get(ORIGINAL_URL_SESSION_KEY) : "/");
    }
    
    @Util
    public static User getCurrentUser() {
        Long userId = Cache.get(session.getId(), Long.class);
        if (userId == null) {
            session.clear();
            flash.put(ORIGINAL_URL_SESSION_KEY, request.url);
            login();
        }
        return User.findById(userId);
    }
    
    public static void twitterAuthenticate(){
        String tk = Codec.UUID();
        OAuthService service = sb.callback("http://localhost:9000/twitter/" + tk + "/").build();
        Token requestToken = service.getRequestToken();
        Cache.add(tk, requestToken, "3min");
        String authorizationUrl = service.getAuthorizationUrl(requestToken);
        redirect(authorizationUrl);
    }
    
    public static void twitterCallback(String tokenKey, String denied) {
        if (denied != null) {
            Cache.delete(tokenKey);
            login();
        }
        Token t = Cache.get(tokenKey, Token.class);
        Verifier verifier = new Verifier(Params.current().get("oauth_verifier"));
        OAuthService service = sb.callback("http://localhost:9000/twitter/" + tokenKey + "/").build();
        Token token = service.getAccessToken(t, verifier);
        String rawResponse = token.getRawResponse();
        Cache.delete(tokenKey);
        String[] splitted = rawResponse.split("&");
        String oauthToken = null, oauthTokenSecret = null, userId = null, screenName = null;
        for (String s : splitted) {
            String value = s.split("=")[1];
            if (s.contains("oauth_token=")) {
                oauthToken = value;
            } else if (s.contains("oauth_token_secret=")) {
                oauthTokenSecret = value;
            } else if (s.contains("user_id=")) {
                userId = value;
            } else if (s.contains("screen_name=")) {
                screenName = value;
            }
        }
        User user = User.findByTwitterId(Long.getLong(userId));
        if(user==null){
        	user = new User(screenName,oauthToken,oauthTokenSecret,Long.getLong(userId));
        	user.save();
        }else{
        	user.authToken = oauthToken;
        	user.authTokenSecret = oauthTokenSecret;
        	user.save();
        }
        afterLogin(user.id, session.getId());
    }
    
    public static void twitterLogin(){
    	Twitter twitter = new TwitterFactory().getInstance();
    	String callbackURL  = request.url;
    	int index = callbackURL.lastIndexOf("/");
    	//twitter.setOAuthConsumer(configMgr.getConsumerKey(),
         //       configMgr.getConsumerSecret());
    	//Logger.debug("Consumer Key: " + configMgr.getConsumerKey()
         //       + ", Consumer Secret: " + configMgr.getConsumerSecret());
    	// RequestToken requestToken = twitter
          //      .getOAuthRequestToken(callbackURL.toString());
    	//request.getSession().setAttribute(CallBackServlet.REQUEST_TOKEN, requestToken);
    	//response.sendRedirect(requestToken.getAuthenticationURL());
    }

}
