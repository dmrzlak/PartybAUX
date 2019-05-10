package HowdyIO.PartybAUX.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import HowdyIO.PartybAUX.Activities.PartyActivity;
import HowdyIO.PartybAUX.Model.PlaylistListAdapter;
import HowdyIO.PartybAUX.Model.TrackListAdapter;
import HowdyIO.PartybAUX.Tools.SpotifyServiceUtil;
import HowdyIO.PartybAUX.Views.SongDetailView;
import PartyBauxUI.R;
import kaaes.spotify.webapi.android.models.FeaturedPlaylists;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Chris on 3/5/2019.
 */

public class PlaylistViewFragment extends PartybAUXFragment {

    private View root;
    private Toolbar toolbar;

    PartyActivity partyActivity;
    RecyclerView recyclerView;
    PlaylistListAdapter playlistAdapter;
    SpotifyServiceUtil spotifyServiceUtil;

    ArrayList<PlaylistSimple> pulledPlaylists = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (root == null) root = inflater.inflate(R.layout.fragment_playslist_view, container, false);
        toolbar = root.findViewById(R.id.toolbar);
        recyclerView = root.findViewById(R.id.recycler_view);


        onCreateView(root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*setSupportActionBar(toolbar = (Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        partyActivity = (PartyActivity) getActivity();

        spotifyServiceUtil = SpotifyServiceUtil.getInstance();
        setUpRecyclerView();
        pullPlaylists();
        //showNewReleases();
    }

    private void pullPlaylists() {
        spotifyServiceUtil.getFeaturedPlaylists(new Callback<FeaturedPlaylists>() {
            @Override
            public void success(FeaturedPlaylists featuredPlaylists, Response response) {
                ArrayList<PlaylistSimple> temp = new ArrayList<>(featuredPlaylists.playlists.items);
                pulledPlaylists.addAll(temp);
                playlistAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        playlistAdapter = new PlaylistListAdapter(pulledPlaylists, recyclerView);
        recyclerView.setAdapter(playlistAdapter);
        playlistAdapter.setOnItemClickListener(getItemClickListener());
    }

    private PlaylistListAdapter.OnItemClickListener getItemClickListener(){
        return new PlaylistListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, PlaylistSimple playlist) {
//                if(searchView.isOpened())
//                    searchView.onClose(true);


            }
        };
    }
}
// 5