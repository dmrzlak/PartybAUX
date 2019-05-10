package HowdyIO.PartybAUX.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import HowdyIO.PartybAUX.Activities.PartyActivity;
import HowdyIO.PartybAUX.Model.PlaylistListAdapter;
import HowdyIO.PartybAUX.Model.TrackListAdapter;
import HowdyIO.PartybAUX.Tools.SpotifyServiceUtil;
import HowdyIO.PartybAUX.Views.FluidSearchView;
import HowdyIO.PartybAUX.Views.SongDetailView;
import PartyBauxUI.R;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.FeaturedPlaylists;
import kaaes.spotify.webapi.android.models.NewReleases;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Chris on 1/24/2019.
 */

public class SearchFragment extends PartybAUXFragment {

    private View root;
    private Toolbar toolbar;

    PartyActivity partyActivity;

    RecyclerView recyclerView;
    RecyclerView.Adapter currentAdapter;


    TrackListAdapter trackListAdapter;
    ArrayList<SearchResults> searchHistory = new ArrayList<>();
    ArrayList<Track> pulledTracks = new ArrayList<>();

    PlaylistListAdapter playlistListAdapter;
    ArrayList<PlaylistSimple> pulledPlaylists = new ArrayList<>();


    SpotifyServiceUtil spotifyServiceUtil;
    SongDetailView songDetailView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (root == null) root = inflater.inflate(R.layout.fragment_search, container, false);
        toolbar = root.findViewById(R.id.toolbar);
        recyclerView = root.findViewById(R.id.recycler_view);
        songDetailView = root.findViewById(R.id.song_detail_view);
        searchView = root.findViewById(R.id.search_view);


