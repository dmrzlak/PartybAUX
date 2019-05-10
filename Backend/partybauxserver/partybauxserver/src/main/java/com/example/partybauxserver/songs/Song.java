package com.example.partybauxserver.songs;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * SQL table for the songs in this application
 */
@Entity
@Table(name = "Songs")
public class Song {

	@Id
	@Column(name = "id")
	@NotFound(action = NotFoundAction.IGNORE)
	private Integer song_id;
	
	@Column(name = "uri")
	@NotFound(action = NotFoundAction.IGNORE)
	private String uri;
	
	@Column(name = "partyid")
	@NotFound(action = NotFoundAction.IGNORE)
	private Integer partyid;
	
	@Column(name = "time")
	@NotFound(action = NotFoundAction.IGNORE)
	private Long time;
	
	//Me love you long time
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Integer getSong_id() {
		return song_id;
	}
	
	public void setSong_id(Integer song_id) {
		this.song_id = song_id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Integer getPartyid() {
		return partyid;
	}

	public void setPartyid(Integer partyid) {
		this.partyid = partyid;
	}
	
		
	
	
	
}
