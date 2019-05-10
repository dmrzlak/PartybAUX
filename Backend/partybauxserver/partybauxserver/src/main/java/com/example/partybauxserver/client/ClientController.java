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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * The ClientController class creates all the mapping requests that correlate
 * with functions that affect the Client SQL table.
 */
@Controller
@ResponseBody
class ClientController {

	@Autowired
	ClientRepository clientsRepository;
	
	@Autowired
    private JavaMailSender sender;
	/**
	 * Removes a client from the Client SQL table
	 * @param Client_id ID of client from Client SQL table to be removed
	 * @return integer based on success or error of removal of client
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/removeclient")
	public @ResponseBody int removeUser(@RequestParam("id") int Client_id) {

		return ClientService.removeClientService(clientsRepository, Client_id);

	}
	

	/**
	 * Adds a new client to the Client SQL table
	 * @param username username of user
	 * @param email email of user
	 * @param password password of user
	 * @param partyid partyid of user, defaults to -1
	 * @param usertype usertype of user, defaults to 0
	 * @return integer based on success or error of adding client
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/signup")
	public @ResponseBody String addNewUser(@RequestParam("username") String username,
			@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("partyid") int partyid, @RequestParam("usertype") int usertype) {
			//Set PID In app
		System.out.println("Print Works");
		
		int c = ClientService.addClientService(clientsRepository, (int) clientsRepository.count(), username, email, password, partyid, usertype);
		if( 0 == c ) {
			return Integer.toString(ClientService.emailClientService(email, username, sender));
		}
		return Integer.toString(c);
		

	}
	
	/**
	 * Used for finding a client by username in Client SQL table
	 * @param username username of client to be found
	 * @return Client object of given client username
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/get/user")
	public @ResponseBody Client getUser(@RequestParam("username") String username) {
		Client c = clientsRepository.findByUsername(username);
		return c;
	}

	/**
	 * Allows for a user to login to the app
	 * @param Username username of the client
	 * @param Password password of the client
	 * @return integer based on success or error of logging in
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/login")
	public @ResponseBody int login(@RequestParam("username") String Username,
			@RequestParam("password") String Password) {
		Client c = clientsRepository.findByUsername(Username);
		if (c == null) {
			return -2;
		}
		if (!c.getPassword().contentEquals(Password)) {
			return -1;
		}
		return c.getClient_id();
	}



}
