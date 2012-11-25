package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.Codec;

@Entity
public class User extends Model{
	
	@Required
    public String screenName;

    public String email;
	@Required
    public String password;
	
	public Long twitterId;
	public String authToken;
	public String authTokenSecret;
	
	public String picture;
	
	@OneToMany
	public List<Item> items; 
	
	public User(String name,String email,String password){
		this.screenName = name;
		this.email = email;
		this.password = password;
	}
	
	public User(String name,String authToken, String authTokenSecret, Long twitterId){
		this.screenName = name;
		this.authToken = authToken;
		this.authTokenSecret = authTokenSecret;
		this.twitterId = twitterId;
	}
	
	public static User findByEmailOrUsername(String emailOrUserName) {
		return User.find("email = ?1 or screenName = ?1",emailOrUserName).first();
	}
	
	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}
	
	public static User findByTwitterId(Long twitterId){
		return User.find("byTwitterId", twitterId).first();
	}

}
