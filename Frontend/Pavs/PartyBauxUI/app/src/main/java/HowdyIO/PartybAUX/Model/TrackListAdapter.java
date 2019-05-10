package HowdyIO.PartybAUX.Model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import PartyBauxUI.R;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Chris on 1/24/2019.
 */

public class TrackListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context context;
    private RecyclerView recyclerView;
    private List<Track> trackResults;
    private List<PlaylistSimple> playlistResults;
    private OnItemClickListener itemClickListener;
    private OnImageLoadedListener imageLoadedListener;

    private String searchQuery;
    private HybridTrack spTrack;
    private boolean isPlaying;

    private final int TYPE_HEADER = 1;
    private final int TYPE_ITEM = 0;

    private ADAPTER_TYPE adapterType;

    public enum ADAPTER_TYPE {
        TYPE_SEARCH, TYPE_PARTY
    }

//    private onItemClickedListener itemClickedListener;
//    private View.OnLongClickListener longClickListener;

/*    public TrackListAdapter(List<Parcelable> resultsList, Class listType, RecyclerView recyclerView){
        this.recyclerView = recyclerView;
        this.context = recyclerView.getContext();
        if(listType == Track.class){
            constructAs((List<Track>)(List<?>)resultsList);
        }else if(listType == AlbumSimple.class){
            constructAs((List<Track>)(List<?>)resultsList);
        }
    }*/

    public TrackListAdapter(List<Track> trackResults, RecyclerView recyclerView) {
        adapterType = ADAPTER_TYPE.TYPE_PARTY;

        this.trackResults = trackResults;
        this.recyclerView = recyclerView;
        this.context = recyclerView.getContext();

        SpotifyPlayer.addPlayerCallback(getPlayerCallback());
    }

    public TrackListAdapter(String query, List<Track> trackResults, RecyclerView recyclerView) {
        adapterType = ADAPTER_TYPE.TYPE_SEARCH;

        this.searchQuery = query;
        this.trackResults = trackResults;
        this.recyclerView = recyclerView;
        this.context = recyclerView.getContext();

    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        if (viewType == TYPE_HEADER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_header, parent, false);
            viewHolder = new VHSearchResultHeader(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_track, parent, false);
            viewHolder = new VHSearchResultTrack(v);
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder genericVH, int position) {
        if (genericVH instanceof VHSearchResultTrack) {
            VHSearchResultTrack holder = (VHSearchResultTrack) genericVH;
            if (holder.getAdapterPosition() == 0) {
                holder.setIsCurrentSong(true);
                holder.setPlaying(isPlaying);
            } else
                holder.setIsCurrentSong(false);

            final Track searchResult = trackResults.get(searchQuery == null ? holder.getAdapterPosition() : holder.getAdapterPosition() - 1);
            loadAlbumImage(holder, searchResult.album);

            holder.trackTitle.setText(searchResult.name);
            StringBuilder artistsString = new StringBuilder();
            for (int i = 0; i < searchResult.artists.size(); i++) {
                artistsString.append(searchResult.artists.get(i).name);
                if (i < searchResult.artists.size() - 1) artistsString.append(", ");
            }
            holder.trackArtists.setText(artistsString);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) itemClickListener.onItemClick(v, searchResult);
                }
            });
        } else {
            VHSearchResultHeader holder = (VHSearchResultHeader) genericVH;
            holder.query.setText(searchQuery);
        }
    }

    private void loadAlbumImage(VHSearchResultTrack holder, AlbumSimple album){
        if (album != null) {
            Glide.with(context).load(album.images.get(0).url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {return false;}

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    if(imageLoadedListener != null && holder.getAdapterPosition() == 0) imageLoadedListener.onImageLoaded(resource);
                    return false;
                }
            }).into(holder.albumImage);
        }
    }

    private void setPlaying(boolean isPlaying) {

    }

    @Override
    public int getItemCount() {
        int count = trackResults.size();
        if (searchQuery != null) count++;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && searchQuery != null)
            return TYPE_HEADER;

        return super.getItemViewType(position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setImageLoadedListener(OnImageLoadedListener imageLoadedListener){
        this.imageLoadedListener = imageLoadedListener;
    }

    private SpotifyPlayer.PlayerCallback playerCallback;

    public SpotifyPlayer.PlayerCallback getPlayerCallback() {
        return playerCallback != null ? playerCallback :
                new SpotifyPlayer.PlayerCallback() {
                    @Override
                    public void onPlayTrack(HybridTrack track) {
                        spTrack = track;
                    }

                    @Override
                    public void onPlayPause(boolean playing) {
                        if (spTrack != null && playlistResults != null && playlistResults.get(0).uri.equals(spTrack.uri))
                            isPlaying = playing;
                    }

                    @Override
                    public void onTracksRemoved(ArrayList<String> trackUri) { //Remove Track from Playlist when removed from Player

                    }
                };
    }

    public Track getCurrentlyPlaying(){
        return trackResults.get(0);
    }


    public interface OnItemClickListener {
        void onItemClick(View view, Track track);
    }

    public interface OnImageLoadedListener{
        void onImageLoaded(Drawable resource);
    }
}
