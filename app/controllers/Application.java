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
    	Long userId = Cache.get(session.getId(), Long.class);
    	User user = null;
    	if(userId!=null){
    		user = User.findById(userId);
    		renderArgs.put("user",user);
    	}
    	Item product = Item.findById(itemId);
    	if(user!=null && product != null){
    		new Visitor(product,user).save();
    	}
    	for(Comment comment :product.comments){
    		Logger.info(comment.commentText);
    	}
    	render(product);
    }
    
    public static void addItem(Long id, String description, String keyword, File picture){
    	Long userId = Cache.get(session.getId(), Long.class);
    	User user = null;
    	if(userId!=null){
    		user = User.findById(userId);
    		renderArgs.put("user",user);
    	}
    	String generatedFileName = util.Codec.sha1_hex(UUID.randomUUID()+ String.valueOf(Calendar.getInstance().getTimeInMillis()));
    	String fileName = generatedFileName + ".jpg";
    	try {
			FileUtils.moveFile(picture, new File(Start.getImagePath() + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	String fullUrl = request.current().getBase() + "/image/" + fileName;
    	if(id == null){
	    	new Item(description,fullUrl,keyword,user).save();
	    	index();
    	}
    	else{
    		Item product = Item.findById(id);
    		if(product != null){
    			product.update(description,fullUrl,keyword);
    		}
    		render("application/showItem.html", product);
    	}
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
    
    public static void profile(Long profileId){
    	Long userId = Cache.get(session.getId(), Long.class);
    	User user = null;
    	if(userId!=null){
    		user = User.findById(userId);
    		renderArgs.put("user",user);
    	}
    	User profile = User.findById(profileId);
    	List<Item> items = Item.findItemsByUser(profile);
    	render(profile, items);
    }

    
    public static void deleteItem(Long itemId){
    	Long userId = Cache.get(session.getId(), Long.class);
    	Item item = Item.findById(itemId);
    	User user = null;
    	if(userId!=null){
    		user = User.findById(userId);
    		renderArgs.put("user",user);
    	}
    	if(item.owner.id.equals(userId)){
    		item.delete();
    	}
    	List<Item> items = Item.findItemsByUser(item.owner);
    	User profile = item.owner;
    	render("application/profile.html", profile, items);
    }
    
    public static void sendtweet(){
    	Tweet tweet = Tweet.getTweet2Ads();
    }
    
    public static void addComment(Long itemId, String text){
    	String error;
    	Long userId = Cache.get(session.getId(), Long.class);
    	if(userId==null){
    		error = "you need to sign up";
    		render(error);
    	}
    	User user = User.findById(userId);
    	if(user==null){
    		error = "you need to sign up";
    		render(error);
    	}
    	renderArgs.put("user",user);
    	Item item = Item.findById(itemId);
    	if(item==null){
    		error = "you need to sign up";
    		render(error);
    	}
    	Comment comment = new Comment(user,text,item).save();
    	render(comment);
    }
}