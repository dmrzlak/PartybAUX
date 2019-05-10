package HowdyIO.PartybAUX.Model;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;

import java.util.ArrayList;

import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Tools.SpotifyServiceUtil;
import HowdyIO.PartybAUX.Utils.DataProvider;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Chris on 3/10/2019.
 */

public class SpotifyPlayer {

    private static SpotifyAppRemote spotifyAppRemote;
    private static SongTimer songTimer;

    private static boolean isPlaying;
    private static boolean forceSeekUpdate;
    private static String trackPlayingInPlayer = "";
    private static String currentSongUri = "";
    private static long playbackPosition;
    private static Queue q;

    private static ArrayList<PlayerCallback> playerCallbacks;
    private static OnSeekListener onSeekListener;

    /**
     * Must be called to initialize the Spotify Player.
     * <p>
     * Sets our appRemote so we can make calls directly to Spotify
     * and "subscribes" to the phone's Spotify Player so we get live events
     * such as Play and Pause.
     *
     * @param spotifyAppRemote: Spotify's api for communication with their built-in player
     */
    public static void setSpotifyAppRemote(SpotifyAppRemote spotifyAppRemote) {
        SpotifyPlayer.spotifyAppRemote = spotifyAppRemote;
        songTimer = new SongTimer();



        spotifyAppRemote.getConnectApi()
                .connectSwitchToLocalDevice()
                .setResultCallback(connect -> {

                })
                .setErrorCallback(error -> logError(error.getMessage()));

        spotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(mPlayerStateEventCallback);

        q = new Queue();
    }

    public static boolean isPlaying() {
        return isPlaying;
    }

    public static boolean isPlaying(String trackUri) {
        return currentSongUri.equals(trackUri);
    }


    /**
     * Plays the first song in the Queue if it's not already playing
     */
    public static void playIfNotPlaying() {
        if (q.isEmpty() || QuickTools.userType != QuickTools.ROLE_HOST) return;
        spotifyAppRemote.getPlayerApi()
                .getPlayerState()
                .setResultCallback(playerState -> {
                    if (playerState.isPaused) {
                        if (QuickTools.trackUriEquals(trackPlayingInPlayer, q.peek()))
                            play(playbackPosition);
                        else
                            play();
                    }
                })
                .setErrorCallback(error -> logError("Could not play track: " +error.getMessage()));


    }

    /**
     * plays the first song the queue with palyback position zero (Start of the song)
     */
    private static void play() {
        play(0);
    }


    /**
     * Play the first song in the queue from a given position in ms
     *
     * @param playbackPosition Position to start playback
     */
    private static void play(long playbackPosition) {
        String uri = q.peek();

        SpotifyServiceUtil.getInstance().getTrack(uri, new Callback<Track>() {
            @Override
            public void success(Track track, Response response) {
                asyncPlay(track, playbackPosition);
            }

            @Override
            public void failure(RetrofitError error) {
                logError(error.getMessage());
            }
        });
    }

    /**
     * Async Play function, calls a timer to monitor the player
     *
     * @param track            track to be played
     * @param playbackPosition Position to start playback
     */
    public static void asyncPlay(Track track, long playbackPosition) {
        if (QuickTools.userType == QuickTools.ROLE_GUEST) return;


        spotifyAppRemote.getPlayerApi()
                .play(track.uri)
                .setResultCallback(empty -> {
                    currentSongUri = track.uri;
                    songTimer.start(track.duration_ms - playbackPosition);
                    logSuccess("Track was played");
                })
                .setErrorCallback(throwable -> logError(throwable.toString()));

    }

    /**
     * Removes the current song, and plays the next one
     */
    private static void playNext() {
        if (q.size() == 0)
            spotifyAppRemote.getPlayerApi().pause();
        else {
            String track;
            DataProvider.removeSong(QuickTools.partyID, (track = q.pop()), new HowdyIO.PartybAUX.Model.Callback() {
                @Override
                public void onResult(String result) {
                    ArrayList<String> tracks = new ArrayList<>();
                    tracks.add(track);
                    updateCallbacksForTrackRemoved(tracks);
                    if (result.equals("0"))
                        logSuccess("Song removed");
                    else
                        logError(result);
                }
            });
            play();
        }
    }


