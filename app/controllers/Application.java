package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.io.File;
import java.util.*;

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
    
    public static void showItem(Item item){
    	Logger.info(item.toString());
    	render();
    }
    
    public static void addItem(String twit, File picture, String key){
    	new Item(twit,picture,key,null).save();
    	index();
    }

    public static void search(String keywors){

    }

    public static void showUser(Long userId){

    }

    public static void showUserItems(Long userId){

    }

    public static void signout(){

    }
    
}