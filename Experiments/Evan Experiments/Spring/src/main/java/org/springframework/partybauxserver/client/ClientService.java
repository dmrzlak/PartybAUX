package org.springframework.partybauxserver.client;

import java.util.List;

public class ClientService {

	public static List<Client> getAllClientsService(ClientRepository cRepo) {

		List<Client> results = cRepo.findAll();
		return results;
	}

	public static int removeClientService(ClientRepository cRepo, int Client_id) {
		if (cRepo.existsById(Client_id)) {
			cRepo.deleteById(Client_id);
			return 0;
		} else {
			return 1;
		}
	}
	
	public static int addClientService(ClientRepository cRepo, int client_id, String username, String email, String password, int partyid, int usertype) {

		
		if(cRepo.existsByUsername(username) ) {return -2;}
		if(cRepo.existsByEmail(email)) { return -1; }
		 
		
		
		Client newClient = new Client();
		newClient.setClient_id(client_id);
		newClient.setUsername(username);
		newClient.setEmail(email);
		newClient.setPassword(password);
		newClient.setParty_id(partyid);
		newClient.setUserTypeid(usertype);
		cRepo.save(newClient);
		
		return 0;
	}

	

}
