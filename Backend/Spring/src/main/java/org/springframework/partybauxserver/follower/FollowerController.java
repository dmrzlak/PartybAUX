package org.springframework.partybauxserver.follower;

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

// @author Marcin Lukanus

@RestController
class FollowerController {
	
	@Autowired
	FollowerRepository followersRepository;
	
	@Autowired
	ClientRepository clientsRepository;
	
	// Show followers table
	@RequestMapping(method = RequestMethod.GET, path = "/followers")
	public List<Follower> getAllFollowers() {
		return FollowerService.getAllFollowersService(followersRepository);
	}
	
	// Return certain user's followers
	@RequestMapping(method = RequestMethod.GET, path = "/userfollowers")
	public @ResponseBody List<Follower> showFollowers(@RequestParam("username") String username) {
		return FollowerService.getFollowers(clientsRepository, followersRepository, username);
	}

	
	// Allow user to follow userToFollow
	@RequestMapping(method = RequestMethod.GET, path = "/follow")
	public @ResponseBody String addNewFollower(@RequestParam("userToFollow") String userToFollow, 
												@RequestParam("user") String user) {
		
		return Integer.toString(FollowerService.addFollowerService(clientsRepository, followersRepository, (int) followersRepository.count(), userToFollow, user));
	}
	
	// TODO
	// Allow user to unfollow userToUnfollow
	@RequestMapping(method = RequestMethod.GET, path = "/unfollow")
	public @ResponseBody String unfollow(@RequestParam("userToUnfollow") String userToUnfollow, 
											@RequestParam("user") String user) {
		
		return Integer.toString(FollowerService.deleteFollowerService(clientsRepository, followersRepository, userToUnfollow, user));
	}
	
	
}
