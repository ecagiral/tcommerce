package jobs;

import java.awt.ItemSelectable;

import models.Item;
import models.User;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class DataFill extends Job {
	public void doJob(){
		if(User.count() == 0) {
            Fixtures.deleteDatabase();
            Fixtures.loadModels("datafill.yml");
		}
	}
}
