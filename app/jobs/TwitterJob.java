package jobs;

import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

@Every("1mn")
public class TwitterJob extends Job {
	
	public void doJob() {
		//Works for every hour
		Logger.info("Twitter Job triggered");
	}

}
