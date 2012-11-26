package models;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.bouncycastle.util.encoders.Base64;

import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.Codec;

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

	public UserType type;

	@OneToMany
	public List<Item> items;

	public User(String name, String email, String password) {
		this.screenName = name;
		this.email = email;
		this.password = password;
	}

	public User(String name, String authToken, String authTokenSecret,
			Long twitterId) {
		this.screenName = name;
		this.authToken = authToken;
		this.authTokenSecret = authTokenSecret;
		this.twitterId = twitterId;
	}

	public static User findByEmailOrUsername(String emailOrUserName) {
		return User.find("email = ?1 or screenName = ?1", emailOrUserName)
				.first();
	}

	public boolean checkPassword(String password) {
		String sha512 = sha512(password + salt);
		return this.password.equals(sha512);
	}

	public static User findByTwitterId(Long twitterId) {
		return User.find("byTwitterId", twitterId).first();
	}

	private static String sha512(String val) {
		String sha512 = null;
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-512");
			messageDigest.update(val.toString().getBytes("UTF-8"));
			sha512 = new String(Base64.encode(messageDigest.digest()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sha512;
	}

}
