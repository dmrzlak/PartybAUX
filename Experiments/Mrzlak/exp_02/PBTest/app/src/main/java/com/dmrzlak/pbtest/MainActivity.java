package com.dmrzlak.pbtest;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "6c8d86bc00b54291955a9f6857d37ff2";
    private static final String CLIENT_SEC = "d361a62b4318412ba8ea6ce1db7590c2";
    private static final String REDIRECT_URI = "http://pbtest.com/callback/";
    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    private static final int REQUEST_CODE = 1337;

    ListView list;
    ArrayList<PlaylistSimple> listItems= new ArrayList<>();
    ArrayList<String> playlistNames= new ArrayList<String>();
    ArrayAdapter<String> adapter;

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list=(ListView) findViewById(R.id.list_item);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playlistNames);
        list.setAdapter(adapter);

        SpotifyApi api= new SpotifyApi();

        final Intent intent = getIntent();
        final String token = intent.getStringExtra(EXTRA_TOKEN);

        api.setAccessToken(token);
        final SpotifyService spotify = api.getService();
        spotify.getMyPlaylists(new SpotifyCallback<Pager<PlaylistSimple>>(){
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                List newList= playlistSimplePager.items.subList(0,19);
                ListIterator iter =newList.listIterator();
                while(iter.hasNext()){
                    PlaylistSimple p= (PlaylistSimple) iter.next();
                    playlistNames.add(p.name);
                    listItems.add(p);
                    adapter.notifyDataSetChanged();
                    iter.remove();

                }
                list.setClickable(true);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        Log.println(Log.VERBOSE,"", listItems.get(position).name);
                        spotify.getMe(new Callback<UserPrivate>() {
                            @Override
                            public void success(UserPrivate userPrivate, Response response) {
//                                Bundle newBundle = new Bundle();
                                Intent intent1 = new Intent(MainActivity.this, PlayerActivity.class);
                                String message = listItems.get(position).id;
                                intent1.putExtra("User", userPrivate.id);
                                intent1.putExtra("Playlist", message);
                                intent1.putExtra("AuthToken", token);
                                startActivity(intent1);
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
                    }
                });
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                playlistNames.add("Couldn't Load Playlists");
                adapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onStop(){
        super.onStop();
    }
}

