package com.example.partybauxserver.party;

import com.example.partybauxserver.client.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The PartyController class creates all the mapping requests that correlate
 * with functions that affect the Party SQL table.
 */
@RestController
public class PartyController {

	@Autowired
	PartyRepository partyrepo;

	@Autowired
	ClientRepository clientrepo;

	int index = 0;

	/**
	 * Creates a new entry in Party SQL table
	 * @param hostname username of Client creating a party
	 * @return integer based on success or error of adding new entry in Party SQL table
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/create")
	public @ResponseBody int create(@RequestParam("hostname") String hostname) {
		if (partyrepo.existsByHostname(hostname)) {
			return partyrepo.findByHostname(hostname).getPartyid();
		} else if (!clientrepo.existsByUsername(hostname)) {
			// Host DNE
			return -3;
		} else {
			Party p = new Party();
			p.setHostname(hostname);
			p.setPartyid(index++);
			partyrepo.save(p);
			Client c = clientrepo.findByUsername(hostname);
			c.setPartyid(p.getPartyid());
			c.setUserTypeid(2); // Establishes client as host
			clientrepo.save(c);
			return p.getPartyid();
		}
	}

	/**
	 * Adds a Client into a specific Party
	 * @param hostname username of Client that is hosting the party
	 * @param username username of Client that is joining the party
	 * @return integer based on success or error of Client joining the party
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/join")
	public @ResponseBody int join(@RequestParam("hostname") String hostname,
			@RequestParam("username") String username) {
		
		if (hostname.equals(username)) {
			Client c = clientrepo.findByUsername(username);
			Party p = partyrepo.findByHostname(hostname);
			c.setUserTypeid(2);
			c.setPartyid(p.getPartyid());
			clientrepo.save(c);
			return p.getPartyid();
		}
		if (partyrepo.existsByHostname(hostname)) {
			Party p = partyrepo.findByHostname(hostname);
			if (clientrepo.existsByUsername(username)) {
				Client c = clientrepo.findByUsername(username);
				c.setPartyid(p.getPartyid());
				clientrepo.save(c);
				return p.getPartyid();
			} else {
				// Invalid Username
				return -2;
			}
		} else {
			// Invalid Hostname || Party Not avaliable
			return -3;
		}

	}

	/**
	 * Modifies Client's partyID when exiting party
	 * @param username username of Client exiting party
	 * @return integer based on success or error of exiting party
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/exit")
	public @ResponseBody int exit(@RequestParam("username") String username) {

		if (!clientrepo.existsByUsername(username)) {
			// Invalid Username
			return -2;
		}

		// Host Called exit teardown party
		if (partyrepo.existsByHostname(username)) {
			int partyid = clientrepo.findByUsername(username).getPartyid();
			List<Client> partymembers = clientrepo.findByPartyid(partyid);
			for (Client c : partymembers) {
				c.setPartyid(-1);
				c.setUserTypeid(0);
				clientrepo.save(c);
			}
			partyrepo.deleteById(partyid);
			return 0;
		}
		
		// Member called exit
		else {
			Client c = clientrepo.findByUsername(username);
			c.setPartyid(-1);
			c.setUserTypeid(0);
			clientrepo.save(c);
			return 0;
		}

	}

	
	// Host/DJ(Bouncer) functionalities below this comment
	
	// (Hosts can kick DJs and Guests, DJs can kick Guests)
	/**
	 * Kicks a Client from a party
	 * @param user username of Client that is kicking
	 * @param kick username of Client to be kicked
	 * @return integer based on success or error of Client being kicked from party
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/kick")
	public @ResponseBody int kick(@RequestParam("user") String user, @RequestParam("kick") String kick) {

		// Nonexistant users
		if (!clientrepo.existsByUsername(user) || !clientrepo.existsByUsername(kick)) {
			return -2;
		}

		Client u = clientrepo.findByUsername(user);
		Client k = clientrepo.findByUsername(kick);

		// Users in different parties
		if (u.getPartyid() != k.getPartyid()) {
			return -6; // Error number can be changed if needed
		}

		// User doesn't have power to kick
		if (u.getUserTypeid() <= k.getUserTypeid()) {
			return -7; // Error number can be changed if needed
		}

		k.setPartyid(-1);
		k.setUserTypeid(0);
		clientrepo.save(k);

		return 0;

	}

	// Change userTypeids of users
	/**
	 * Modifies a Client's userTypeid in order to change their power/role within a party
	 * @param user username of Client that is changing another Client's userTypeid
	 * @param change username of Client that is having their userTypeid changed
	 * @param id new value for change's userTypeid
	 * @return integer based on success or error of changing a Client's userTypeid
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/change")
	public @ResponseBody int change(@RequestParam("user") String user, @RequestParam("change") String change,
			@RequestParam("id") int id) {

		// Nonexistant users
		if (!clientrepo.existsByUsername(user) || !clientrepo.existsByUsername(change)) {
			return -2;
		}

		Client u = clientrepo.findByUsername(user);
		Client c = clientrepo.findByUsername(change);

		// Users in different parties
		if (u.getPartyid() != c.getPartyid()) {
			return -6; // Error number can be changed if needed
		}

		// User isn't host of party (can't change others' userTypeids)
		if (u.getUserTypeid() != 2) {
			return -7; // Error number can be changed if needed
		}

		// Change already has same userTypeid as new id
		if (c.getUserTypeid() == id) {
			return 0; // Nothing actually changed
		}

		c.setUserTypeid(id);
		clientrepo.save(c);

		return 0;

	}

}
