package controllers;

import play.*;
import play.mvc.*;

import java.io.File;
import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	
    	List<Item> items= Item.findAll();
        render(items);
    }
    
    public static void itemAdd(){
    	render();
    }
    
    public static void addItem(String twit, File picture, String key){
    	new Item(twit,picture,key,null).save();
    	index();
    }

}