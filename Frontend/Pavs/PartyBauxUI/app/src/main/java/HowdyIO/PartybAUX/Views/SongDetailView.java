package HowdyIO.PartybAUX.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import HowdyIO.PartybAUX.Model.SpotifyPlayer;
import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Tools.SwipeGestureDetector;
import HowdyIO.PartybAUX.Utils.GradientGenerator;
import PartyBauxUI.R;
import kaaes.spotify.webapi.android.models.Track;

import static HowdyIO.PartybAUX.Views.SongDetailView.LAYOUT_TYPE.TYPE_PLAYER;

/**
 * Created by Chris on 1/26/2019.
 */

public class SongDetailView extends RelativeLayout {

    private int userType;
    private Context context;

    private View root;
    private RelativeLayout contentView;
    private View backgroundGradient;

    private Track currentTrack;

    private CardView wAlbum;
    private ImageView album;
    private TextView trackTitle;
    private TextView artistList;
    private RelativeLayout goBack;
    private RelativeLayout playerViewContainer;
    private LinearLayout searchResultContainer;
    private CardView addToPlaylist;
    private View addToPlaylistSelector;
    private CardView skipTo;
    private ImageView addToCheckmark;

    public static boolean isOpen = false;
    private OnCloseListener closeListener;
    private OnAddToPlaylistListener addToPlayListListener;
    private int startY;

    private LAYOUT_TYPE layoutType;

    public enum LAYOUT_TYPE {
        TYPE_SEARCH_RESULT, TYPE_PLAYER
    }

    public SongDetailView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SongDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        assignAttr(attrs);
        init();
    }

    public SongDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        assignAttr(attrs);
        init();
    }

    private void assignAttr(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SongDetailView);

        String type = a.getString(R.styleable.SongDetailView_type);

        if (type != null) {
            if (type.equals("1"))
                setLayoutType(LAYOUT_TYPE.TYPE_SEARCH_RESULT);
            else
                setLayoutType(LAYOUT_TYPE.TYPE_PLAYER);
        }

        a.recycle();
    }

    private void init() {
        root = inflate(getContext(), R.layout.view_song_details, this);
        //root = LayoutInflater.from(context).inflate(R.layout.view_song_details, this, false);
        root.post(new Runnable() {
            @Override
            public void run() {
                contentView = root.findViewById(R.id.content_view);
                backgroundGradient = root.findViewById(R.id.background_gradient);
                wAlbum = root.findViewById(R.id.wrapper_album_image);
                album = root.findViewById(R.id.imageview_album);
                trackTitle = root.findViewById(R.id.textview_track_title);
                artistList = root.findViewById(R.id.textview_track_artists);
                goBack = root.findViewById(R.id.back_button);
                playerViewContainer = root.findViewById(R.id.container_in_player);
/*                wPlayButton = root.findViewById(R.id.wrapper_button_play);
                wSkipCurButton = root.findViewById(R.id.wrapper_button_skip);*/
                skipTo = root.findViewById(R.id.button_skip_to);
                searchResultContainer = root.findViewById(R.id.container_search_result);
                addToPlaylist = root.findViewById(R.id.button_add_to_playlist);
                addToPlaylistSelector = root.findViewById(R.id.selector_purple);
                addToCheckmark = root.findViewById(R.id.imageanim_add_to_checkmark);
                root.setVisibility(INVISIBLE);
                //contentView.getBackground().setAlpha((int) (255 * 3/4f));
                startY = (int) getY();
                setY(getHeight());

                setUpDrawableAnim();
                setUpOnClicks();
                //setUpGestureDetector();
            }
        });
    }

    public void setUserType(int userType){
        this.userType = userType;
    }

    TwoWayAnimatedDrawable twoWayAnimatedDrawable;

    private void setUpDrawableAnim() {
        twoWayAnimatedDrawable = new TwoWayAnimatedDrawable(addToCheckmark,
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.add_to_checkmark),
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.checkmark_to_add));

    }

    private void setUpOnClicks() {
        goBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        skipTo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyPlayer.skipTo(SpotifyPlayer.indexOf(currentTrack.uri.substring(14)));
                hide();
            }
        });

        addToPlaylist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addToPlayListListener != null)
                    addToPlayListListener.onClicked(currentTrack,
                            twoWayAnimatedDrawable.getState() == TwoWayAnimatedDrawable.DrawableState.STATE1);
                twoWayAnimatedDrawable.start();
            }
        });
