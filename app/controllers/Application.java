package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import util.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import jobs.Start;

import org.apache.commons.collections.map.HashedMap;
import org.apache.ivy.util.FileUtil;

import com.sun.xml.internal.ws.api.pipe.Codec;

import models.*;

@With({Auth.class})
public class Application extends Controller {

    public static void index() {
    	Logger.info("index triggered");
    	List<Item> items = Item.findAll();
    	Long userId = Cache.get(session.getId(), Long.class);
    	if(userId!=null){
    		renderArgs.put("user",User.findById(userId));
    	}
        render(items);
    }
    
    public static void itemAdd(){
    	render();
    }
    
    public static void showItem(Long itemId){
    	Item item = Item.findById(itemId);
    	Logger.info(item.toString());
    	render();
    }
    
    public static void addItem(String description, String price, String keyword, File picture){
    	int princeInt = Integer.parseInt(price);
    	String generatedFileName = util.Codec.sha1_hex(UUID.randomUUID()+ String.valueOf(Calendar.getInstance().getTimeInMillis()));
    	
    	String fileName = generatedFileName + ".jpg";
    	try {
			FileUtils.moveFile(picture, new File(Start.getImagePath() + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	String fullUrl = request.current().getBase() + "/image/" + fileName;
    	Long userId = Cache.get(session.getId(), Long.class);
        User owner =  User.findById(userId);
    	new Item(description,fullUrl,keyword,owner,princeInt).save();
    	index();
    }
    
    public static void displayImage(String imageName){
    	File file = new File(Start.getImagePath() + imageName);
    	renderBinary(file);
    }

    public static void search(String query){
    	Long userId = Cache.get(session.getId(), Long.class);
    	if(userId!=null){
    		renderArgs.put("user",User.findById(userId));
    	}
    	List<Item> items = new ArrayList<Item>();
    	if(query==null){
    		render("application/index.html",items);
    	}
    	//get only first word
    	query = query.trim().split(" ")[0];
    	if(query == ""){
    		render("application/index.html",items);
    	}
    	items = Item.searchTitle(query);

        render("application/index.html",items);
    }

    public static void showUser(Long userId){

    }

    public static void showUserItems(Long userId){

    }
    
    public static void sendtweet(){
    	Tweet tweet = Tweet.getTweet2Ads();
    }
}