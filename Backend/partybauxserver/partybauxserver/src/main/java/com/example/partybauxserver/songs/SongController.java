package com.example.partybauxserver.songs;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
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

import com.example.partybauxserver.client.Client;
import com.example.partybauxserver.client.ClientRepository;
import com.example.partybauxserver.party.PartyRepository;

/**
 * The SongController class creates all the mapping requests that correlate
 * with functions that affect the Song SQL table.
 */
@RestController
public class SongController {

	@Autowired
	PartyRepository partyRepo;
	@Autowired
	ClientRepository clientRepo;
	@Autowired
	SongRepository songRepo;
	
	int songcount = 0;
	
	
	
	/**
	 * Removes a song from a party's queue
	 * @param partyid id of party in Party SQL table
	 * @param uri song id from Spotify
	 * @return integer based on success or error of removing song from Song SQL table
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/remove/song")
	public @ResponseBody int removeSong(@RequestParam("partyid") int partyid, @RequestParam("uri") String uri) {
		
		if(songRepo.existsByPartyidAndUri(partyid, uri)) {
			songRepo.deleteByPartyidAndUri(partyid, uri);
			return 0;
		}
			//Song Not in DB
			return -6;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/remove/songs")
	public @ResponseBody int removeSongs(@RequestParam("partyid") int partyid, @RequestParam String songs) {

		String[] uriList = songs.split(",");
		for (int i = 0; i < uriList.length; i++) {

			if (songRepo.existsByPartyidAndUri(partyid, uriList[i])) {
				System.out.println("Song Deleted: " + uriList[i]);
				songRepo.deleteByPartyidAndUri(partyid, uriList[i]);
			}
		}

		return 0;
	}
	
	/**
	 * Adds song to party's queue
	 * @param partyid id of party in Party SQL table
	 * @param uri song id from Spotify
	 * @return integer based on success or error of adding song to Song SQL table
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/addsong")
	public @ResponseBody int addSong(@RequestParam("partyid") int partyid, @RequestParam("uri") String uri) {
		
		Song s = new Song();
		s.setPartyid(partyid);
		s.setUri(uri);
		s.setSong_id(songcount++);
		s.setTime(System.currentTimeMillis());
		songRepo.save(s);
		
		return 0;
	}

	/**
	 * This message mapping sends an update object called PartyBox to all the
	 * current clients listening at the topic/party/pid subscription point This
	 * Object contains a real-time updated list of all the songs and members of a
	 * given party.
	 * 
	 * @param pid id of party to select
	 * @return PartyBox object containing party's members and songs
	 */
	@MessageMapping("/update/{pid}")
	@SendTo("/topic/party/{pid}")
	public PartyBox update(@DestinationVariable int pid) {
		PartyBox pb = new PartyBox();
		ArrayList<Song> songs = new ArrayList<Song>();
		List<Song> array = songRepo.findByPartyid(pid);

		for (Song s : array) {
			songs.add(s);
		}

		pb.setSongs(songs);
		List<Client> members = clientRepo.findByPartyid(pid);
		ArrayList<ClientSimple> clientInfo;
		clientInfo = new ArrayList<ClientSimple>();
		for (Client c : members) {
			ClientSimple cs = new ClientSimple();
			cs.setUsername(c.getUsername());
			cs.setUserType(c.getUserTypeid());
			System.out.println("Username : " + c.getUsername());
			System.out.println("UserType : " + c.getUserTypeid());
			clientInfo.add(cs);
		}

		pb.setPartyMembers(clientInfo);


		return pb;
	}

}
