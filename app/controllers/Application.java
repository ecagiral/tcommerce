package controllers;

import play.*;
import play.mvc.*;

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
    	
    }

}