package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Visitor extends Model {
	
	@ManyToOne
	public Item item;
	
	@ManyToOne
	public User user;
	
	public Visitor(Item item, User user){
		this.item = item;
		this.user = user;
	}
	
}
