package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.codec.binary.Hex;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Item extends Model{
	
	@Required
	public String description;
	
	@Required
	public String picture;
	
	@ManyToOne
	public User owner;
	
	public int price;
	
	@ManyToOne
	public SearchKey searchKey;
	
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="item")
	public List<Tweet> tweets; 
	
	public Date lastAds;
	
	public Item(String description, String picture, String key, User owner, int price){
		this.description = description.toLowerCase();
		this.picture = picture;
		this.owner = owner;
		SearchKey searchKey = SearchKey.find("byKeyName", key).first();
		if(searchKey==null){
			searchKey = new SearchKey(key).save();
		}
	}
	
	public static Item findItem2Ads(){
		return null;//return Item.find("owner.adsTweetLevel <> ?1 and (select count(*) from tweet where item = ) order by lastAds", AdsTweetLevel.NONE).first();
	}
	
	public static List<Item> searchTitle(String keyword){
		return Item.find("select i from Item i where i.description like ?", "%"+keyword.toLowerCase()+"%").fetch();
	}
}
