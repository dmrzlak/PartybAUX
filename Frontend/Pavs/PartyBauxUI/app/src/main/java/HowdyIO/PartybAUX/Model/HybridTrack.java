package HowdyIO.PartybAUX.Model;

import com.spotify.protocol.types.Artist;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;

/**
 * Created by Chris on 4/9/2019.
 */

public class HybridTrack {
    public String name;
    public ArrayList<HybridArtist> artists;
    public HybridAlbum album;
    public String uri;
    public long duration;

    public HybridTrack(kaaes.spotify.webapi.android.models.Track kaaesTrack){
        this.name = kaaesTrack.name;
        this.artists = generateArtists(kaaesTrack.artists, ArtistSimple.class.toString());
        this.album = new HybridAlbum(kaaesTrack.album);
        this.uri = kaaesTrack.uri;
        this.duration = kaaesTrack.duration_ms;
    }

    public HybridTrack(com.spotify.protocol.types.Track spotifyTrack){
        this.name = spotifyTrack.name;
        this.artists = generateArtists(spotifyTrack.artists, Artist.class.toString());
        this.album = new HybridAlbum(spotifyTrack.album);
        this.uri = spotifyTrack.uri;
        this.duration = spotifyTrack.duration;
        //this. = spotifyTrack.imageUri;
    }

    private ArrayList<HybridArtist> generateArtists(Object object, String className){
        if( !(object instanceof List) || ((List<Object>) object).isEmpty()) return new ArrayList<>();

        if(className.equals(kaaes.spotify.webapi.android.models.ArtistSimple.class.toString())) {
            ArrayList<HybridArtist> temp = new ArrayList<>();
            for (ArtistSimple artist : (List<ArtistSimple>) object)
                temp.add(new HybridArtist(artist));
            return temp;
        }else{
            ArrayList<HybridArtist> temp = new ArrayList<>();
            for (Artist artist : (List<Artist>) object)
                temp.add(new HybridArtist(artist));
            return temp;
        }
    }

    public class HybridAlbum{
        public String name;
        public String uri;

        public HybridAlbum(kaaes.spotify.webapi.android.models.AlbumSimple kaaesAlbum){
            this.name = kaaesAlbum.name;
            this.uri = kaaesAlbum.uri;
        }

        public HybridAlbum(com.spotify.protocol.types.Album spotifyAlbum){
            this.name = spotifyAlbum.name;
            this.uri = spotifyAlbum.uri;
        }
    }

    public class HybridArtist{
        public String name;
        public String uri;

        public HybridArtist(kaaes.spotify.webapi.android.models.ArtistSimple kaaesArtist){
            this.name = kaaesArtist.name;
            this.uri = kaaesArtist.uri;
        }

        public HybridArtist(com.spotify.protocol.types.Artist spotifyArtist){
            this.name = spotifyArtist.name;
            this.uri = spotifyArtist.uri;
        }
    }
}