    /**
     * Fast forwards a song  to the given position
     *
     * @param playbackPositionMs Position to resume playing
     */
    public static void seekTo(long playbackPositionMs) {
        spotifyAppRemote.getPlayerApi()
                .seekTo(playbackPositionMs)
                .setResultCallback(empty -> {
                    logSuccess("Seek to " + playbackPositionMs);
                    playbackPosition = playbackPositionMs;
                })
                .setErrorCallback(throwable -> logError(throwable.toString()));
    }

    public static void seekAndPlay(long playbackPositionMs){
        if(!QuickTools.trackUriEquals(trackPlayingInPlayer, q.peek())){
            play(playbackPositionMs);
            return;
        }

        spotifyAppRemote.getPlayerApi()
                .seekTo(playbackPositionMs)
                .setResultCallback(empty -> {
                    playbackPosition = playbackPositionMs;
                    spotifyAppRemote.getPlayerApi().resume();
                })
                .setErrorCallback(throwable -> logError(throwable.toString()));

    }

    /**
     * pause the current song
     */
    public static void pause() {
        spotifyAppRemote.getPlayerApi().pause();
    }

    /**
     * Skip to the next song
     */
    public static void skipForward() {
        spotifyAppRemote.getPlayerApi().pause();
        playNext();
    }

    /**
     * Restart the current song
     */
    public static void restartCurrent() {
        spotifyAppRemote.getPlayerApi().seekTo(0);
    }

    public static void switchToCurrentTrackIfNeeded() {

    }

    /**
     * Queue Helper Methods
     */

    /**
     * If a song is in the queue
     *
     * @param uri uri of song to be checked
     * @return true is song is in queue
     */
    public static boolean isInQueue(String uri) {
        if (q != null)
            return q.contains(uri);

        return false;
    }

    /**
     * Given a list of tracks updates the local queue to what was given <br>
     * If a song is moved or removed, it will update
     *
     * @param tracks
     */
    public static void updateQueue(ArrayList<String> tracks) {
        q.update(tracks);
    }


    /**
     * Stops the player and resets the queue
     */
    public static void reset() {

        pause();
        q.clear();
        currentSongUri = "";
        playbackPosition = 0;

    }

    public static ArrayList<String> queueArray() {
        return q.toArray();
    }

    public static void getCurrentPlaybackPosition(HowdyIO.PartybAUX.Model.Callback callback) {
        spotifyAppRemote.getPlayerApi()
                .getPlayerState()
                .setResultCallback(playerState ->
                        callback.onResult("" + playerState.playbackPosition) )
                .setErrorCallback(error ->
                        callback.onResult("0"));
    }

    /**
     * Callback Methods so several classes can listen for updates to the player.
     */

    public static void addPlayerCallback(PlayerCallback playerCallback) {
        if (playerCallbacks == null)
            playerCallbacks = new ArrayList<>();

        //Replace a callback if its already registered as a listener.
        for (PlayerCallback callback : playerCallbacks) {
            if (callback.getClass().toString().equals(playerCallback.getClass().toString())) {
                playerCallbacks.set(playerCallbacks.indexOf(callback), playerCallback);
                return;
            }
        }

        playerCallbacks.add(playerCallback);
    }

    public static void removePlayerCallback(PlayerCallback playerCallback) {
        if (playerCallbacks == null) return;

        playerCallbacks.remove(playerCallback);
    }

    private static void updateCallbacksForPlayerState(PlayerState playerState, HybridTrack track) {
        if (playerCallbacks == null) return;

        for (PlayerCallback callback : playerCallbacks) {
            if (track != null && !trackPlayingInPlayer.equals(track.uri))
                callback.onPlayTrack(track);

            // isPlaying should equal !playerState.isPaused. If isPlaying == playerState.isPaused,
            // the player has switched states so we should notify our children.
            // We will then set isPlaying to its correct value after updateCallbacksForPlayerStateFinishes (isPlaying = !isPaused)
            if (isPlaying == playerState.isPaused) callback.onPlayPause(!isPlaying);
        }
    }

