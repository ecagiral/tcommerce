package controllers;

import play.*;
import play.mvc.*;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.io.File;
import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	Logger.info("index triggered");
    	List<Item> items = Item.findAll();
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
    
    public static void signin(){
    	Twitter twitter = new TwitterFactory().getInstance();
    	String callbackURL  = request.url;
    	int index = callbackURL.lastIndexOf("/");
    	//twitter.setOAuthConsumer(configMgr.getConsumerKey(),
         //       configMgr.getConsumerSecret());
    	//Logger.debug("Consumer Key: " + configMgr.getConsumerKey()
         //       + ", Consumer Secret: " + configMgr.getConsumerSecret());
    	// RequestToken requestToken = twitter
          //      .getOAuthRequestToken(callbackURL.toString());
    	//request.getSession().setAttribute(CallBackServlet.REQUEST_TOKEN, requestToken);
    	//response.sendRedirect(requestToken.getAuthenticationURL());
    }

}