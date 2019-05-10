package HowdyIO.PartybAUX.Model;

/**
 * Created by Chris on 4/16/2019.
 */

public interface PlayerCallbackAbstract {

    void onPlayTrack(HybridTrack track);

    void onPlayPause(boolean isPlaying);


    abstract class PlayerCallback implements PlayerCallbackAbstract{
        @Override
        public void onPlayTrack(HybridTrack track){};

        @Override
        public void onPlayPause(boolean isPlaying){};


    }
}
