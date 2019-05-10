package HowdyIO.PartybAUX.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


import HowdyIO.PartybAUX.Fragments.HomeFragment;
import HowdyIO.PartybAUX.Fragments.PartybAUXFragment;
import HowdyIO.PartybAUX.Model.Callback;
import HowdyIO.PartybAUX.Model.User;
import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Tools.SpotifyServiceUtil;
import HowdyIO.PartybAUX.Utils.DataProvider;
import HowdyIO.PartybAUX.Views.CustomProgressBar;
import PartyBauxUI.R;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public class MainActivity extends PartybAUXActivity {

    private static final String CLIENT_ID = "e2a396f6cc5a4fb39bcf75fc55a696af";
    private static final String REDIRECT_URI = "spotifytestapp://callback";
    private static final int REQUEST_CODE_LOG_IN = 2121;
    private static final int REQUEST_CODE_SPOTIFY = 3421;

    private String spotifyToken = "";
    private SpotifyAppRemote spotifyAppRemote;
    private SpotifyService spotifyService;

    private ViewGroup root;

    public CustomProgressBar progressBar;
    private boolean connecting;


    private void connected() {
        // Then we will write some more code here.
        spotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        root = findViewById(R.id.root_layout);
        progressBar = findViewById(R.id.progress_bar);
        setFlagsForWindow();
        logUserIntoPartyBaux();
    }

    private void setFlagsForWindow() {
        Window window = getWindow();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
        //int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        //window.getDecorView().setSystemUiVisibility(uiOptions);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); //Let's our layout bounds go past status bar
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.background));
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void logUserIntoPartyBaux() {
        User user = QuickTools.getSavedUser(getBaseContext());
        if (user == null) {
            openLogInActivity();
            return;
        }

        progressBar.show();
        DataProvider.logUserIn(user.getUsername(), user.getPassword(), new Callback() {
            @Override
            public void onResult(String obj) {
                final int result = Integer.valueOf(obj);
                switch (result) {
                    case -1:
                    case -2:
                    case -3:
                        openLogInActivity();
                        return;
                    default:

                        break;
                }

                QuickTools.userName = user.getUsername();
                setUpSpotify();
            }
        });
    }

    private void openLogInActivity() {
        startActivityForResult(new Intent(this, LogInActivity.class), REQUEST_CODE_LOG_IN);
    }


    private void setUpSpotify() {
        if (connecting) return;
        connecting = true;

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

//        Intent intent = AuthenticationClient.createLoginActivityIntent(this, request);
//        AuthenticationClient.getResponse(REQUEST_CODE_SPOTIFY, intent);
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE_SPOTIFY, request);
    }

    /**
     * Will be called after either of these activities are opened, and then closed:
     * - Our Login Activity
     * - Spotify's built in Login activity
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_LOG_IN) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    root.post(new Runnable() {
                        @Override
                        public void run() {
                            setUpSpotify();
                        }
                    });
                    break;
                default:
                    finishAffinity();
                    break;
            }
        } else if (requestCode == REQUEST_CODE_SPOTIFY) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    spotifyToken = response.getAccessToken();
                    connectToAppRemote();
                    // Handle successful response
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    setUpSpotify();
                    // Handle other cases
            }
        }

    }

    private void connectToAppRemote() {
        ConnectionParams connectionParams =
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
                        connectToSpotify();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void connectToSpotify() {
        SpotifyApi spotifyApi = new SpotifyApi();
        spotifyApi.setAccessToken(spotifyToken);
        SpotifyServiceUtil.setSpotifyService(spotifyApi.getService(), spotifyAppRemote, spotifyToken);

        connecting = false;

        startFragment(new HomeFragment(), "Home", "Home");
        progressBar.finish();
    }

    public void logUserOut(){
        getSupportFragmentManager().popBackStackImmediate();
        QuickTools.sharedPrefs(getBaseContext()).edit().putString(QuickTools.SHARED_PREFS_USER, "").commit();
        openLogInActivity();
    }

    public void startFragment(PartybAUXFragment fragment, String tag, String title) {
        startFragment(fragment, tag, title, R.id.module_container, PartyActivity.ANIM_SLIDE_IN_OVER);
    }

    public void popupFragment(PartybAUXFragment fragment, String tag, String title) {
        popupFragment(fragment, tag, title, R.id.popup_container);
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
                }, 2000);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
/*        if (spotifyToken.isEmpty()) {
            root.post(new Runnable() {
                @Override
                public void run() {
                    setUpSpotify();
                }
            });
        }*/
    }
}
