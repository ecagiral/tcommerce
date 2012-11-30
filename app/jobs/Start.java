package jobs;

import graph.GraphDatabase;

import java.io.File;

import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Start extends Job {
	private static String IMAGE_PATH = null;
	private static final String FS = System.getProperty("file.separator");
	public void doJob(){
		createImageFolder();
		GraphDatabase.startGraphDatabase();
	}
	
	private void createImageFolder(){
		String home = System.getenv("HOME");
    	String imagePath = home + FS + ".tcommerce" + FS + "images" + FS;
    	File file = new File(imagePath);
    	if(!file.exists()){
    		file.mkdirs();
    	}
    	IMAGE_PATH = imagePath;
	}
	
	public static String getImagePath(){
		return IMAGE_PATH;
	}

}