    private static void updateCallbacksForTrackRemoved(ArrayList<String> tracks) {
        if (playerCallbacks == null) return;

        for (PlayerCallback callback : playerCallbacks) {
            if (tracks != null)
                callback.onTracksRemoved(tracks);
        }
    }

    public static void setOnSeekListener(OnSeekListener seekListener) {
        onSeekListener = seekListener;
        forceSeekUpdate = true;
    }

    public static void setForceSeekUpdate(boolean force){
        forceSeekUpdate = force;
    }

    public static int indexOf(String uri) {
        return queueArray().indexOf(uri);
    }

    public static String currentTrack() {
        return q.peek();
    }

    public interface PlayerCallback {
        void onPlayTrack(HybridTrack track);

        void onPlayPause(boolean isPlaying);

        void onTracksRemoved(ArrayList<String> trackUri);
    }

    public interface OnSeekListener {
        void onDurationSet(long duration);

        void onSeek(long playbackPosMs);
    }


    /**
     * Debug Logging
     */

    private static void logSuccess(String subject) {
        if (subject == null || subject.isEmpty()) return;

        Log.d("SpotifyPlayerSuccess", subject);
    }

    private static void logError(String error) {
        if (error == null || error.isEmpty()) return;

        Log.e("SpotifyPlayerError", error);
    }


    private static class SongTimer {
        private static Handler songTimerHandler;
        private static Runnable songTimer;

        public boolean isRunning;
        public boolean waitingForResume;

        private final int TIME_TOLERANCE = 1000;

        SongTimer() {
            songTimerHandler = new Handler();
        }

        private void start(long duration) {
            if (songTimer != null) songTimerHandler.removeCallbacks(songTimer);
            songTimerHandler.postDelayed(songTimer = new Runnable() {
                @Override
                public void run() {
                    //playNext();
                }
            }, duration - TIME_TOLERANCE);

            isRunning = true;
            waitingForResume = false;
        }

        private void pause() {
            if (waitingForResume) return;
            waitingForResume = true;

            if (songTimer != null)
                songTimerHandler.removeCallbacks(songTimer);

            isRunning = false;
        }

        private void resume() {
            if (isRunning) return;
            waitingForResume = false;

            songTimerHandler.postDelayed(songTimer, playbackPosition - TIME_TOLERANCE);

            isRunning = true;
        }

        void stateChanged(boolean isPlaying) {
            if (q.isEmpty()) return;

            if (isPlaying)
                resume();
            else
                pause();
        }
    }

