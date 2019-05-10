package com.example.partybauxserver.songs;

import java.util.Comparator;

public class SongCompare implements Comparator<Song>{

	SongCompare(){
		
	}
	
	
	@Override
	public int compare(Song s1, Song s2) {
		
		return s1.getSong_id() - s2.getSong_id();
	}

	
	
	
}
