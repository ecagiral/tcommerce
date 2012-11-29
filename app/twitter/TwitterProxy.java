package twitter;

import java.util.List;

import models.SearchKey;
import models.User;

public interface TwitterProxy {
	public void search(SearchKey searchKey);
	public void addFriends();
}
