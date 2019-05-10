package org.springframework.partybauxserver.follower;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.core.style.ToStringCreator;
import org.springframework.partybauxserver.client.Client;

// @author Marcin Lukanus

@Entity
@Table(name = "Followers")
public class Follower {
	
	@Id
	@Column(name = "id")
	@NotFound(action = NotFoundAction.IGNORE)
	private Integer follower_id;
	
	@Column(name = "clientFK")
	@NotFound(action = NotFoundAction.IGNORE)
	private Integer clientFK;	
	
	@Column(name = "username")
	@NotFound(action = NotFoundAction.IGNORE)
	private String username;
	
	public Integer getFollower_id() {
		return follower_id;
	}
	
	public void setFollower_id(Integer follower_id) {
		this.follower_id = follower_id;
	}
	
	public Integer getclientFK() {
		return clientFK;
	}
	
	public void setclientFK(int followerFK) {
		clientFK = followerFK;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String toString() {
		return new ToStringCreator(this)
				.append("id", this.getFollower_id()).append("username", this.getUsername()).toString();
	}
	
}
