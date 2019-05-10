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
package org.springframework.partybauxserver.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.partybauxserver.client.Client;
import org.springframework.partybauxserver.client.ClientRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @ModifiedBy Tanmay Ghosh
 */
@RestController
class ClientController {

	@Autowired
	ClientRepository clientsRepository;


	@RequestMapping(method = RequestMethod.GET, path = "/clients")
	public List<Client> getAllClients() {

		return ClientService.getAllClientsService(clientsRepository);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/removeclient")
	public @ResponseBody int removeUser(@RequestParam("id") int Client_id) {

		return ClientService.removeClientService(clientsRepository, Client_id);

	}

	@RequestMapping(method = RequestMethod.GET, path = "/signup")
	public @ResponseBody String addNewUser(@RequestParam("username") String username, @RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("partyid") int partyid,
			@RequestParam("usertype") int usertype) {

		return Integer.toString(ClientService.addClientService(clientsRepository, (int) clientsRepository.count(), (int) clientsRepository.count(), username, email,
				password, partyid, usertype));

	}
	@RequestMapping(method = RequestMethod.GET, path = "/login")
	public @ResponseBody int login(@RequestParam("username") String Username, @RequestParam("password") String Password) {
		Client c = clientsRepository.findByUsername(Username);
		if(c == null) {
			return -2;
		}
		if(!c.getPassword().contentEquals(Password)) {
			return -1;
		}
		return c.getClient_id();
	}

}
