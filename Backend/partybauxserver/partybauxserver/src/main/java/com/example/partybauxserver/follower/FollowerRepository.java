package com.example.partybauxserver.follower;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// @author Marcin Lukanus

/**
 * The FollowerRepository class is an interface that autogenerates SQL calls to the Follower SQL table
 */
public interface FollowerRepository extends JpaRepository<Follower, Integer> {

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Client c WHERE c.username = :username")
	boolean existsByUsername(@Param("username") String username);
	
	Follower findByUsername(String username);
	
	List<Follower> findByclientFK(int fk);
	
	boolean existsByclientFKAndUsername(int clientFK, String username);
	
	Follower findByclientFKAndUsername(int clientFK, String username);
}
