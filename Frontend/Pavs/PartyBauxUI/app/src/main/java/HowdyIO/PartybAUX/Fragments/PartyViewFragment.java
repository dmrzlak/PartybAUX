package HowdyIO.PartybAUX.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import HowdyIO.PartybAUX.Activities.PartyActivity;
import HowdyIO.PartybAUX.Activities.PartybAUXActivity;
import HowdyIO.PartybAUX.Model.SpotifyPlayer;
import HowdyIO.PartybAUX.Model.TrackListAdapter;
import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Tools.SpotifyServiceUtil;
import HowdyIO.PartybAUX.Utils.DataProvider;
import HowdyIO.PartybAUX.Utils.StaticSocketWrapper;
import HowdyIO.PartybAUX.Views.EmbeddedPlayer;
import HowdyIO.PartybAUX.Views.PartyMembersView;
import HowdyIO.PartybAUX.Views.PartyPopup;
import HowdyIO.PartybAUX.Views.SongDetailView;
import PartyBauxUI.R;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Chris on 2/24/2019.
 */

public class PartyViewFragment extends PartybAUXFragment implements PartyActivity.SongObserver {

    private Toolbar toolbar;
    private View root;
    private Context context;
    private PartyPopup partyPopup;
    private SearchFragment searchFragment;
    private RecyclerView recyclerView;
    private TrackListAdapter adapter;

    private FrameLayout membersButton;
    private RelativeLayout addSongBar;
    private CardView addSongButton;
    private EmbeddedPlayer embeddedPlayer;
    private SongDetailView songDetailView;
    private PartyMembersView partyMembersView;
    private ArrayList<Track> trackQueue = new ArrayList<>();

    private boolean hasPlayerPermissions;
    private boolean firstLaunchAsHost;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (root != null) return root;
        root = inflater.inflate(R.layout.fragment_party_view, container, false);
        embeddedPlayer = root.findViewById(R.id.embedded_player);
        songDetailView = root.findViewById(R.id.song_detail_view);
        partyMembersView = root.findViewById(R.id.party_members_view);
        membersButton = root.findViewById(R.id.menu_action_members);
        addSongBar = root.findViewById(R.id.bottom_bar);
        addSongButton = root.findViewById(R.id.button_add_song);
        recyclerView = root.findViewById(R.id.recycler_view);
        this.context = getContext();

        onCreateView(root);

