package jobs;

import models.SearchKey;
import models.User;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import twitter.TwitterProxyImpl;
import twitter4j.TwitterFactory;

@Every("10s")
public class TweetSearch extends Job {
	@Override
	public void doJob() {
		try {
			SearchKey searchKey = SearchKey.getLeastUsed();
			if (searchKey != null) {
				User user = User.findLeastUsed();
				if (user != null) {
					TwitterProxy twitterProxy = TwitterProxyFactory
							.newInstance(user);
					twitterProxy.search(searchKey);
				}
			}

		} catch (Exception e) {
			Logger.error(e, "Error in tweet search");
		}
	}
}
