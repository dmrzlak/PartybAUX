package HowdyIO.PartybAUX.Model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import PartyBauxUI.R;

/**
 * Created by Chris on 1/24/2019.
 */

public class VHSearchResultTrack extends RecyclerView.ViewHolder {

    Context context;

    TextView trackTitle;
    TextView trackArtists;
    ImageView albumImage;
    ImageView infoButton;
    ImageView playButton;
    Drawable play;
    Drawable pause;

    boolean playing;
    boolean isCurrentSong;
    public VHSearchResultTrack(View itemView) {
        super(itemView);
        trackTitle = itemView.findViewById(R.id.textview_track_title);
        trackArtists = itemView.findViewById(R.id.textview_track_artists);
        albumImage = itemView.findViewById(R.id.imageview_album);
        infoButton = itemView.findViewById(R.id.imageview_info_button);
        playButton = itemView.findViewById(R.id.imageview_play_button);

        this.context = itemView.getContext();
    }

    public void setIsCurrentSong(boolean isCurrentSong){
        this.isCurrentSong = isCurrentSong;
        if(play == null || pause == null){
            play = ContextCompat.getDrawable(context, R.drawable.baseline_play_arrow_black_24);
            play.setTint(ContextCompat.getColor(context, R.color.spotGreen));
            pause = ContextCompat.getDrawable(context, R.drawable.baseline_pause_black_24);
            pause.setTint(ContextCompat.getColor(context, R.color.goodPurp));
        }

        infoButton.setVisibility(playing? View.INVISIBLE: View.VISIBLE);
        playButton.setVisibility(playing? View.VISIBLE: View.INVISIBLE);
    }

    public void setPlaying(boolean playing) {
        if(play == null || pause == null) return;

        if(playing)
            playButton.setBackground(play);
        else
            playButton.setBackground(pause);
    }
}
