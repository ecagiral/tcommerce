package jobs;

import graph.GraphDatabase;
import models.AdsTweetLevel;
import models.Reply;
import models.Tweet;
import models.User;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import twitter4j.TwitterFactory;

@Every("1s")
public class TweetResponder extends Job {
	@Override
	public void doJob() {
		Tweet tweet = Tweet.getTweet2Ads();
		//check that do we have tweets to advertise
		if (tweet != null) {
			//if send level set to everyone no need to check shortest path on grahpdb
			if(tweet.item.owner.adsTweetLevel == AdsTweetLevel.EVERYONE){
				TwitterProxy proxy = TwitterProxyFactory.newInstance(tweet.item.owner);
				Reply reply = new Reply();
				reply.tweet = generateReply(tweet);
				reply.source = tweet;
				proxy.reply(reply);
			}
			//if set to none do nothing
			else if(tweet.item.owner.adsTweetLevel != AdsTweetLevel.NONE){
				//if not EVERYONE nor NONE find connection level between nodes
				int level = GraphDatabase.findConnectionBetweet(
						tweet.item.owner.twitterId, tweet.owner.twitterId);
				//if there is connection bigger than 0 check that if it is in
				//boundries or not
				if(level > 0 && level <= tweet.item.owner.adsTweetLevel.level  ){
					//if it is in boundries send reply.
					TwitterProxy proxy = TwitterProxyFactory.newInstance(tweet.item.owner);
					Reply reply = new Reply();
					reply.tweet = generateReply(tweet);
					reply.source = tweet;
					proxy.reply(reply);
				}
			}
		}
	}
	
	private static String generateReply(Tweet tweet){
		int defaultSize = tweet.owner.screenName.length() + tweet.item.shortLink.length() + 3;
		int textSize = 140 - defaultSize;
		String text = tweet.item.description.length() < textSize ? tweet.item.description : tweet.item.description.substring(0, textSize);
		StringBuilder message = new StringBuilder();
		message.append("@").append(tweet.owner.screenName).append(" ").append(text).append(" ").append(tweet.item.shortLink);
		return message.toString();
	}
}
