package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model{
	
	@Required
	public String username;
    String screenName;
	
	@OneToMany
	public List<Item> items; 

}
