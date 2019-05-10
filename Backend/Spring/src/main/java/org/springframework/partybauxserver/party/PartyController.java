package org.springframework.partybauxserver.party;

import org.springframework.partybauxserver.client.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PartyController {
	
	@Autowired
	PartyRepository partyrepo;
	
	@Autowired
	ClientRepository clientrepo;
	
	@RequestMapping(method = RequestMethod.GET, path = "/create")
	public @ResponseBody int create(@RequestParam("hostname") String hostname) {
		if(partyrepo.existsByHostname(hostname)) {
			return -1;
		}
		else {
			Party p  = new Party();
			p.setHostname(hostname);
			p.setPartyid((int) partyrepo.count());
			partyrepo.save(p);
			return 0;
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/join")
	public @ResponseBody int join(@RequestParam("username") String username) {
		if(partyrepo.existsByHostname(username)) {
			Party p = partyrepo.findByHostname(username);
			return p.getPartyid();
		}
		else {return -1;} //NO party avaliable
		
		
	}
	
	
	
	
	
	
	

}
