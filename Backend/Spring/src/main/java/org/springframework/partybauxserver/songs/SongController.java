package org.springframework.partybauxserver.songs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SongController {

	@Autowired
	SongRepository songRepo;
	
	@RequestMapping(method = RequestMethod.GET, path = "/refresh")
	public @ResponseBody List<String> refreshQueue(@RequestParam("partyid") int partyid){
		
		List<Song> entities;
		entities = songRepo.findByPartyid(partyid);
		
		List<String> uris = new ArrayList<String>();
		
		for(Song s : entities) {
			uris.add(s.getUri());
		}
		
		return uris;
		
		
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/addsong")
	public @ResponseBody int addSong(@RequestParam("partyid") int partyid, @RequestParam("uri") String uri) {
		
		Song s = new Song();
		s.setPartyid(partyid);
		s.setUri(uri);
		s.setSong_id((int)songRepo.count());
		songRepo.save(s);
		
		return 0;
	}
	
	
	
	
	
}
