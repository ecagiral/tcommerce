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
    String screenName;
	String authToken;
	String authTokenSecret;
	String picture;
	
	@OneToMany
	public List<Item> items; 

}
