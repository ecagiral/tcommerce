package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class SearchKey extends Model{
	
	@Required 
	public String keyName;
	
	public Date lastSearch;
	
	@OneToMany
	@Cascade(value={CascadeType.PERSIST,CascadeType.MERGE})
	public List<Item> item;
	
	@OneToMany
	public List<User> userList;
	
	public SearchKey(String name){
		this.keyName = name;
	}
	
	public static SearchKey getLeastUsed(){
		return SearchKey.find("order by lastSearch asc").first();
	}

}
