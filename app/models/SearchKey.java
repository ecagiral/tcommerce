package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class SearchKey extends Model{
	
	@Required 
	public String keyName;
	
	@ManyToMany
	@Cascade(value={CascadeType.PERSIST,CascadeType.MERGE})
	public List<Item> item;
	
	public SearchKey(String name){
		this.keyName = name;
	}

}
