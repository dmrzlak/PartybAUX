package com.example.partybauxserver.follower;

import java.util.List;

import com.example.partybauxserver.client.*;

/**
 * The FollowerService class provides methods for use with the Follower SQL table
 */
public class FollowerService {
	
	/**
	 * Returns a list of all Follower entries from Follower SQL table
	 * @param fRepo table to pull all Followers from
	 * @return list of all Followers from Follower SQL table
	 */
	public static List<Follower> getAllFollowersService(FollowerRepository fRepo) {
		List<Follower> followers = fRepo.findAll();
		return followers;
	}
	
	/**
	 * Returns a list of all followers for a specific Client
	 * @param cRepo Client SQL table
	 * @param fRepo Follower SQL table
	 * @param username username of Client
	 * @return list of all followers for a specific Client
	 */
	public static List<Follower> getFollowers(ClientRepository cRepo, FollowerRepository fRepo, String username) {
		if(!cRepo.existsByUsername(username)) return null;
		
		Client c = cRepo.findByUsername(username);
		
		return fRepo.findByclientFK(c.getfollowerFK());
	}
	
	
	/**
	 * Removes an entry from the Follower SQL table
	 * @param cRepo Client SQL table
	 * @param fRepo Follower SQL table
	 * @param usernameToUnfollow username of Client to be unfollowed in Follower SQL table
	 * @param username username of Client wanting to unfollow usernameToUnfollow
	 * @return integer based on success or error of unfollowing
	 */
	public static int deleteFollowerService(ClientRepository cRepo, FollowerRepository fRepo, String usernameToUnfollow, String username) {
		if(!cRepo.existsByUsername(username) || !cRepo.existsByUsername(usernameToUnfollow)) 
			return -2; 
		
		Client c = cRepo.findByUsername(usernameToUnfollow);
		
		if(fRepo.existsByclientFKAndUsername(c.getfollowerFK(), username)) {
			Follower f = fRepo.findByclientFKAndUsername(c.getfollowerFK(), username);
			fRepo.deleteById(f.getFollower_id());
		} else {
			return -1;
		}
		
		return 0;
	}
	

	/**
	 * Adds an entry to the Follower SQL table
	 * @param cRepo Client SQL table
	 * @param fRepo Follower SQL table
	 * @param follower_id ID in Follower SQL table, increments based on amount of entries in Follower SQL table
	 * @param usernameToFollow username of Client to be followed
	 * @param username username of Client wanting to follow
	 * @return integer based on success or error of following
	 */
	public static int addFollowerService(ClientRepository cRepo, FollowerRepository fRepo, int follower_id, String usernameToFollow, String username) {
		if(!cRepo.existsByUsername(username) || !cRepo.existsByUsername(usernameToFollow)) 
			return -2;
		
		if(usernameToFollow.equals(username)) {
			return -1;
		}
		
		Follower newFollower = new Follower();
		newFollower.setFollower_id(follower_id);
		
		Client c = cRepo.findByUsername(usernameToFollow);
		newFollower.setclientFK(c.getfollowerFK());
		
		newFollower.setUsername(username);
		
		fRepo.save(newFollower);
		return 0;
	}
}
