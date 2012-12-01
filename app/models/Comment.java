package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Comment extends Model{
	
	public String commentText;
	
	@ManyToOne
	public User owner;
	
	@ManyToOne
	public Item item;
	
	public Comment(User owner, String text,Item item){
		this.item = item;
		this.owner = owner;
		this.commentText = text;
	}

}
