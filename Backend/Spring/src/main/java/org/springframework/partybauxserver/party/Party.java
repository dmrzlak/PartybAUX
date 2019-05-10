package org.springframework.partybauxserver.party;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Table(name = "Parties")
public class Party {
	@Id
	@Column(name = "partyid")
	@NotFound(action = NotFoundAction.IGNORE)
	private Integer partyid;
	
	@Column(name = "hostname")
	@NotFound(action = NotFoundAction.IGNORE)
	private String hostname;

	public Integer getPartyid() {
		return partyid;
	}

	public void setPartyid(Integer partyid) {
		this.partyid = partyid;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}


	
}
