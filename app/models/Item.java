package models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.apache.commons.codec.binary.Hex;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

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
	
	public Item(String description, String picture, String key, User owner, int price){
		this.description = description;
		this.picture = picture;
		this.owner = owner;
		SearchKey searchKey = SearchKey.find("byKeyName", key).first();
		if(searchKey==null){
			searchKey = new SearchKey(key).save();
		}
	}

}