/*        findViewById(R.id.scrollView).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });*/
    }

    private void setUpGestureDetector() {
        SwipeGestureDetector swipeTouchListener = new SwipeGestureDetector(context) {
            public boolean onSwipeTop() {

                return true;
            }

            public boolean onSwipeRight() {

                return true;
            }

            public boolean onSwipeLeft() {

                return true;
            }

            public boolean onSwipeBottom() {
                hide();
                return true;
            }
        };
        contentView.setOnTouchListener(swipeTouchListener);
        goBack.setOnTouchListener(swipeTouchListener);
        ((ViewGroup) album.getParent()).setOnTouchListener(swipeTouchListener);

    }

    public void setLayoutType(LAYOUT_TYPE layoutType) {
        this.layoutType = layoutType;
    }

    public SongDetailView setContent(Track track) {
        return setContent(track, false);
    }

    public SongDetailView setContent(Track track, boolean isAdded) {
        if (layoutType == TYPE_PLAYER) {
            searchResultContainer.setVisibility(GONE);

            if(userType == QuickTools.ROLE_HOST) {
                playerViewContainer.setVisibility(VISIBLE);
                if(QuickTools.trackUriEquals(SpotifyPlayer.currentTrack(), (track.uri)))
                    skipTo.setVisibility(GONE);
                else
                    skipTo.setVisibility(VISIBLE);
            }
            else
                playerViewContainer.setVisibility(GONE);
        } else {
            playerViewContainer.setVisibility(GONE);

            searchResultContainer.setVisibility(VISIBLE);
            twoWayAnimatedDrawable.setState(!isAdded ? TwoWayAnimatedDrawable.DrawableState.STATE1 : TwoWayAnimatedDrawable.DrawableState.STATE2);
        }

        if(isAdded){
            addToPlaylist.setClickable(false);
            addToPlaylist.setFocusable(false);
            addToPlaylistSelector.setVisibility(GONE);
        }else{
            addToPlaylist.setClickable(true);
            addToPlaylist.setFocusable(true);
            addToPlaylistSelector.setVisibility(VISIBLE);
        }

        if (track == currentTrack)
            return this;
        else
            return build(track);
    }

    private SongDetailView build(Track track) {
        backgroundGradient.setAlpha(0f);
        backgroundGradient.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.background)));

        trackTitle.setText(track.name);
        StringBuilder aList = new StringBuilder();
        for (int i = 0; i < track.artists.size(); i++) {
            aList.append(track.artists.get(i).name);
            if (i < track.artists.size() - 1) aList.append(", ");
        }
        artistList.setText(aList);


        currentTrack = track;

        album.setAlpha(0f);
        wAlbum.setAlpha(0f);

        Glide.with(context).load(currentTrack.album.images.get(0).url).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                album.animate().setDuration(150).alpha(1f).start();
                wAlbum.animate().setDuration(150).alpha(1f).start();
                GradientGenerator.generateGradient(context, resource, new GradientGenerator.GradientCallBack() {
                    @Override
                    public void onResult(GradientDrawable gradientDrawable) {
                        backgroundGradient.setBackground(gradientDrawable);
                        backgroundGradient.animate().alpha(1f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
                    }
                    @Override
                    public void onError() {}
                });
                return false;
            }
        }).into(album);

        return this;
    }


    public void show() {
        isOpen = true;
        if (root.getVisibility() != VISIBLE)
            root.setVisibility(VISIBLE);
        clearAnimation();
        animate().y(startY).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
    }

    public void hide() {
        isOpen = false;
        animate().y(getHeight()).setDuration(200).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
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

            private void finish() {
                if (!isOpen)
                    setVisibility(View.INVISIBLE);
            }
        }).start();

        if (closeListener != null) closeListener.onClose();
    }

    public void setOnAddToPlaylistListener(OnAddToPlaylistListener addToPlaylistListener) {
        this.addToPlayListListener = addToPlaylistListener;
    }

    public void setOnCloseListener(OnCloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public interface OnAddToPlaylistListener {
        public void onClicked(Track track, boolean checked);
    }

    public interface OnCloseListener {
        public void onClose();
    }
}
