package com.example.partybauxserver.party;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The PartyRepository class is an interface that autogenerates SQL calls to the Party SQL table
 */
@Repository
public interface PartyRepository extends JpaRepository<Party, Integer> {

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Party p WHERE p.hostname = :hostname")
	boolean existsByHostname(@Param ("hostname") String hostname);
	
	Party findByHostname(String hostname);
	
}
