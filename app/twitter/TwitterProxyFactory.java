package twitter;

import models.User;

public class TwitterProxyFactory {
	public static TwitterProxy newInstance(User user){
		return new TwitterProxyImpl(user);
	}
}
