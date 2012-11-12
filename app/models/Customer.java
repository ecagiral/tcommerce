package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Customer extends Model{
	
	@Required
	public String username;
	
	@OneToMany
	public List<Item> items; 

}
