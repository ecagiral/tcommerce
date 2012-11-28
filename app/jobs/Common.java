package jobs;

import java.io.File;

import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Common extends Job {
	private static String IMAGE_PATH = null;
	public static final String FS = System.getProperty("file.separator");
	public void doJob(){
		createImageFolder();
	}
	
	private void createImageFolder(){
		String home = System.getenv("HOME");
    	String imagePath = home + FS + ".tcomitem" + FS;
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
