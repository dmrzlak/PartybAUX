/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.partybauxserver.client;

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

/**
 * SQL table for the clients (aka "users") in this application
 */
@Entity
@Table(name = "Clients")
public class Client {
	@Id
	@Column(name = "id")
	@NotFound(action = NotFoundAction.IGNORE)
	private Integer Client_id;
	
	@Column(name = "followerFK")
	@NotFound(action = NotFoundAction.IGNORE)
	private Integer followerFK;

	@Column(name = "username")
	@NotFound(action = NotFoundAction.IGNORE)
	private String username;

	@Column(name = "email")
	@NotFound(action = NotFoundAction.IGNORE)
	private String email;

	@Column(name = "password")
	@NotFound(action = NotFoundAction.IGNORE)
	private String password;

	@Column(name = "partyid")
	@NotFound(action = NotFoundAction.IGNORE)
	private Integer partyid;

	@Column(name = "userType")
	@NotFound(action = NotFoundAction.IGNORE)
	private Integer userTypeid;

	public Integer getClient_id() {
		return Client_id;
	}

	public void setClient_id(Integer client_id) {
		Client_id = client_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public Integer getfollowerFK() {
		return followerFK;
	}

	public void setfollowerFK(Integer followerFK) {
		this.followerFK = followerFK;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getPartyid() {
		return partyid;
	}

	public void setPartyid(Integer partyid) {
		this.partyid = partyid;
	}

	public Integer getUserTypeid() {
		return userTypeid;
	}

	public void setUserTypeid(Integer userTypeid) {
		this.userTypeid = userTypeid;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this)

				.append("id", this.getClient_id()).append("userName", this.getUsername())
				.append("firstName", this.getEmail()).append("password", this.getPassword())
				.append("partyid", this.getPartyid()).append("usertype", this.getUserTypeid()).append("folowerfk", this.getfollowerFK()).toString();
	}
}