    public static void skipTo(int index){
        pause();
        ArrayList<String> removed = new ArrayList<>();
        StringBuilder uriList = new StringBuilder();
        //"[ "text" , "text2" ,
        for(int i = 0; i < index; i++){
            String toRemove = q.toArray().get(i);
            uriList.append(toRemove);
            uriList.append(",");
            removed.add(toRemove);
        }
//        list = list.substring(0, list.length()-1);

        q.popAll(removed.size()); //Because q will probably be used before server can update q.

        DataProvider.removeSongs(QuickTools.partyID, uriList.toString(), new HowdyIO.PartybAUX.Model.Callback() {
            @Override
            public void onResult(String result) {
                updateCallbacksForTrackRemoved(removed);
                playIfNotPlaying();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private static final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback = new Subscription.EventCallback<PlayerState>() {
        @Override
        public void onEvent(PlayerState playerState) {

/*            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.mediaservice_shuffle, getTheme());
            if (!playerState.playbackOptions.isShuffling) {
                mToggleShuffleButton.setImageDrawable(drawable);
                DrawableCompat.setTint(mToggleShuffleButton.getDrawable(), Color.WHITE);
            } else {
                mToggleShuffleButton.setImageDrawable(drawable);
                DrawableCompat.setTint(mToggleShuffleButton.getDrawable(), getResources().getColor(R.color.cat_medium_green));
            }

            if (playerState.playbackOptions.repeatMode == Repeat.ALL) {
                mToggleRepeatButton.setImageResource(R.drawable.mediaservice_repeat_all);
                DrawableCompat.setTint(mToggleRepeatButton.getDrawable(), getResources().getColor(R.color.cat_medium_green));
            } else if (playerState.playbackOptions.repeatMode == Repeat.ONE) {
                mToggleRepeatButton.setImageResource(R.drawable.mediaservice_repeat_one);
                DrawableCompat.setTint(mToggleRepeatButton.getDrawable(), getResources().getColor(R.color.cat_medium_green));
            } else {
                mToggleRepeatButton.setImageResource(R.drawable.mediaservice_repeat_off);
                DrawableCompat.setTint(mToggleRepeatButton.getDrawable(), Color.WHITE);
            }

            mPlayerStateButton.setText(String.format(Locale.US, "%s\n%s", playerState.track.name, playerState.track.artist.name));
            mPlayerStateButton.setTag(playerState);

            // Update progressbar
            if (playerState.playbackSpeed > 0) {
                mTrackProgressBar.unpause();
            } else {
                mTrackProgressBar.pause();
            }

            // Invalidate play / pause
            if (playerState.isPaused) {
                mPlayPauseButton.setImageResource(R.drawable.btn_play);
            } else {
                mPlayPauseButton.setImageResource(R.drawable.btn_pause);
            }

            // Invalidate playback speed
            mPlaybackSpeedButton.setVisibility(View.VISIBLE);
            if (playerState.playbackSpeed == 0.5f) {
                mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_50);
            } else if (playerState.playbackSpeed == 0.8f) {
                mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_80);
            } else if (playerState.playbackSpeed == 1f) {
                mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_100);
            } else if (playerState.playbackSpeed == 1.2f) {
                mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_120);
            } else if (playerState.playbackSpeed == 1.5f) {
                mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_150);
            } else if (playerState.playbackSpeed == 2f) {
                mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_200);
            } else if (playerState.playbackSpeed == 3f) {
                mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_300);
            }
            if (playerState.track.isPodcast && playerState.track.isEpisode) {
                mPlaybackSpeedButton.setEnabled(true);
                mPlaybackSpeedButton.clearColorFilter();
            } else {
                mPlaybackSpeedButton.setEnabled(false);
                mPlaybackSpeedButton.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            }

            // Get image from track
            mSpotifyAppRemote.getImagesApi()
                    .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                    .setResultCallback(bitmap -> {
                        mCoverArtImageView.setImageBitmap(bitmap);
                        mImageLabel.setText(String.format(Locale.ENGLISH, "%d x %d", bitmap.getWidth(), bitmap.getHeight()));
                    });

            // Invalidate seekbar length and position
            mSeekBar.setMax((int) playerState.track.duration);*/

            boolean validTrack = true;
            if(playerState.track != null && playerState.track.name == null)
                validTrack = false;

            if(validTrack)
                finishUpdateForPlayerState(playerState, new HybridTrack(playerState.track));
            else{
                SpotifyServiceUtil.getInstance()
                        .getTrack(playerState.track.uri, new Callback<Track>() {
                            @Override
                            public void success(Track track, Response response) {
                                finishUpdateForPlayerState(playerState, new HybridTrack(track));
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                finishUpdateForPlayerState(playerState, null);
                            }
                        });
            }

        }
    };

    private static void finishUpdateForPlayerState(PlayerState playerState, HybridTrack track){

        if (track != null) {
                /*if (!(trackPlayingInPlayer.equalsIgnoreCase(playerState.track.uri)))// && !isInQueue(trackPlayingInPlayer))
                    pause();*/

            if (onSeekListener != null) {
                if (track.duration > 0 && forceSeekUpdate || !(trackPlayingInPlayer.equals(track.uri)) || trackPlayingInPlayer.isEmpty()) {
                    onSeekListener.onDurationSet(track.duration);

                    if(playerState.track.name != null)
                        onSeekListener.onSeek(playerState.playbackPosition);

                    forceSeekUpdate = false;
                }
            }
        }

        playbackPosition = playerState.playbackPosition;


        if (isPlaying == playerState.isPaused) //Explained in "updateCallbacksForPlayerState()"
            songTimer.stateChanged(!isPlaying);

        updateCallbacksForPlayerState(playerState, track);


        if(track != null)
            trackPlayingInPlayer = playerState.track.uri;

        isPlaying = !playerState.isPaused;
    }
}
