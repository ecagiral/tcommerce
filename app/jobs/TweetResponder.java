package jobs;

import graph.GraphDatabase;
import models.Tweet;
import models.User;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@Every("1s")
public class TweetResponder extends Job {
	@Override
	public void doJob() {
		Tweet tweet = Tweet.getTweet2Ads();
		if (tweet != null) {
			int level = GraphDatabase.findConnectionBetweet(
					tweet.item.owner.twitterId, tweet.owner.twitterId);
			if (tweet != null
					&& (tweet.item.owner.adsTweetLevel.getLevel() == -1 || (tweet.item.owner.adsTweetLevel
							.getLevel() <= level && tweet.item.owner.adsTweetLevel
							.getLevel() > 0))) {

			}
		}
	}
}
