package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.db.jpa.Model;

@Entity @Table(uniqueConstraints = {@UniqueConstraint(columnNames={"tweetId"})})
public class Tweet extends Model{
	
	public long tweetId;
	public String tweet;
	@ManyToOne
	public User owner;
	public boolean responded;
	@ManyToOne
	public User respondedBy;
	@OneToOne
	public SearchKey searchKey;
}
