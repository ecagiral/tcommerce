package models;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
	public Long twitterId;
	public String authToken;
	public String authTokenSecret;
	public String picture;
	public String fullName;
	public String description;
	public String webSite;
	public String location;
	@Enumerated(EnumType.STRING) 
	public AdsTweetLevel adsTweetLevel;
	public Date lastUsed = null;
	public Date lastAds = null;
	public Date firstLogin = null;
	public Date lastLogin = null;
	public Date lastResponded = null;
	public String backgroundImage = null;

	@OneToMany(cascade={CascadeType.ALL}, mappedBy="owner")
	public List<Item> items = new ArrayList<Item>();

	
	public User(twitter4j.User twUser){
		updateTwData(twUser,null,null);
		this.adsTweetLevel = AdsTweetLevel.NONE;
	}
	
	public User(twitter4j.User twUser, String authToken, String authTokenSecret){
		updateTwData(twUser, authToken, authTokenSecret);
		this.adsTweetLevel = AdsTweetLevel.NONE;
	}

	public static User findByEmailOrUsername(String emailOrUserName) {
		return User.find("email = ?1 or screenName = ?1", emailOrUserName)
				.first();
	}

	public static User findByTwitterId(Long twitterId) {
		return User.find("byTwitterId", twitterId).first();
	}

	public static User findLeastUsed(){
		return User.find("authToken is not null and authTokenSecret is not null order by lastUsed asc").first();
	}

	public void updateTwData(twitter4j.User twUser, String authToken,
			String authTokenSecret) {
		this.screenName = twUser.getScreenName();
		this.picture = twUser.getProfileImageURL().toExternalForm();
		this.fullName = twUser.getName();
		this.authToken = authToken;
		this.authTokenSecret = authTokenSecret;
		this.webSite = twUser.getURL() == null ? "" : twUser.getURL().toExternalForm();
		this.location = twUser.getLocation();
		this.description = twUser.getDescription();
		this.twitterId = twUser.getId();
		this.backgroundImage = twUser.getProfileBackgroundImageUrl() == null ? "http://si0.twimg.com/images/themes/theme1/bg.png" : twUser.getProfileBackgroundColor();
	}
}