        onCreateView(root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*setSupportActionBar(toolbar = (Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        partyActivity = (PartyActivity) getActivity();

        spotifyServiceUtil = SpotifyServiceUtil.getInstance();
        setUpRecyclerView();
        setUpSearch();
        getFeaturedPlaylists();
    }

    private void changeAdapter(Class adapterClass) {
        if (adapterClass == Track.class) {
            currentAdapter = trackListAdapter != null ? trackListAdapter :
                    (trackListAdapter = new TrackListAdapter("", pulledTracks, recyclerView));
        } else if (adapterClass == PlaylistSimple.class) {
            currentAdapter = playlistListAdapter != null ? playlistListAdapter :
                    (playlistListAdapter = new PlaylistListAdapter("", pulledPlaylists, recyclerView));
        }

        recyclerView.setAdapter(currentAdapter);
    }


    private void getFeaturedPlaylists() {
        changeAdapter(PlaylistSimple.class);

        playlistListAdapter.setSearchQuery("Featured Playlists");
        playlistListAdapter.setOnItemClickListener(getPlaylistClickListener());

        spotifyServiceUtil.getFeaturedPlaylists(new Callback<FeaturedPlaylists>() {
            @Override
            public void success(FeaturedPlaylists featuredPlaylists, Response response) {
                pulledPlaylists.clear();
                ArrayList<PlaylistSimple> temp = new ArrayList<>(featuredPlaylists.playlists.items);
                pulledPlaylists.addAll(temp);
                currentAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void showNewReleases() {
        changeAdapter(Track.class);

        trackListAdapter.setSearchQuery("New Releases");
        pulledTracks.clear();
        spotifyServiceUtil.getNewReleases(new Callback<NewReleases>() {
            @Override
            public void success(NewReleases newReleases, Response response) {
                final ArrayList<AlbumSimple> albums = new ArrayList<>(newReleases.albums.items);
                spotifyServiceUtil.getTracksFromAlbums(albums, new Callback<ArrayList<List<Track>>>() {
                    @Override
                    public void success(ArrayList<List<Track>> lists, Response response) {
                        for (int i = 0; i < lists.size(); i++) {
                            Track cur = lists.get(i).get(0);
                            cur.album = albums.get(i);
                            pulledTracks.add(cur);
                        }
                        trackListAdapter.notifyDataSetChanged();
                        trackListAdapter.setOnItemClickListener(getTrackClickListener());
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchView != null && isCurrentFrag) {
            searchView.requestSearchFocus();
        }
    }

    @Override
    public void onPause() {
        if (searchView != null && justPaused) {
            searchView.clearSearchFocus();
        }

        super.onPause();
    }

    @Override
    public void onConnected() {


    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    private TrackListAdapter.OnItemClickListener getTrackClickListener() {
        return new TrackListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Track track) {
//                if(searchView.isOpened())
//                    searchView.onClose(true)

                songDetailView.setContent(track, partyActivity.partyTrackIDs.contains(track.id));
                songDetailView.show();
                searchView.hide();

                songDetailView.setOnCloseListener(new SongDetailView.OnCloseListener() {
                    @Override
                    public void onClose() {
                        searchView.show();
                    }
                });

                songDetailView.setOnAddToPlaylistListener(new SongDetailView.OnAddToPlaylistListener() {
                    @Override
                    public void onClicked(Track track, boolean checked) {
                        addToPlaylist(track);
                    }
                });
            }
        };
    }

    private PlaylistListAdapter.OnItemClickListener getPlaylistClickListener() {
        return new PlaylistListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final PlaylistSimple playlist) {
                changeAdapter(Track.class);
                trackListAdapter.setSearchQuery(playlist.name);
                pulledTracks.clear();


                spotifyServiceUtil.getTracksFromPlaylist(playlist, new Callback<Pager<PlaylistTrack>>() {
                    @Override
                    public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                        searchHistory.add(new SearchResults(PlaylistSimple.class, new ArrayList<>(pulledPlaylists)));
                        ArrayList<PlaylistTrack> temp = new ArrayList<>(playlistTrackPager.items);
                        for (int i = 0; i < temp.size(); i++)
                            pulledTracks.add(temp.get(i).track);

                        trackListAdapter.notifyDataSetChanged();
                        trackListAdapter.setOnItemClickListener(getTrackClickListener());

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

            }
        };
    }

    FluidSearchView searchView;

    private void setUpSearch() {
        searchView.init(toolbar, getActivity(), true);

        //searchView.setToolbarAlpha(0.8f);
//        root.post(new Runnable() {
//            @Override
//            public void run() {
//                searchView.openSearch();
//            }
//        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) return true;
                searchSpotify(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SongDetailView.isOpen)
                    songDetailView.hide();
            }
        });

        searchView.setOnCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });


    }

    private void searchSpotify(String query) {
        if(currentAdapter instanceof TrackListAdapter)
            searchHistory.add(new SearchResults(Track.class, new ArrayList<>(pulledTracks), trackListAdapter != null? trackListAdapter.getSearchQuery(): ""));
        else if(currentAdapter instanceof PlaylistListAdapter)
            searchHistory.add(new SearchResults(PlaylistSimple.class, new ArrayList<>(pulledPlaylists)));

        changeAdapter(Track.class);

        trackListAdapter.setSearchQuery(query);
        trackListAdapter.setOnItemClickListener(getTrackClickListener());

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, 0);
        options.put(SpotifyService.LIMIT, 50);

//                partybAUXActivity.progressBar.show();
//                partybAUXActivity.progressBar.start();
        SpotifyServiceUtil.getInstance().searchTracks(query, options, new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {
                pulledTracks.clear();
                ArrayList<Track> temp = new ArrayList<>(tracksPager.tracks.items);
                pulledTracks.addAll(filter(temp));
                trackListAdapter.notifyDataSetChanged();

                //partybAUXActivity.progressBar.finish();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private ArrayList<Track> filter(ArrayList<Track> tracks) {
        HashMap<String, Boolean> containsName = new HashMap<>();
        HashMap<String, Boolean> containsArtist = new HashMap<>();
        for (Track track : tracks) {
            containsName.put(track.name, false);
            containsArtist.put(track.artists.get(0).name, false);
        }
        ArrayList<Track> nonDuplicates = new ArrayList<Track>();
        for (Track track : tracks) {
            Boolean hasName = containsName.get(track.name);
            Boolean hasArtist = containsArtist.get(track.artists.get(0).name);
            if ((!hasName && !hasArtist)) {
                nonDuplicates.add(track);
                containsName.put(track.name, true);
                containsArtist.put(track.artists.get(0).name, true);
            }
        }
        return nonDuplicates;
    }

    @Override
    public boolean onBackPressed() {
        if (SongDetailView.isOpen) {
            songDetailView.hide();
            return true;
        } else if (!searchHistory.isEmpty()) {
            popFromSearchHistory();
            return true;
        }
        return false;
    }

    private void popFromSearchHistory() {
        SearchResults searchResult = searchHistory.get(searchHistory.size() - 1);
        if(searchResult.tracks != null){
            changeAdapter(Track.class);
            trackListAdapter.setSearchQuery(searchResult.searchQuery);
            searchView.setQuery(searchResult.searchQuery, false);

            pulledPlaylists.clear();
            pulledTracks.clear();
            pulledTracks.addAll(searchResult.tracks);

            trackListAdapter.notifyDataSetChanged();

        }else if (searchResult.playlists != null){
            changeAdapter(PlaylistSimple.class);
            searchView.setQuery("", false);

            pulledTracks.clear();
            pulledPlaylists.clear();
            pulledPlaylists.addAll(searchResult.playlists);

            trackListAdapter.notifyDataSetChanged();
        }

        searchHistory.remove(searchHistory.size() - 1);
    }

    public void addToPlaylist(Track currentTrack) {
        partyActivity.addToPlaylist(currentTrack.id);
    }

    private class SearchResults {
        String searchQuery = "";
        ArrayList<Track> tracks;
        ArrayList<PlaylistSimple> playlists;

        public SearchResults(Class className, ArrayList<Object> results){
            sharedConst(className, results, null);
        }

        public SearchResults(Class className, ArrayList<Object> results, String searchQuery) {
            sharedConst(className, results, searchQuery);
        }

        private void sharedConst(Class className, ArrayList<Object> results, String searchQuery){
            this.searchQuery = searchQuery;

            if (className == Track.class) {
                tracks = new ArrayList<>();
                for (int i = 0; i < results.size(); i++)
                    tracks.add((Track) (results.get(i)));
            } else if (className == PlaylistSimple.class) {
                playlists = new ArrayList<>();
                for (int i = 0; i < results.size(); i++)
                    playlists.add((PlaylistSimple) (results.get(i)));
            }
        }

    }
}
