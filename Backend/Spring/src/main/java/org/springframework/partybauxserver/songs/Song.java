package org.springframework.partybauxserver.songs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
