package jobs;

import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart(async=true)
public class TweetResponder extends Job{

	@Override
	public void doJob() {
		while(true){
			User user = User.findUser4Tweet();
			
		}
	}

}
