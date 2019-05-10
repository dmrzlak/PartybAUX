package com.example.partybauxserver.songs;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The SongRepository class is an interface that autogenerates SQL calls to the Song SQL table
 */
@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {

	
	List<Song> findByPartyid(int partyid);
	
	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Song s WHERE s.partyid = :partyid and s.uri = :uri")
	boolean existsByPartyidAndUri(@Param("partyid") int partyid, @Param("uri")String uri);
	
	@Transactional
	void deleteByPartyidAndUri(int partyid, String uri);
	
	
}
