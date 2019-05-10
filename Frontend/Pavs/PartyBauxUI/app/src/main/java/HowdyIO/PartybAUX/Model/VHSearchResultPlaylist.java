package HowdyIO.PartybAUX.Model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import PartyBauxUI.R;

/**
 * Created by Chris on 1/24/2019.
 */

public class VHSearchResultPlaylist extends RecyclerView.ViewHolder {

    TextView trackTitle;
    TextView trackArtists;
    ImageView albumImage;
    public VHSearchResultPlaylist(View itemView) {
        super(itemView);
        trackTitle = itemView.findViewById(R.id.textview_track_title);
        //trackArtists = itemView.findViewById(R.id.textview_track_artists);
        albumImage = itemView.findViewById(R.id.imageview_album);
    }
}
