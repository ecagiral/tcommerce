package twitter;

import graph.GraphDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.exception.ConstraintViolationException;

import play.Logger;

import jobs.OAuthSettings;
import twitter4j.IDs;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import models.Item;
import models.Reply;
import models.SearchKey;
import models.Tweet;
import models.User;

public class TwitterProxyImpl implements TwitterProxy {
	private static final SimpleDateFormat dt = new SimpleDateFormat(
			"yyyy-mm-dd");
	User user = null;
	Twitter twitter = null;

	public TwitterProxyImpl(User user) {
		this.user = user;
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(OAuthSettings.getConsumerKey(),
				OAuthSettings.getConsumerSecret());
		twitter.setOAuthAccessToken(new AccessToken(user.authToken,
				user.authTokenSecret));
	}

	public void search(SearchKey searchKey) {
		if(searchKey.items == null || searchKey.items.size() == 0){
			searchKey.delete();
			return;
		}
		StringBuilder enchancedQuery = new StringBuilder(searchKey.keyName);
		enchancedQuery.append(" -http");
		Query _query = new Query(enchancedQuery.toString());
		_query.setResultType(Query.RECENT);
		// get results after last search
		if (searchKey.lastSearch != null) {
			String formattedDate = dt.format(searchKey.lastSearch);
			_query.since(formattedDate);
		}

		searchKey.lastSearch = Calendar.getInstance().getTime();
		searchKey.save();

		try {
			QueryResult queryResult = twitter.search(_query);
			List<twitter4j.Tweet> tweetList = queryResult.getTweets();
			for (twitter4j.Tweet tweet : tweetList) {
				Tweet t = new Tweet();
				t.tweet = tweet.getText();
				t.tweetId = tweet.getId();
				t.created = tweet.getCreatedAt();
				User owner = User.findByTwitterId(tweet.getFromUserId());
				if (owner == null) {
					try{
						twitter4j.User fromUser = twitter.showUser(tweet
							.getFromUserId());
						owner = new User(fromUser, null, null);
						owner.save();
						t.owner = owner;
					}catch (TwitterException e) {
						Logger.error(e,"");
					}
				}
				t.responded = false;
				t.respondedBy = null;
				t.item = searchKey.items.get((int)(searchKey.items.size() * Math.random()));
				try{
					if(t.owner != null){
						t.save();
					}
				}catch (PersistenceException e) {
					if(! (e.getCause() instanceof ConstraintViolationException)){
						Logger.error(e, "");
					}
				}
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addFriends() {
		ResponseList<twitter4j.User> userResponseList = null;
		long cursor = -1;
		IDs ids;
		long[] allIds = new long[0];
		try {
			do {
				ids = twitter.getFriendsIDs(cursor);
				long[] idArr = ids.getIDs();
				allIds = ArrayUtils.addAll(allIds, idArr);
				if (idArr != null) {
					if (idArr.length > 100) {
						for (int i = 0; i < idArr.length; i += 100) {
							long[] subArray = Arrays
									.copyOfRange(
											idArr,
											i,
											(i + 100) >= idArr.length ? (idArr.length - 1)
													: (i + 100));
							if (userResponseList == null) {
								userResponseList = twitter
										.lookupUsers(subArray);
							} else {
								userResponseList.addAll(twitter
										.lookupUsers(subArray));
							}
						}
					} else {
						userResponseList = twitter.lookupUsers(idArr);
					}
				}

			} while ((cursor = ids.getNextCursor()) != 0);
			saveResponseList2Db(userResponseList);
			GraphDatabase.addUserAndFriends(user.twitterId, allIds);
		} catch (Exception e) {
		}
	}

	public void saveResponseList2Db(ResponseList<twitter4j.User> userResponseList) {
		for (twitter4j.User user : userResponseList) {
			User u = new User(user);
			u.save();
			
		}
	}

	@Override
	public void reply(Reply reply){
		try {
			Status status = twitter.updateStatus(new StatusUpdate(reply.tweet).inReplyToStatusId(reply.source.tweetId));
			reply.tweetId = status.getId();
		} catch (TwitterException e) {
			e.printStackTrace();
		} 
	}
}
