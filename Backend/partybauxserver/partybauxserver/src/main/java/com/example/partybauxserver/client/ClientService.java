package com.example.partybauxserver.client;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * The ClientService class provides methods for use with the Client SQL table
 */
public class ClientService {

	/**
	 * Returns a list of all clients from Client SQL table
	 * @param cRepo table to search through
	 * @return list of Client objects from Client SQL table
	 */
	public static List<Client> getAllClientsService(ClientRepository cRepo) {

		List<Client> results = cRepo.findAll();
		return results;
	}

	/**
	 * Removes a client from the Client SQL table
	 * @param cRepo table to search through
	 * @param Client_id id of client in Client SQL table
	 * @return integer based on success or error in removing a client
	 */
	public static int removeClientService(ClientRepository cRepo, int Client_id) {
		if (cRepo.existsById(Client_id)) {
			cRepo.deleteById(Client_id);
			return 0;
		} else {
			return -1;
		}
	}
	
	/**
	 * Creates a new Client object and adds it to Client SQL table
	 * @param cRepo table to add client in
	 * @param client_id id of client
	 * @param username username of client
	 * @param email email of client
	 * @param password password of client
	 * @param partyid partyid of client, defaults to -1
	 * @param usertype usertype of client, defaults to 0
	 * @return integer based on success or error of adding new client to SQL table
	 */
	public static int addClientService(ClientRepository cRepo, int client_id, String username, String email, String password, int partyid, int usertype) {

		
		if(cRepo.existsByUsername(username) ) {return -2;}
		if(cRepo.existsByEmail(email)) { return -1; }
		 
		
		
		Client newClient = new Client();
		newClient.setClient_id(client_id);
		newClient.setUsername(username);
		newClient.setEmail(email);
		newClient.setPassword(password);
		newClient.setPartyid(partyid);
		newClient.setUserTypeid(usertype);
		newClient.setfollowerFK(client_id);
		cRepo.save(newClient);
		
		return 0;
	}
	
	public static int emailClientService(String email, String username, JavaMailSender sender) {
		 MimeMessage message = sender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message);
	        try {
	            helper.setTo(email);
	            helper.setText("Thanks for signing up! We appreciate you signing up for the service! Here is your username: " + username);
	            helper.setSubject("Welcome to PartybAUX");
	            
	        } catch (MessagingException e) {
	            e.printStackTrace();
	            return -1; //Email Jank
	        }
	        sender.send(message);
	        return 0;
	}

	

}
