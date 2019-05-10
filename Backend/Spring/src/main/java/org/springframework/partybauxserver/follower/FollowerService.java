package org.springframework.partybauxserver.follower;

import java.util.List;

import org.springframework.partybauxserver.client.Client;
import org.springframework.partybauxserver.client.ClientRepository;

public class FollowerService {
	
	public static List<Follower> getAllFollowersService(FollowerRepository fRepo) {
		List<Follower> followers = fRepo.findAll();
		return followers;
	}
	
	public static List<Follower> getFollowers(ClientRepository cRepo, FollowerRepository fRepo, String username) {
		if(!cRepo.existsByUsername(username)) return null;
		
		Client c = cRepo.findByUsername(username);
		
		return fRepo.findByclientFK(c.getfollowerFK());
	}
	
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
