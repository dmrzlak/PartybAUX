package HowdyIO.PartybAUX.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import HowdyIO.PartybAUX.Fragments.PartyViewFragment;
import HowdyIO.PartybAUX.Fragments.PartybAUXFragment;
import HowdyIO.PartybAUX.Model.Callback;
import HowdyIO.PartybAUX.Model.ClientSimple;
import HowdyIO.PartybAUX.Model.HybridTrack;
import HowdyIO.PartybAUX.Model.PartyBox;
import HowdyIO.PartybAUX.Model.PartyBoxCallback;
import HowdyIO.PartybAUX.Model.Song;
import HowdyIO.PartybAUX.Model.SpotifyPlayer;
import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Utils.DataProvider;
import HowdyIO.PartybAUX.Utils.StaticSocketWrapper;
import HowdyIO.PartybAUX.Views.CustomProgressBar;
import HowdyIO.PartybAUX.Views.GestureParent;
import PartyBauxUI.R;

/**
 * Created by Chris on 2/26/2019.
 */

public class PartyActivity extends PartybAUXActivity implements SpotifyPlayer.PlayerCallback{

    private PartyViewFragment partyViewFragment;
    private GestureParent gestureParent;
    private MemberObserver memberObserver;

    private ViewGroup root;
    private CustomProgressBar progressBar;

    public ArrayList<String> partyTrackIDs = new ArrayList<>();

    public int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
        root = findViewById(R.id.root_layout);
        progressBar = findViewById(R.id.progress_bar);
        gestureParent = findViewById(R.id.module_container);
        setUpParty();
        setUpGestures();
        SpotifyPlayer.addPlayerCallback(this);
    }

    private void setUpParty() {
        SpotifyPlayer.pause();
        connectParty();
    }

    private void connectParty(){
        userType = QuickTools.sharedPrefs(getBaseContext()).getInt(QuickTools.SHARED_PREFS_USERTYPE, QuickTools.ROLE_HOST);

        // Besides the DataProvider call, these virtually do the same thing
        // Gonna work on a better implementation in the future
        if (userType == QuickTools.ROLE_HOST) {
            DataProvider.createParty(QuickTools.userName, new HowdyIO.PartybAUX.Model.Callback() {
                @Override
                public void onResult(String result) {
                    int resultCode = Integer.valueOf(result);
                    if (resultCode == -3) {
                        Log.e("Couldn't connect", "Could not connect to server");
                        finish();
                        return;
                    }
                    QuickTools.hostName = QuickTools.userName;
                    finishConnectParty(resultCode);

                }
            });
        } else if (userType == QuickTools.ROLE_GUEST) {
            DataProvider.joinParty(QuickTools.hostName, QuickTools.userName, new HowdyIO.PartybAUX.Model.Callback() {
                @Override
                public void onResult(String result) {
                    int resultCode = Integer.valueOf(result);
                    if (resultCode == -3) {
                        Log.e("Couldn't connect", "Could not connect to server");
                        finish();
                        return;
                    }
                    finishConnectParty(resultCode);

                }
            });
        }
    }

    private void finishConnectParty(int partyID){
        QuickTools.partyID = partyID;

        // partyID =
        //      -4 party already
        if(partyID < 0) {
            QuickTools.partyID = -1;
            finish();
            return;
        }

        verifyUserType();
        startFragment(partyViewFragment = new PartyViewFragment(), "PartyView", "Party");

        //partyViewFragment.onConnected();

        setUpWebSocket();
    }

    private void verifyUserType(){
        // If the person who joins is also the host, set them to host and continue.
        // This is a fix for when someone exits without disbanding their party.
        if(userType == QuickTools.ROLE_GUEST && QuickTools.hostName.equalsIgnoreCase(QuickTools.userName)) {
            QuickTools.sharedPrefs(getBaseContext()).edit().putInt(QuickTools.SHARED_PREFS_USERTYPE, (userType = QuickTools.ROLE_HOST)).commit();
            QuickTools.hostName = QuickTools.userName;
            QuickTools.userType = QuickTools.ROLE_HOST;
        }

    }


    private void setUpGestures() {
        gestureParent.setOnSwipeListener(new GestureParent.OnSwipeListener() {
            @Override
            public void onSwipeRight() {

            }
            @Override
            public void onSwipeLeft() {

            }
            @Override
            public void onSwipeTop() {

            }
            @Override
            public void onSwipeBottom() {
                onBackPressed();
            }
        });
    }

    public void startFragment(PartybAUXFragment fragment, String tag, String title) {
        startFragment(fragment, tag, title, R.id.module_container, ANIM_SLIDE_IN_OVER);
    }

    public void startFragment(PartybAUXFragment fragment, String tag, String title, int animType) {
        startFragment(fragment, tag, title, R.id.module_container, animType);
    }

    public void popupFragment(PartybAUXFragment fragment, String tag, String title) {
        popupFragment(fragment, tag, title, R.id.module_container);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void attemptLeaveParty(){
        PartybAUXFragment currentFragment = getCurrentFragment(getSupportFragmentManager());
        if(currentFragment instanceof PartyViewFragment)
            ((PartyViewFragment) currentFragment).popupLeaveParty();

        //TODO: popup some "would you like to leave this party?" box
    }

    public void addToPlaylist(String trackID) {
        DataProvider.addSong(QuickTools.partyID + "", trackID, new Callback() {
            @Override
            public void onResult(String result) {
                int res = Integer.parseInt(result);
                switch (res) {
                    case 0:
                        SpotifyPlayer.playIfNotPlaying();
                        StaticSocketWrapper.update();
//                            partyTrackIDs.add(trackID);
                    case 1:
                        Log.d("FAIL ADD SONG", "onResult: ");
                    case 3:
                        Log.d("SERVER ERROR", "Failed to connect");
                    default:
                        Log.d("SERVER ERROR", "Failed to connect");
                }
            }
        });
//        if(socketProvider != null) socketProvider.sendEchoViaStomp("/app/update/" + QuickTools.partyID);
//        partyTrackIDs.add(trackID);
    }

    public void setUpWebSocket(){
        StaticSocketWrapper.initSocketProvider("/topic/party/" +QuickTools.partyID);
        StaticSocketWrapper.setCallback(new PartyBoxCallback() {
            @Override
            public void onResult(PartyBox partyBox) {
                if(partyBox == null) return;

                partyTrackIDs = new ArrayList<>(sortSongs(partyBox.getSongs()));

                SpotifyPlayer.updateQueue(partyTrackIDs);
                partyViewFragment.onListReceived(SpotifyPlayer.queueArray());
                if(memberObserver != null) memberObserver.onMembersUpdated(partyBox.getPartyMembers());
            }
        });

        StaticSocketWrapper.update();
    }

    private ArrayList<String> sortSongs(ArrayList<Song> songs){
        ArrayList<String> temp = new ArrayList<>();
        if(songs == null || songs.isEmpty()) return temp;

        Collections.sort(songs);
        for(Song song: songs)
            temp.add(song.getUri());

        return temp;
    }

    public void setMemberObserver(MemberObserver memberObserver){
        this.memberObserver = memberObserver;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onPlayTrack(HybridTrack track) {

    }
    @Override
    public void onPlayPause(boolean isPlaying) {

    }
    @Override
    public void onTracksRemoved(ArrayList<String> trackUri) {
        StaticSocketWrapper.update();
    }


    public interface SongObserver{
        public void onConnected();
        public void onListReceived(ArrayList<String> songURIs);
    }

    public interface MemberObserver{
        void onMembersUpdated(ArrayList<ClientSimple> members);
    }


}
