package com.dmrzlak.pbtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class PlayerActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "6c8d86bc00b54291955a9f6857d37ff2";
    private static final String CLIENT_SEC = "d361a62b4318412ba8ea6ce1db7590c2";
    private static final String REDIRECT_URI = "http://pbtest.com/callback/";
    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    private static final int REQUEST_CODE = 1337;

    private SpotifyAppRemote mSpotifyAppRemote;
    private String userID;
    private String playlistID;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Bundle extras = getIntent().getExtras();
        userID = extras.getString("User");
        playlistID = extras.getString("Playlist");
        authToken = extras.getString("AuthToken");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("PlayerActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("PlayerActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play("spotify:"+userID+":playlist:"+playlistID);
        mSpotifyAppRemote.getPlayerApi().
    }
}
