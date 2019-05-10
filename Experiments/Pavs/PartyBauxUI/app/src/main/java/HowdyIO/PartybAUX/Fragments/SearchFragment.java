package HowdyIO.PartybAUX.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import HowdyIO.PartybAUX.Activities.MainActivity;
import HowdyIO.PartybAUX.Model.SearchAdapter;
import HowdyIO.PartybAUX.Views.FluidSearchView;
import HowdyIO.PartybAUX.Views.SongDetailView;
import PartyBauxUI.R;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Chris on 1/24/2019.
 */

public class SearchFragment extends PartybAUXFragment{

    private View root;
    MainActivity mainActivity;
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    SpotifyService spotifyService;
    SongDetailView songDetailView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        mainActivity = (MainActivity) getActivity();
        recyclerView = root.findViewById(R.id.recycler_view);
        songDetailView = root.findViewById(R.id.song_detail_view);

        onCreateView(root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spotifyService = mainActivity.getSpotifyService();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        setUpSearch();
    }

    private SearchAdapter.OnItemClickListener getItemClickListener(){
        final SearchFragment me = this;
        return new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Track track) {
                if(searchView.isOpened())
                    searchView.closeSearch(true);

                songDetailView.setContent(track, me);
                songDetailView.show();
            }
        };
    }

    FluidSearchView searchView;
    private void setUpSearch(){
        searchView = mainActivity.getSearchView();
        //searchView.setToolbarAlpha(0.8f);
//        root.post(new Runnable() {
//            @Override
//            public void run() {
//                searchView.openSearch();
//            }
//        });
        searchView.setOnSubmitListener(new FluidSearchView.OnSubmitListener() {
            @Override
            public void onSubmit(String query) {
                Map<String, Object> options = new HashMap<>();
                options.put(SpotifyService.OFFSET, 0);
                options.put(SpotifyService.LIMIT, 50);

                spotifyService.searchTracks(query, options, new Callback<TracksPager>() {
                    @Override
                    public void success(TracksPager tracksPager, Response response) {
                        ArrayList<Track> tracks = new ArrayList<>(tracksPager.tracks.items);
                        recyclerView.setAdapter(searchAdapter = new SearchAdapter(filter(tracks), recyclerView));
                        searchAdapter.setOnItemClickListener(getItemClickListener());
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SongDetailView.isOpen)
                    songDetailView.hide();
            }
        });
    }

    private ArrayList<Track> filter(ArrayList<Track> tracks){
        HashMap<String, Boolean> containsName = new HashMap<>();
        HashMap<String, Boolean> containsArtist = new HashMap<>();
        for(Track track: tracks){
            containsName.put(track.name, false);
            containsArtist.put(track.artists.get(0).name, false);
        }
        ArrayList<Track> nonDuplicates = new ArrayList<Track>();
        for(Track track: tracks){
            Boolean hasName = containsName.get(track.name);
            Boolean hasArtist = containsArtist.get(track.artists.get(0).name);
            if((!hasName && !hasArtist)){
                nonDuplicates.add(track);
                containsName.put(track.name, true);
                containsArtist.put(track.artists.get(0).name, true);
            }
        }
        return nonDuplicates;
    }

    @Override
    public boolean onBackPressed(){
        if(SongDetailView.isOpen){
            songDetailView.hide();
            return true;
        }
        else
            return false;
    }

    public void addToPlaylist(Track currentTrack) {
        //TODO mainActivity.addToPlaylist
    }
}
