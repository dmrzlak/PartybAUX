package HowdyIO.PartybAUX.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


import HowdyIO.PartybAUX.Fragments.HomeFragment;
import HowdyIO.PartybAUX.Fragments.PartybAUXFragment;
import HowdyIO.PartybAUX.Fragments.SearchFragment;
import HowdyIO.PartybAUX.Views.CustomProgressBar;
import HowdyIO.PartybAUX.Views.FluidSearchView;
import PartyBauxUI.R;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "e2a396f6cc5a4fb39bcf75fc55a696af";
    private static final String REDIRECT_URI = "spotifytestapp://callback";
    private static final int REQUEST_CODE = 3421;

    private String userToken;
    private SpotifyAppRemote spotifyAppRemote;
    private SpotifyService spotifyService;

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private Toolbar toolbar;
    private FluidSearchView searchView;

    private ViewGroup moduleContainer;

    public CustomProgressBar progressBar;

    private void connected() {
        // Then we will write some more code here.
        spotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        moduleContainer = findViewById(R.id.module_container);
        progressBar = findViewById(R.id.progress_bar);
        setUpApp();
    }

    private void setUpApp() {
        setSupportActionBar(toolbar = (Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setVisibility(View.INVISIBLE);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        //setUpSearchView();
        setUpSpotify();
        //startActivity(new Intent(this, LogInActivity.class));
    }

    private void setUpSearchView(){
        if(searchView == null) {
            searchView = new FluidSearchView(toolbar, this);
            searchView.build();
            searchView.hide();
        }
    }

    private void setUpSpotify(){
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    userToken = response.getAccessToken();
                    connectToSpotify();
                    // Handle successful response
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    private void connectToSpotify(){
        SpotifyApi spotifyApi = new SpotifyApi();
        spotifyApi.setAccessToken(userToken);

        spotifyService = spotifyApi.getService();

        toolbar.setTitle("Search");
        toolbar.setVisibility(View.VISIBLE);

        /*
        if(searchFragment == null){
            startFragment(searchFragment = new SearchFragment(), "searchFragment");
        }
        /*if(homeFragment == null){
            homeFragment = new HomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.module_container, homeFragment, "homeFragment")
                    .commitAllowingStateLoss();
        }


        if(searchView == null) setUpSearchView();
        searchView.show();
        //startActivity(new Intent(this, LogInActivity.class));

        /*ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        MainActivity.this.spotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });*/
    }

    public void startFragment(PartybAUXFragment fragment, String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.module_container, fragment, tag)
                .commitAllowingStateLoss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        menu.findItem(R.id.action_settings).getIcon().setTint(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Toast.makeText(this, "This is the settings option", Toast.LENGTH_SHORT).show();
                progressBar.finish();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.start();
                    }
                },2000);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(searchFragment == null || !searchFragment.onBackPressed())
            super.onBackPressed();
    }

    public SpotifyService getSpotifyService() {
        return spotifyService;
    }

    public FluidSearchView getSearchView(){
        return searchView;
    }

    public Toolbar getToolbar(){
        return toolbar;
    }
}
