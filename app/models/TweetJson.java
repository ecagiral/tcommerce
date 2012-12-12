package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TweetJson {
	public String ownerId;
	public String tweetId;
	public String tweet;
	public boolean responded;
	public String respondedBy;
	public String itemId;
	public Date created;
	
	public TweetJson(Tweet tweet){
		this.ownerId = tweet.owner.id.toString();
		this.tweetId = tweet.id.toString();
		this.tweet = tweet.tweet;
		this.responded = tweet.responded;
		this.respondedBy = tweet.respondedBy == null ? null : tweet.respondedBy.id.toString();
		this.itemId = tweet.item.id.toString();
		this.created = new Date(tweet.created.getTime());
	}
	
	public static List<TweetJson> toTweetJsonList(List<Tweet> tweetList){
		ArrayList<TweetJson> tweetJsonList = new ArrayList<TweetJson>(tweetList.size());
		for(Tweet tweet : tweetList){
			tweetJsonList.add(new TweetJson(tweet));
		}
		return tweetJsonList;
	}
	
}
