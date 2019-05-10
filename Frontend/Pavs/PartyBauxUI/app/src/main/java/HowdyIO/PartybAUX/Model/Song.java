package HowdyIO.PartybAUX.Model;

import android.support.annotation.NonNull;

/**
 * Created by Chris on 4/25/2019.
 */

public class Song implements Comparable<Song>{
    private int song_id, party_id;
    private String uri;
    private long time;

    public int getSong_id() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }

    public int getParty_id() {
        return party_id;
    }

    public void setParty_id(int party_id) {
        this.party_id = party_id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int compareTo(@NonNull Song song) {
        if(getTime() < song.getTime())
            return -1;
        if(getTime() > song.getTime())
            return 1;

        return 0;
    }
}
