package com.example.partybauxserver.follower;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.partybauxserver.client.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

// @author Marcin Lukanus

/**
 * The FollowerController class creates all the mapping requests that correlate
 * with functions that affect the Follower SQL table.
 */
@RestController
class FollowerController {
	
	@Autowired
	FollowerRepository followersRepository;
	
	@Autowired
	ClientRepository clientsRepository;
	
	// Show followers table
	/**
	 * Returns a list of all followers from Follower SQL table
	 * @return list of Follower objects from Follower SQL table
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/followers")
	public List<Follower> getAllFollowers() {
		return FollowerService.getAllFollowersService(followersRepository);
	}
	
	/**
	 * Returns a list of all followers of a particular Client from Follower SQL table
	 * @param username username of Client
	 * @return list of all followers of a particular Client from Follower SQL table
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/userfollowers")
	public @ResponseBody List<Follower> showFollowers(@RequestParam("username") String username) {
		return FollowerService.getFollowers(clientsRepository, followersRepository, username);
	}

	
	/**
	 * Creates a new entry in Follower SQL table showing a relation between two users
	 * @param userToFollow username of Client to be followed
	 * @param user username of Client that wants to follow userToFollow
	 * @return a string of all necessary information to create new entry in Follower SQL table
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/follow")
	public @ResponseBody String addNewFollower(@RequestParam("userToFollow") String userToFollow, 
												@RequestParam("user") String user) {
		
		return Integer.toString(FollowerService.addFollowerService(clientsRepository, followersRepository, (int) followersRepository.count(), userToFollow, user));
	}
	
	
	// Allow user to unfollow userToUnfollow
	/**
	 * Removes an entry in Follower SQL table by unfollowing a Client
	 * @param userToUnfollow username of Client to be unfollowed
	 * @param user username of Client wanting to unfollow userToUnfollow
	 * @return a string of all necessary information to remove existing entry in Follower SQL table
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/unfollow")
	public @ResponseBody String unfollow(@RequestParam("userToUnfollow") String userToUnfollow, 
											@RequestParam("user") String user) {
		
		return Integer.toString(FollowerService.deleteFollowerService(clientsRepository, followersRepository, userToUnfollow, user));
	}
	
	
}