        setUpToolbar();
        setUpRecyclerView();
        setUpListeners();
        setUpPopup();
        setUpPartyMembersView();
        updateUser();
        return root;
    }

    private void setUpPopup() {
        partyPopup = new PartyPopup(context, root);
        partyPopup
                .setTitle("Leave Party")
                .setPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leaveParty();
                    }
                })
                .setNegativeClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        partyPopup.dismiss();
                    }
                });
    }

    private void checkPlayer() {
        if (!hasPlayerPermissions) return;
        if (!trackQueue.isEmpty()) embeddedPlayer.refresh(trackQueue.get(0));
        if (firstLaunchAsHost && !SpotifyPlayer.queueArray().isEmpty()) {
            SpotifyPlayer.playIfNotPlaying();
            firstLaunchAsHost = false;
        }
    }

    private void setUpToolbar() {
        toolbar = root.findViewById(R.id.toolbar);
        toolbar.setTitle("My Party");
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof AppCompatTextView) {
                ((AppCompatTextView) child)
                        .setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + getResources().getString(R.string.font_primary)), Typeface.BOLD);
            }
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayer();
    }

    private void updatePlaylist(ArrayList<String> songURIs) {
        if (songURIs.isEmpty()) {
            trackQueue.clear();
            adapter.notifyDataSetChanged();
            embeddedPlayer.setDefault();
            return;
        }

        SpotifyServiceUtil.getInstance().getTracks(songURIs, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                trackQueue.clear();
                trackQueue.addAll(tracks.tracks);
                adapter.notifyDataSetChanged();
                checkPlayer();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
/*        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                if(!recyclerView.canScrollVertically(-1) && velocityY < 0)
                    popupLeaveParty();
                return false;
            }
        });*/

        recyclerView.setAdapter(adapter = new TrackListAdapter(trackQueue, recyclerView));
        adapter.setOnItemClickListener(new TrackListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Track track) {
                songDetailView.setContent(track, true).show();
            }
        });

    }

    private void updateUser() {
        int userType = QuickTools.sharedPrefs(context).getInt(QuickTools.SHARED_PREFS_USERTYPE, QuickTools.ROLE_HOST);

        if (userType == QuickTools.ROLE_HOST) {
            hasPlayerPermissions = true;
            addSongBar.setVisibility(View.GONE);
            setRecyclerViewPaddingBottom(context.getResources().getDimension(embeddedPlayer.getRscForPlayerHeight()));
            setUpEmbeddedPlayer();
            firstLaunchAsHost = true;

        } else if (userType == QuickTools.ROLE_GUEST || userType == QuickTools.ROLE_MOD) {
            hasPlayerPermissions = false;
            embeddedPlayer.hide();
            addSongBar.setVisibility(View.VISIBLE);
            setRecyclerViewPaddingBottom(context.getResources().getDimension(R.dimen.item_size_medium));
        }

        songDetailView.setUserType(userType);
        toolbar.setTitle(QuickTools.hostName + "'s Party");

        //TODO
        //Find user type ->
        //User is Host
        //Create party on server with potential join code
        //Set up controls, suggest songs, ect.
        //User is DJ
        //Not sure yet, basically reorder/remove permissions
        //User is Guest
        //Connect to server from host
        //Display playlist from server

        //TODOz
    }

    private void setRecyclerViewPaddingBottom(float height) {
        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), (int) height);
    }

    private void setUpEmbeddedPlayer() {
        embeddedPlayer.show();
        embeddedPlayer.setAddSongListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchFragment();
            }
        });
        embeddedPlayer.setSongClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trackQueue.isEmpty()) return;

                songDetailView.setContent(adapter.getCurrentlyPlaying());
                songDetailView.show();
            }
        });
        adapter.setImageLoadedListener(new TrackListAdapter.OnImageLoadedListener() {
            @Override
            public void onImageLoaded(Drawable resource) {
                embeddedPlayer.loadInGradient(resource);
            }
        });
    }

    private void setUpListeners() {
        addSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchFragment();
            }
        });
        membersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partyMembersView.show();
            }
        });
    }

    private void setUpPartyMembersView() {
        partyMembersView.setItemClickListener(new PartyMembersView.OnItemClickListener() {
            @Override
            public void onItemClick(String username, int index) {
                UserProfileFragment profileFragment = new UserProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                profileFragment.setArguments(bundle);

                startFragment(profileFragment, "ProfileFragment", "ProfileFragment");
            }
        });
        partyMembersView.setPartyClosedListener(new PartyMembersView.PartyClosedListener() {
            @Override
            public void onClose() {
                leaveParty();
            }
        });
        ((PartyActivity) partybAUXActivity).setMemberObserver(partyMembersView);
        partyMembersView.connect();
    }

    @Override
    public boolean onBackPressed() {
        if (SongDetailView.isOpen) {
            songDetailView.hide();
            return true;
        } else if (PartyMembersView.isOpen) {
            partyMembersView.hide();
            return true;
        }else
            popupLeaveParty();

        return true;
    }

/*    public void getTracksFromServer() {
        updatePlaylist();
    }*/

    // We need to convert our return value from the form: "["<SongID>","<SongID>"]"
    private ArrayList<String> parseList(String response) {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 1; i < response.length() - 1; i++) {
            if (response.charAt(i) == '"') {
                String songID = goToQuote(response, i + 1);
                temp.add(songID);
                i += songID.length() + 1;
            }
        }

        return temp;
    }

    private String goToQuote(String victim, int index) {
        StringBuilder result = new StringBuilder();
        while (victim.charAt(index) != '"') {
            result.append(victim.charAt(index));
            index++;
        }
        return result.toString();
    }

    private void openSearchFragment() {
        startFragment(searchFragment == null ? (searchFragment = new SearchFragment()) : searchFragment,
                "Search", "Search", PartybAUXActivity.ANIM_SLIDE_IN_OUT);
    }


    public void popupLeaveParty() {
        if (PartyPopup.isOpen) {
            partyPopup.dismiss();
            return;
        }

        partyPopup.show();
    }


    public void leaveParty() {
        DataProvider.leaveParty(QuickTools.userName, new HowdyIO.PartybAUX.Model.Callback() {
            @Override
            public void onResult(String result) {
                finishLeave();
            }
        });
    }

    private void finishLeave() {
        SpotifyPlayer.reset();
        StaticSocketWrapper.update();
        QuickTools.partyID = -1;
        finish();
    }

    @Override
    public void onConnected() {
        partyMembersView.connect();
    }

    @Override
    public void onListReceived(ArrayList<String> songURIs) {
        updatePlaylist(songURIs);
    }

}
