package models;

public enum AdsTweetLevel {
	NONE(-1), FIRST(1), SECOND(2), THIRD(3), FOURTH(4);
	int level;
	AdsTweetLevel(int level){
		this.level = level;
	}
	public int getLevel(){return level;}
}
