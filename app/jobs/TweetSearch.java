package jobs;

import models.SearchKey;
import models.User;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import twitter.TwitterProxy;
import twitter.TwitterProxyImpl;

@OnApplicationStart(async=true)
public class TweetSearch extends Job {
	private static final long INTERVAL = 10 * 60 * 1000;

	@Override
	public void doJob() {
		while (true) {
			try {
				SearchKey searchKey = SearchKey.getLeastUsed();
				if (searchKey != null) {
					User user = User.findLeastUsed();
					if(user != null){
						TwitterProxy twitterProxy = new TwitterProxyImpl(user);
						twitterProxy.search(searchKey);
						Thread.sleep(INTERVAL);
					}
				}
				
			} catch (Exception e) {
				Logger.error(e, "Error in tweet search");
			}
		}
	}

}
