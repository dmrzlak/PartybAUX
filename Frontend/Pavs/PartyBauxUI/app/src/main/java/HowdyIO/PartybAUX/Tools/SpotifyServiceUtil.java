package HowdyIO.PartybAUX.Tools;

import android.util.Log;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.Empty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import HowdyIO.PartybAUX.Model.SpotifyPlayer;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.FeaturedPlaylists;
import kaaes.spotify.webapi.android.models.NewReleases;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.QueryMap;

/**
 * Created by Chris on 2/26/2019.
 */

public class SpotifyServiceUtil {

    private static SpotifyServiceUtil spotifyServiceUtil;

    private static SpotifyService spotifyService;
    private static SpotifyAppRemote spotifyAppRemote;

    private static String token;

    public static void setSpotifyService(SpotifyService spotifyService, SpotifyAppRemote spotifyAppRemote, String token){
        SpotifyServiceUtil.spotifyService = spotifyService;
        SpotifyServiceUtil.spotifyAppRemote = spotifyAppRemote;
        SpotifyServiceUtil.token = token;
        SpotifyPlayer.setSpotifyAppRemote(spotifyAppRemote);
    }

    public static SpotifyServiceUtil getInstance(){
        return spotifyServiceUtil == null? spotifyServiceUtil = new SpotifyServiceUtil(): spotifyServiceUtil;
    }

    private SpotifyServiceUtil(){

    }

    public void searchTracks(String query, @QueryMap Map<String, Object> options, Callback<TracksPager> callback){
        spotifyService.searchTracks(query, options, callback);
    }


    public void getTrack(String trackID, Callback<Track> callback) {
        spotifyService.getTrack(trackID, callback);
    }

    public void getTracks(ArrayList<String> trackIDList, Callback<Tracks> callback) {
        StringBuilder trackIDs = new StringBuilder();
        for (int i = 0; i < trackIDList.size(); i++){
            trackIDs.append(trackIDList.get(i));
            if(i < trackIDList.size() - 1)trackIDs.append(",");
        }
        spotifyService.getTracks(trackIDs.toString(), callback);
    }

    public void getNewReleases(Callback<NewReleases> newReleasesCallback){
        spotifyService.getNewReleases(newReleasesCallback);
    }

    private ArrayList<List<Track>> results = new ArrayList<>();
    public void getTracksFromAlbums(ArrayList<AlbumSimple> albums, Callback<ArrayList<List<Track>>> callback) {
        results = new ArrayList<>();
        ArrayList<AlbumSimple> tempAlbums = new ArrayList<>(albums);
        patientLoadTracks(tempAlbums, callback);
    }

    private void patientLoadTracks(final ArrayList<AlbumSimple> albums, final Callback<ArrayList<List<Track>>> callback){
        if(albums.size() > 0){
            filterAlbum(albums.get(albums.size() - 1).id, new Callback<Pager<Track>>() {
                @Override
                public void success(Pager<Track> trackPager, Response response) {
                    results.add(0, trackPager.items);
                    tryNext(response);
                }

                @Override
                public void failure(RetrofitError error) {
                    tryNext(null);
                }

                private void tryNext(Response response) {
                    if(albums.size() > 1) {
                        albums.remove(albums.size() - 1);
                        patientLoadTracks(albums, callback);
                    }else
                        callback.success(results, response);

                }
            });
        }
    }

    private void filterAlbum(String albumID, Callback<Pager<Track>> callback){
        spotifyService.getAlbumTracks(albumID, callback);
    }

    public void getFeaturedPlaylists(Callback<FeaturedPlaylists> callback){
        spotifyService.getFeaturedPlaylists(callback);
    }

    public void getTracksFromPlaylist(PlaylistSimple playlist, Callback<Pager<PlaylistTrack>> callback) {
        spotifyService.getPlaylistTracks(playlist.owner.id, playlist.id, callback);
    }

    public void playTrack(Track track) {
        spotifyAppRemote.getPlayerApi()
                .play(track.uri)
                .setResultCallback(empty -> Log.e("Play successful", "wow"))
                .setErrorCallback(throwable -> Log.e("ERROR", throwable.toString()));

        //spotifyAppRemote.getPlayerApi().play("spotify:track:" + );
    }
}
