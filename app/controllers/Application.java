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

@With({ Auth.class })
public class Application extends Controller {
	public static void index() {
		List<Item> items = Item.findAll();
		Long userId = Cache.get(session.getId(), Long.class);
		if (userId != null) {
			renderArgs.put("user", User.findById(userId));
		}
		render(items);
	}

	public static void showItem(Long itemId) {
		Long userId = Cache.get(session.getId(), Long.class);
		User user = null;
		if (userId != null) {
			user = User.findById(userId);
			renderArgs.put("user", user);
		}
		Item product = Item.findById(itemId);
		if (user != null && product != null) {
			new Visitor(product, user).save();
		}
		for (Comment comment : product.comments) {
			Logger.info(comment.commentText);
		}
		render(product);
	}

	public static void addItem(String description, String keyword, File picture) {
		Long userId = Cache.get(session.getId(), Long.class);
		User user = null;
		if (userId != null) {
			user = User.findById(userId);
			renderArgs.put("user", user);
		}
		String fullUrl = null;
		String generatedFileName = util.Codec.sha1_hex(UUID.randomUUID()
				+ String.valueOf(Calendar.getInstance().getTimeInMillis()));
		String fileName = generatedFileName + ".jpg";
		try {
			FileUtils.moveFile(picture, new File(Start.getImagePath()
					+ fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		fullUrl = request.current().getBase() + "/image/" + fileName;
		new Item(description, fullUrl, keyword, user).save();
		index();
	}

	public static void updateItem(Long id, String description, String keyword,
			File picture) {
		Long userId = Cache.get(session.getId(), Long.class);
		User user = null;
		if (userId != null) {
			user = User.findById(userId);
			renderArgs.put("user", user);
		}
		Item product = Item.findById(id);
		String fullUrl = null;
		if (picture != null) {
			String generatedFileName = util.Codec.sha1_hex(UUID.randomUUID()
					+ String.valueOf(Calendar.getInstance().getTimeInMillis()));
			String fileName = generatedFileName + ".jpg";
			try {
				FileUtils.moveFile(picture, new File(Start.getImagePath()
						+ fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
			fullUrl = request.current().getBase() + "/image/" + fileName;
		}
		if (product != null) {
			product.update(description, fullUrl, keyword);
		}
		profile(userId);
	}

	public static void itemData(Long id) {
		Item item = Item.findById(id);
		String error = null;
		if (item == null) {
			error = "This product does not exist";
			render(error);
		} else {
			render(item);
		}
	}

	public static void displayImage(String imageName) {
		File file = new File(Start.getImagePath() + imageName);
		renderBinary(file);
	}

	public static void search(String query) {
		Long userId = Cache.get(session.getId(), Long.class);
		if (userId != null) {
			renderArgs.put("user", User.findById(userId));
		}
		List<Item> items = new ArrayList<Item>();
		if (query == null) {
			render("application/index.html", items);
		}
		// get only first word
		query = query.trim().split(" ")[0];
		if (query == "") {
			render("application/index.html", items);
		}
		items = Item.searchTitle(query);
		render("application/index.html", items);
	}

	public static void profile(Long profileId) {
		Long userId = Cache.get(session.getId(), Long.class);
		User user = null;
		if (userId != null) {
			user = User.findById(userId);
			renderArgs.put("user", user);
			
		}
		User profile = User.findById(profileId);
		List<Item> items = Item.findItemsByUser(profile);
		render(profile, items);
	}

	public static void deleteItem(Long itemId) {
		Long userId = Cache.get(session.getId(), Long.class);
		Item item = Item.findById(itemId);
		User user = null;
		if (userId != null) {
			user = User.findById(userId);
			renderArgs.put("user", user);
		}
		if (item.owner.id.equals(userId)) {
			item.delete();
		}
		List<Item> items = Item.findItemsByUser(item.owner);
		User profile = item.owner;
		render("application/profile.html", profile, items);
	}

	public static void addComment(Long itemId, String text) {
		String error;
		Long userId = Cache.get(session.getId(), Long.class);
		if (userId == null) {
			error = "you need to sign up";
			render(error);
		}
		User user = User.findById(userId);
		if (user == null) {
			error = "you need to sign up";
			render(error);
		}
		renderArgs.put("user", user);
		Item item = Item.findById(itemId);
		if (item == null) {
			error = "you need to sign up";
			render(error);
		}
		Comment comment = new Comment(user, text, item).save();
		render(comment);
	}
	
	public static void updateSettings(AdsTweetLevel adsTweetLevel){
		Long userId = Cache.get(session.getId(), Long.class);
		if (userId != null) {
			User user = User.findById(userId);
			user.adsTweetLevel = adsTweetLevel;
			user.save();
			profile(userId);
		}
		else{
			index();
		}
	}
	
	public static void showCustomers(){
		Long userId = Cache.get(session.getId(), Long.class);
		if (userId != null) {
			User user = User.findById(userId);
			List<Item> itemList = Item.findItemsByUser(user);
			render(itemList);
		}
		else{
			index();
		}
	}
	
	public static void showProductTweets(Long productId){
		Item item = Item.findById(productId);
		if(item != null){
			Long userId = Cache.get(session.getId(), Long.class);
			if(item.owner.id == userId){
				List<Tweet> tweetList = item.tweets;
				renderJSON(TweetJson.toTweetJsonList(tweetList));
			}
			else{
				String error = "You are not allowed to execute this request";
				render(error);
			}
		}
		else{
			String error = "This product does not exist.";
			render(error);
		}
	}
}