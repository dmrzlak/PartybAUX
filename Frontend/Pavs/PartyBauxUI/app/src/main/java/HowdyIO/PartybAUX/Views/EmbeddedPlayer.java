package HowdyIO.PartybAUX.Views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import HowdyIO.PartybAUX.Model.Callback;
import HowdyIO.PartybAUX.Model.HybridTrack;
import HowdyIO.PartybAUX.Model.SpotifyPlayer;
import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Utils.GradientGenerator;
import PartyBauxUI.R;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Chris on 4/22/2019.
 */

public class EmbeddedPlayer extends RelativeLayout implements SpotifyPlayer.PlayerCallback, SpotifyPlayer.OnSeekListener {

    private Context context;

    private View root;
    private View backgroundGradient;
    private RelativeLayout wTitleText;
    private ScrollView titleScroller;
    private CustomTextView trackTitle;
    private SeekBar seekBar;
    private CustomTextView seekTimeStart;
    private CustomTextView seekTimeEnd;
    private ImageView playButton;
    private CardView wPlayButton;
    private RelativeLayout wSkipCurButton;
    private ViewGroup addSongButton;
    private TwoWayAnimatedDrawable twoWayAnimatedDrawable;

    private HybridTrack currentTrackPlaying;
    private TrackProgressBar trackProgressBar;
    private OnClickListener addSongListener;
    private OnClickListener songClickListener;

    private boolean playLocked;
    private boolean seekBarEnabled;

    public EmbeddedPlayer(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public EmbeddedPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //assignAttr(attrs);
        init();
    }

    public EmbeddedPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        //assignAttr(attrs);
        init();
    }

