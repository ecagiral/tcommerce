package controllers;

import jobs.OAuthSettings;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import models.User;
import models.UserType;
import play.cache.Cache;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.Util;
import play.mvc.Scope.Params;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Auth extends Controller{
	
	private final static String ORIGINAL_URL_SESSION_KEY = "original_url";
	static ServiceBuilder sb = new ServiceBuilder()
    .provider(TwitterApi.class)
    .apiKey(OAuthSettings.getConsumerKey())
    .apiSecret(OAuthSettings.getConsumerSecret()).callback(Router.getFullUrl("Auth.twitterCallback"));
	
    @Before(unless = { "Application.index","authenticate", "logout", "login", "signup", "register", "twitterCallback", "registerWithTwitter", "twitterAuthentication"}, priority = 0)
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

        user = new User(user.email, user.password);
        try {
            user.save();
        } catch (Exception e) {
            error();
        }
        afterLogin(session.getId(), user.id);
        Auth.login();
    }
    
    public static void authenticate(User user) {
        User fetched = User.findByEmailOrUsername(user.email);
        if (fetched == null) {
            flash.error(Messages.get("invalid.user.name"));
            Auth.login();
        } else {
            boolean checkPassword = fetched.checkPassword(user.password);
            if(checkPassword){
                afterLogin(session.getId(), fetched.id);
            }else{
                flash.error(Messages.get("invalid.password"));
                Auth.login();
            }
        }
        
    }

    @Util
    private static void afterLogin(String sessionId, Long userId) {
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
    

    public static void twitterAuthentication(){
        OAuthService service = sb.build();
        Token requestToken = service.getRequestToken();
        Cache.add(requestToken.getToken(), requestToken, "3min");
        String authorizationUrl = service.getAuthorizationUrl(requestToken);
        redirect(authorizationUrl);
    }
    
    public static void twitterCallback(String oauth_token, String oauth_verifier, String denied) throws TwitterException {
        if (denied != null) {
            Cache.delete(oauth_token);
            login();
        }
        Token token = Cache.get(oauth_token, Token.class);
        Cache.delete(oauth_token);
        
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(OAuthSettings.getConsumerKey(),
        		OAuthSettings.getConsumerSecret());
        RequestToken requestToken = new RequestToken(token.getToken(), token.getSecret());
        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,
        		oauth_verifier);
        
        long userId = accessToken.getUserId();
        String authToken = accessToken.getToken();
		String authTokenSecret = accessToken.getTokenSecret();
		twitter.setOAuthAccessToken(accessToken);
		twitter4j.User twUser =  twitter.showUser(userId);
        User user = User.findByTwitterId(userId);
        if(user==null){
        	user = new User(twUser, authToken, authTokenSecret);
        }
        else{
        	user.updateTwData(twUser,authToken,authTokenSecret);
        }
  	
    	user.save();
        
    	afterLogin(session.getId(), user.id);
    }
  }
