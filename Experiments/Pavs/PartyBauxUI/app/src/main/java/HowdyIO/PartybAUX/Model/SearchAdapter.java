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
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Chris on 1/24/2019.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchResultViewHolder>{

    public Context context;
    RecyclerView recyclerView;
    private List<Track> searchResults;
    private OnItemClickListener itemClickListener;

//    private onItemClickedListener itemClickedListener;
//    private View.OnLongClickListener longClickListener;

    public SearchAdapter(List<Track> searchResults, RecyclerView recyclerView) {
        this.searchResults = searchResults;
        this.recyclerView = recyclerView;

        this.context = recyclerView.getContext();
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_result, parent, false);
        return new SearchResultViewHolder(v);
    }


    @Override public void onBindViewHolder(@NonNull final SearchResultViewHolder holder, int position) {
        final Track searchResult = searchResults.get(holder.getAdapterPosition());
        Glide.with(context).load(searchResult.album.images.get(0).url).into(holder.albumImage);
        holder.trackTitle.setText(searchResult.name);
        StringBuilder artistsString = new StringBuilder();
        for(int i = 0; i < searchResult.artists.size(); i++) {
            artistsString.append(searchResult.artists.get(i).name);
            if(i < searchResult.artists.size() - 1)artistsString.append(", ");
        }
        holder.trackArtists.setText(artistsString);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null)itemClickListener.onItemClick(v, searchResult);
            }
        });
/*        holder.title.setText(item.getTitle());
        holder.replyText.setText(item.getReplyText());
        holder.contactsText.setText(getSomeContacts(item));

        if(item.getGradient() == null) setUpColorScheme(holder, item);
        holder.enabledInd.setBackground(item.getGradient());
        holder.disabledInd.setBackground(item.getBorder());
        setHandle(holder, item.isChecked());

        setEnabled(holder, item.isChecked(), false);

        //if(imageSize == -1)imageSize = holder.proPic.getLayoutParams().width;
        holder.itemView.setTag(item);*/
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }


    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(View view, Track track);
    }
}