/*    private void assignAttr(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SongDetailView);

        String type = a.getString(R.styleable.SongDetailView_type);

        if (type != null) {
            if (type.equals("1"))
                setLayoutType(SongDetailView.LAYOUT_TYPE.TYPE_SEARCH_RESULT);
            else
                setLayoutType(SongDetailView.LAYOUT_TYPE.TYPE_PLAYER);
        }

        a.recycle();
    }*/

    private void init() {
        root = inflate(getContext(), R.layout.view_embedded_player, this);
/*        contentView = root.findViewById(R.id.content_view);
        wAlbum = root.findViewById(R.id.wrapper_album_image);
        album = root.findViewById(R.id.imageview_album);
        trackTitle = root.findViewById(R.id.textview_track_title);
        artistList = root.findViewById(R.id.textview_track_artists);
        goBack = root.findViewById(R.id.back_button);*/
        backgroundGradient = root.findViewById(R.id.background_gradient);
        wTitleText = root.findViewById(R.id.wrapper_title_text);
        titleScroller = root.findViewById(R.id.scrollview_track_title);
        trackTitle = root.findViewById(R.id.textview_track_title);
        seekBar = root.findViewById(R.id.seek_bar);
        seekTimeStart = root.findViewById(R.id.textview_seekbar_start);
        seekTimeEnd = root.findViewById(R.id.textview_seekbar_end);
        wPlayButton = root.findViewById(R.id.wrapper_button_play);
        playButton = root.findViewById(R.id.imageview_play_button);
        wSkipCurButton = root.findViewById(R.id.wrapper_button_skip);
        addSongButton = root.findViewById(R.id.wrapper_button_add);
        //contentView.getBackground().setAlpha((int) (255 * 3/4f));

        setUpDrawableAnim();
        setUpOnClicks();
        setUpSeekBar();

        setDefault();

        SpotifyPlayer.addPlayerCallback(this);
        SpotifyPlayer.setOnSeekListener(this);
    }

    private void setUpDrawableAnim() {
        twoWayAnimatedDrawable = new TwoWayAnimatedDrawable(playButton,
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.play_to_pause),
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.pause_to_play));

        twoWayAnimatedDrawable.setStateChangedListener(new TwoWayAnimatedDrawable.StateChangedListener() {
            @Override
            public void onStateChanged(TwoWayAnimatedDrawable.DrawableState lastState, TwoWayAnimatedDrawable.DrawableState currentState) {
                playLocked = false;
            }
        });
    }

    private void setUpOnClicks() {
        wPlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playLocked || SpotifyPlayer.queueArray().isEmpty()) return;
                playLocked = true;
                twoWayAnimatedDrawable.start();

                if (twoWayAnimatedDrawable.getState() == TwoWayAnimatedDrawable.DrawableState.STATE1) {
                    SpotifyPlayer.seekAndPlay(trackProgressBar.getProgress());
                }else
                    SpotifyPlayer.pause();

            }
        });

        wSkipCurButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyPlayer.skipForward();
            }
        });

        addSongButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addSongListener != null) addSongListener.onClick(v);
            }
        });

        wTitleText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songClickListener != null) songClickListener.onClick(v);
            }
        });
    }

    private void setUpSeekBar() {
        trackProgressBar = new TrackProgressBar(seekBar);

        seekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !seekBarEnabled;
            }
        });
    }

    private void notifyPlaying(boolean isPlaying) {
        if (trackProgressBar != null) {
            if (isPlaying)
                trackProgressBar.unpause();
            else
                trackProgressBar.pause();
        }
        if (playLocked || twoWayAnimatedDrawable == null) return;

        if (isPlaying)
            twoWayAnimatedDrawable.setState(TwoWayAnimatedDrawable.DrawableState.STATE2);
        else
            twoWayAnimatedDrawable.setState(TwoWayAnimatedDrawable.DrawableState.STATE1);

        playLocked = false;
    }

    public void show() {
        root.setVisibility(VISIBLE);
        root.setAlpha(1f);
    }

    public void hide() {
        root.setVisibility(GONE);
        root.setAlpha(0f);
    }

    public void setAddSongListener(OnClickListener addSongListener) {
        this.addSongListener = addSongListener;
    }

    public void setSongClickListener(OnClickListener songClickListener) {
        this.songClickListener = songClickListener;
    }

    @Override
    public void onPlayTrack(HybridTrack track) {
        seekBarEnabled = true;
        currentTrackPlaying = track;
//        if (uriForGradient.isEmpty()) uriForGradient = currentTrackPlaying.uri;

        if (trackTitle == null) return;
        StringBuilder title = new StringBuilder();
        title.append(track.name);
        title.append(" - ");
        if (!track.artists.isEmpty()) {
            for (int i = 0; i < track.artists.size() - 1; i++) {
                title.append(track.artists.get(i).name);
                title.append(", ");
            }
            title.append(track.artists.get(track.artists.size() - 1).name);
        }

        setTrackTitle(title.toString());

        //  There's a chance they change the song after 1000ms
        //  Let's save what song we're currently tracking just in case
        final String currentUri = track.uri;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!currentUri.equals(currentTrackPlaying.uri)) return;

                showGradient(true);
            }
        }, 1000);
    }

    private void setTrackTitle(String title) {
        if (trackTitle.getText().equals(title)) return;
        trackTitle.setText(title);
        trackTitle.post(new Runnable() {
            @Override
            public void run() {
                boolean ellipsized = isTextViewEllipsized(trackTitle);
                trackTitle.setHorizontallyScrolling(ellipsized);
                trackTitle.setSelected(ellipsized);

                        /*if(trackTitle.getLineCount() > 1) {
                            trackTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            trackTitleScrolling = true;
                            titleScrollingSet = false;
                        }*/

                //trackTitle.animate().alpha(1f).setStartDelay(250).setDuration(250).start();
            }
        });
/*        trackTitle.animate().alpha(0f).setDuration(250).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(trackTitle != null){
                    //trackTitle.setEllipsize(null);
                    trackTitle.setText(title);
                }


            }
        }, 250);*/


    }

    private boolean isTextViewEllipsized(final TextView textView) {
        Layout layout = textView.getLayout();
        if (layout != null) {
            int lines = layout.getLineCount();
            if (lines > 0) {
                int ellipsisCount = layout.getEllipsisCount(lines - 1);
                if (ellipsisCount > 0) {
                    return true;
                }
            }
        }
        String debug = "aye";
        return false;
    }

    private void showGradient(boolean show) {
        if (backgroundGradient == null) return;

        if (backgroundGradient.getBackground() == null) {
            backgroundGradient.animate().alpha(0f);
            stopGradientRunnable();
        }

        backgroundGradient.animate().alpha(show ? 0.5f : 0f).setInterpolator(new DecelerateInterpolator()).setDuration(1000).start();
    }

    String uriForGradient = "";

    public void loadInGradient(Drawable resource) {
        if (currentTrackPlaying != null && uriForGradient.equals(currentTrackPlaying.uri))
            return; //We know we've already loaded this gradient in
        if (currentTrackPlaying != null)
            uriForGradient = currentTrackPlaying.uri;

        GradientGenerator.generateGradient(context, resource, new GradientGenerator.GradientCallBack() {
            @Override
            public void onResult(GradientDrawable gradientDrawable) {
                if (backgroundGradient == null) return;

                backgroundGradient.animate().alpha(0f).setDuration(150).start();
                if (gradientDrawable == null) return;
                gradientDrawable.setGradientCenter(0f, 1.1f);
                gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                gradientDrawable.setGradientRadius(2000);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (backgroundGradient != null) {
                            backgroundGradient.setBackground(gradientDrawable);
                            startGradientHandler(gradientDrawable);
                        }
                    }
                }, 150);
            }

            @Override
            public void onError() {
            }
        });
    }

    Handler gradientHandler = new Handler();
    Runnable gradientRunnable;
    ValueAnimator radiusAnim = null;
    private void startGradientHandler(GradientDrawable gradientDrawable) {
        if (backgroundGradient == null || gradientDrawable == null) return;
        final int startR = 2000;
        final int endR = 6000;
        final int halfway = startR + (endR - startR) / 2;
        final int animDuration = 20000;

        if(gradientRunnable != null)
            gradientHandler.removeCallbacks(gradientRunnable);

        gradientRunnable = new Runnable() {
            @Override
            public void run() {
                float currentRadius = gradientDrawable.getGradientRadius();

                if(radiusAnim != null) radiusAnim.removeAllUpdateListeners();

                radiusAnim = ValueAnimator.ofFloat(currentRadius, currentRadius > halfway? startR: endR);
                radiusAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        gradientDrawable.setGradientRadius((float) animation.getAnimatedValue());
                        backgroundGradient.setBackground(gradientDrawable);
                    }
                });
                radiusAnim.setDuration(animDuration).start();
                gradientHandler.postDelayed(this, animDuration);
            }
        };
        gradientHandler.post(gradientRunnable);
    }

    private void stopGradientRunnable(){
        if(gradientRunnable != null)
            gradientHandler.removeCallbacks(gradientRunnable);
    }

    public void refresh(Track playing){
        seekBarEnabled = true;
        getMostRecentPlaybackPos();

        if(playing == null || (currentTrackPlaying != null && playing.uri.equals(currentTrackPlaying.uri)) ) return;
        onPlayTrack(new HybridTrack(playing));
    }

    private void getMostRecentPlaybackPos(){
        SpotifyPlayer.getCurrentPlaybackPosition(new Callback() {
            @Override
            public void onResult(String result) {
                try {
                    long playbackPos = Long.valueOf(result);
                    seekBar.setProgress((int) playbackPos);
                }catch (NumberFormatException e){
                    seekBar.setProgress(0);
                }
            }
        });
    }

    @Override
    public void onPlayPause(boolean isPlaying) {
        seekBarEnabled = true;
        notifyPlaying(isPlaying);
    }

    @Override
    public void onTracksRemoved(ArrayList<String> tracks) {

    }

    @Override
    public void onDurationSet(long duration) {
        seekBarEnabled = true;
        trackProgressBar.setDuration(duration);
        seekTimeEnd.setText(milliSecondsToTimer(duration));
    }

    @Override
    public void onSeek(long playbackPosMs) {
        trackProgressBar.update(playbackPosMs);
    }

    public void setDefault() {
        trackTitle.setText("Add something " + QuickTools.getRandomAdjective());
        seekTimeStart.setText("0:00");
        seekTimeEnd.setText("");
        seekBar.setProgress(0);
        seekBarEnabled = false;
        //seekBar.setEnabled(false);
        if(backgroundGradient != null)
            backgroundGradient.animate().alpha(0f).setDuration(250).start();
    }

    private class TrackProgressBar {

        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;


        private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long maxDuration = currentTrackPlaying != null? currentTrackPlaying.duration: -1;
                if(maxDuration > 0 && progress >= maxDuration - 2000) {
                    SpotifyPlayer.skipForward();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(currentTrackPlaying == null || seekBar.getMax() <= 0) return;
                if(seekBar.getProgress() > seekBar.getMax() - 2000)
                    SpotifyPlayer.skipForward();
                else
                    SpotifyPlayer.seekTo(seekBar.getProgress());
            }
        };

        private final Runnable mSeekRunnable = new Runnable() {
            @Override
            public void run() {
                int progress = mSeekBar.getProgress();
                mSeekBar.setProgress(progress + LOOP_DURATION);
                refreshTimeText(progress);
                mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
            }
        };

        private TrackProgressBar(SeekBar seekBar) {
            mSeekBar = seekBar;
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
            mHandler = new Handler();
        }

        private void setDuration(long duration) {
            mSeekBar.setProgress(0);
            mSeekBar.setMax((int) duration);
        }

        private void update(long progress) {
            mSeekBar.setProgress((int) progress);
            refreshTimeText(progress);
        }

        private void refreshTimeText(long progress) {
            seekTimeStart.setText(milliSecondsToTimer(progress));
        }

        private void pause() {
            //trackTitle.setContinuousScrolling(false);

            mHandler.removeCallbacks(mSeekRunnable);
        }

        private void unpause() {
            if (mSeekBar.getMax() == 0)
                SpotifyPlayer.setForceSeekUpdate(true);
            //trackTitle.setContinuousScrolling(true);

            mHandler.removeCallbacks(mSeekRunnable);
            mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
        }

        public int getProgress(){
            if(seekBar == null || currentTrackPlaying == null) return 0;

            /*long currentDuration = ((int) (currentTrackPlaying.duration / 10f)) * 10;
            long seekDuration = ((int) (seekBar.getMax() / 10f)) * 10;

            if(currentDuration != seekDuration) return 0;*/

            return seekBar.getProgress();
        }
    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    private long getDuration() {
        if (currentTrackPlaying == null) return 0;
        return currentTrackPlaying.duration;
    }

    public int getRscForPlayerHeight(){
        return R.dimen.item_size_xxlarge;
    }

}
