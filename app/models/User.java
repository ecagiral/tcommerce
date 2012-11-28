package models;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.bouncycastle.util.encoders.Base64;

import play.data.validation.Required;
import play.db.jpa.Model;
import util.Codec;

@Entity
public class User extends Model {

	public String screenName;

	public String email;
	public String password;

	public Long twitterId;
	public String authToken;
	public String authTokenSecret;

	public String picture;

	public String fullName;
	public String salt;
	
	@Enumerated(EnumType.STRING) 
	public UserType type;
	
	public String description;
	public String webSite;
	public String location;
	
	@Enumerated(EnumType.STRING) 
	public AdsTweetLevel adsTweetLevel;
	

	@OneToMany
	public List<Item> items;

	
	public User(twitter4j.User twUser, String authToken, String authTokenSecret){
		updateTwData(twUser, authToken, authTokenSecret);
		this.adsTweetLevel = AdsTweetLevel.NONE;
	}

	public User(String email, String password) {
		this.email = email;
		this.password = password;
		this.salt = Codec.sha512_64(String.valueOf(Calendar.getInstance().getTimeInMillis()) + String.valueOf(Math.random()));
		this.type = UserType.NATIVE;
		this.adsTweetLevel = AdsTweetLevel.NONE;
	}

	public static User findByEmailOrUsername(String emailOrUserName) {
		return User.find("email = ?1 or screenName = ?1", emailOrUserName)
				.first();
	}

	public boolean checkPassword(String password) {
		String sha512 = Codec.sha512_64(password + salt);
		return this.password.equals(sha512);
	}

	public static User findByTwitterId(Long twitterId) {
		return User.find("byTwitterId", twitterId).first();
	}



	public void updateTwData(twitter4j.User twUser, String authToken,
			String authTokenSecret) {
		this.screenName = twUser.getScreenName();
		this.picture = twUser.getProfileImageURL().toExternalForm();
		this.fullName = twUser.getName();
		this.authToken = authToken;
		this.authTokenSecret = authTokenSecret;
		this.type = UserType.TWITTER;
	}
}
