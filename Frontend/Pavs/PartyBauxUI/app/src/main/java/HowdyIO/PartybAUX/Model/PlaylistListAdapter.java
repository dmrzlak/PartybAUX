package HowdyIO.PartybAUX.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import PartyBauxUI.R;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Chris on 1/24/2019.
 */

public class PlaylistListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public Context context;
    private RecyclerView recyclerView;
    private List<PlaylistSimple> playlistResults;
    private OnItemClickListener itemClickListener;

    private String searchQuery;

    final int TYPE_HEADER = 1;
    final int TYPE_ITEM = 0;

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

    public PlaylistListAdapter(List<PlaylistSimple> playlistResults, RecyclerView recyclerView) {
        this.playlistResults = playlistResults;
        this.recyclerView = recyclerView;

        this.context = recyclerView.getContext();
    }

    public PlaylistListAdapter(String query, List<PlaylistSimple> playlistResults, RecyclerView recyclerView) {
        this.searchQuery = query;
        this.playlistResults = playlistResults;
        this.recyclerView = recyclerView;

        this.context = recyclerView.getContext();
    }

    public void setSearchQuery(String query){
        this.searchQuery = query;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        if(viewType == TYPE_HEADER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_header, parent, false);
            viewHolder = new VHSearchResultHeader(v);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_playlist, parent, false);
            viewHolder = new VHSearchResultPlaylist(v);
        }
        return viewHolder;
    }


    @Override public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder genericVH, int position) {
        if(genericVH instanceof VHSearchResultPlaylist) {
            VHSearchResultPlaylist holder = (VHSearchResultPlaylist) genericVH;
            final PlaylistSimple playlist = playlistResults.get(searchQuery == null? holder.getAdapterPosition(): holder.getAdapterPosition() - 1);
            if (playlist.images != null)
                Glide.with(context).load(playlist.images.get(0).url).into(holder.albumImage);
            holder.trackTitle.setText(playlist.name);
            StringBuilder artistsString = new StringBuilder();
            /*
            for (int i = 0; i < playlist; i++) {
                artistsString.append(searchResult.artists.get(i).name);
                if (i < searchResult.artists.size() - 1) artistsString.append(", ");
            }
            holder.trackArtists.setText(artistsString);*/

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) itemClickListener.onItemClick(v, playlist);
                }
            });
        }else{
            VHSearchResultHeader holder = (VHSearchResultHeader) genericVH;
            holder.query.setText(searchQuery);
        }
    }

    @Override
    public int getItemCount() {
        int count = playlistResults.size();
        if(searchQuery != null) count++;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && searchQuery != null)
            return TYPE_HEADER;

        return super.getItemViewType(position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(View view, PlaylistSimple playlist);
    }
}
