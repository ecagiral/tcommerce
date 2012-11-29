package twitter;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import jobs.OAuthSettings;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import models.SearchKey;
import models.Tweet;
import models.User;
import models.UserType;

public class TwitterProxyImpl implements TwitterProxy{
	private static final SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd"); 
	User user = null;
	Twitter twitter = null;
	public TwitterProxyImpl(User user){
		if(user.type != UserType.TWITTER){
			throw new IllegalArgumentException("User is not a twitter user");
		}
		this.user = user;
		twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(OAuthSettings.getConsumerKey(),
        		OAuthSettings.getConsumerSecret());
        twitter.setOAuthAccessToken(new AccessToken(user.authToken, user.authTokenSecret));
	}
	
	public void reply(long tweetId, String message){
		
	}
	
	public void search(SearchKey searchKey){
		StringBuilder enchancedQuery = new StringBuilder(searchKey.keyName);
		enchancedQuery.append(" -http");
		Query _query = new Query(enchancedQuery.toString());
		_query.setResultType(Query.RECENT);
		//get results after last search
		if(searchKey.lastSearch != null){
			String formattedDate = dt.format(searchKey.lastSearch);
			_query.since(formattedDate);
		}
		
		searchKey.lastSearch = Calendar.getInstance().getTime();
		searchKey.save();
		
		try {
			QueryResult queryResult = twitter.search(_query);
			List<twitter4j.Tweet> tweetList = queryResult.getTweets();
			for(twitter4j.Tweet tweet : tweetList){
				Tweet t = new Tweet();
				t.tweet = tweet.getText();
				t.tweetId = tweet.getId();
				User owner = User.findByTwitterId(tweet.getFromUserId());
				if(owner == null){
					twitter4j.User fromUser = twitter.showUser(tweet.getFromUserId());
					owner = new User(fromUser, null, null);
					owner.save();
					t.owner = owner;
				}
				t.responded = false;
				t.respondedBy = null;
				t.save();
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
