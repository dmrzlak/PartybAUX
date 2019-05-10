package org.springframework.partybauxserver.songs;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {

	
	List<Song> findByPartyid(int partyid);
	
	
	
}
