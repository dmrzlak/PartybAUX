package HowdyIO.PartybAUX.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import HowdyIO.PartybAUX.Activities.MainActivity;
import HowdyIO.PartybAUX.Fragments.SearchFragment;
import HowdyIO.PartybAUX.Views.TwoWayAnimatedDrawable;
import PartyBauxUI.R;
import de.androidpit.androidcolorthief.MMCQ;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Chris on 1/26/2019.
 */

public class SongDetailView extends RelativeLayout {

    Context context;
    SearchFragment searchFragment;

    private View root;
    private RelativeLayout contentView;
    private View backgroundGradient;
    MainActivity mainActivity;
    RecyclerView recyclerView;

    private Track currentTrack;

    private CardView albumWrapper;
    private ImageView album;
    private TextView trackTitle;
    private TextView artistList;
    private RelativeLayout goBack;
    private CardView addToPlaylist;
    private ImageView addToCheckmark;

    private boolean needsImage;
    public static boolean isOpen = false;

    public SongDetailView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SongDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SongDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        root = inflate(getContext(), R.layout.view_song_details, this);
        //root = LayoutInflater.from(context).inflate(R.layout.view_song_details, this, false);
        root.post(new Runnable() {
            @Override
            public void run() {
                contentView = root.findViewById(R.id.content_view);
                backgroundGradient = root.findViewById(R.id.background_gradient);
                albumWrapper = root.findViewById(R.id.wrapper_album_image);
                album = root.findViewById(R.id.imageview_album);
                trackTitle = root.findViewById(R.id.textview_track_title);
                artistList = root.findViewById(R.id.textview_track_artists);
                goBack = root.findViewById(R.id.back_button);
                addToPlaylist = root.findViewById(R.id.button_add_to_playlist);
                addToCheckmark = root.findViewById(R.id.imageanim_add_to_checkmark);
                root.setVisibility(INVISIBLE);
                //contentView.getBackground().setAlpha((int) (255 * 3/4f));
                setY(getHeight());

                setUpDrawableAnim();
                setUpOnClicks();
            }
        });
    }

    TwoWayAnimatedDrawable twoWayAnimatedDrawable;
    private void setUpDrawableAnim() {
        twoWayAnimatedDrawable = new TwoWayAnimatedDrawable(addToCheckmark,
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.add_to_checkmark),
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.checkmark_to_add));

    }

    private void setUpOnClicks(){
        goBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        addToPlaylist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFragment.addToPlaylist(currentTrack);
                twoWayAnimatedDrawable.start();
            }
        });
    }

    public void setContent(Track track, SearchFragment searchFragment){
        this.searchFragment = searchFragment;
        if(track == currentTrack)return;
        twoWayAnimatedDrawable.setState(TwoWayAnimatedDrawable.DrawableState.STATE1);
        backgroundGradient.setAlpha(0f);
        backgroundGradient.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.background)));

        trackTitle.setText(track.name);
        StringBuilder aList = new StringBuilder();
        for(int i = 0; i < track.artists.size(); i++){
            aList.append(track.artists.get(i).name);
            if(i < track.artists.size() - 1)aList.append(", ");
        }
        artistList.setText(aList);


        currentTrack = track;

        album.setAlpha(0f);
        albumWrapper.setAlpha(0f);

        Glide.with(context).load(currentTrack.album.images.get(0).url).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                album.animate().setDuration(150).alpha(1f).start();
                albumWrapper.animate().setDuration(150).alpha(1f).start();
                new SongGradientTask().execute(resource);

                needsImage = false;
                return false;
            }
        }).into(album);
    }


    public void show(){
        isOpen = true;
        if(root.getVisibility() != VISIBLE)
            root.setVisibility(VISIBLE);
        clearAnimation();
        animate().y(0).setDuration(150).setInterpolator(new DecelerateInterpolator()).start();
/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finishSetContent();
            }
        },175);*/
    }

    public void hide(){
        isOpen = false;
        animate().y(getHeight()).setDuration(150).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                finish();
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
            }
            private void finish(){
                if(!isOpen)
                    setVisibility(View.INVISIBLE);
            }
        }).start();
    }

    private class SongGradientTask extends AsyncTask<Drawable, Void, GradientDrawable>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected GradientDrawable doInBackground(Drawable... drawables) {
            List<int[]> result = new ArrayList<>();
            Bitmap imgBitmap = ((BitmapDrawable)drawables[0]).getBitmap();
            try {
                result = MMCQ.compute(imgBitmap, 5);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            GradientDrawable gradientDrawable = new GradientDrawable();
            int[] dominantColor = result.get(0);
            int domColorInt = Color.rgb(dominantColor[0], dominantColor[1], dominantColor[2]);
            int primary =  ContextCompat.getColor(context, R.color.colorPrimary);
            gradientDrawable.setColors(new int[]{domColorInt, ContextCompat.getColor(context, R.color.background), primary, primary});
            //gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            gradientDrawable.setGradientRadius(4000);
            gradientDrawable.setGradientCenter(0.5f, -0.1f);

            return gradientDrawable;
        }

        @Override
        protected void onPostExecute(GradientDrawable drawable) {
            if(drawable == null){
                needsImage = true;
                return;
            }
            backgroundGradient.setBackground(drawable);
            backgroundGradient.animate().alpha(1f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
            super.onPostExecute(drawable);
        }
    }
}
