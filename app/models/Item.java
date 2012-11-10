package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Item extends Model{
	
	@Required
	public String twit;
	
	@Required
	public String picture;

}
