package analysis;

import java.util.ArrayList;
import java.util.List;

import models.SearchKey;

public class AnalysisResult {
	List<SearchKey> searchKeyList = new ArrayList<SearchKey>();
	
	public void addSearchKey(SearchKey searchKey){
		this.searchKeyList.add(searchKey);
	}
	
	public List<SearchKey> getSearchKeyList(){
		return searchKeyList;
	}
}
