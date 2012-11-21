package models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Item extends Model{
	
	@Required
	public String twit;
	
	@Required
	public String picture;
	
	@ManyToOne
	public User owner;
	
	@ManyToMany
	@Cascade(value={CascadeType.PERSIST,CascadeType.MERGE})
	public List<SearchKey> searchKey;
	
	public Item(String twit, File picture, String key, User owner){
		this.twit = twit;
		this.picture = picture.getName();
		this.owner = owner;
		SearchKey searchKey = SearchKey.find("byKeyName", key).first();
		if(searchKey==null){
			searchKey = new SearchKey(key).save();
		}
		this.searchKey = new ArrayList<SearchKey>();
		this.searchKey.add(searchKey);
	}

}
