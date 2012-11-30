package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;


import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class SearchKey extends Model{
	
	@Required 
	public String keyName;
	
	public Date lastSearch;
	
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="searchKey")
	public List<Item> items = new ArrayList<Item>();
	
	public SearchKey(String name){
		this.keyName = name;
	}
	
	public static SearchKey getLeastUsed(){
		return SearchKey.find("order by lastSearch asc").first();
	}
	
	public Item getRandomItem(){
		List<Item> itemList = Item.find("searchKey = ?1 order by rand()", this).fetch(1);
		return itemList.size() > 0 ? itemList.get(0) : null;
	}

}
