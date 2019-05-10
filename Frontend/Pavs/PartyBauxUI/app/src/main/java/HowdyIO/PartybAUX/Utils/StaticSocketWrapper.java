package HowdyIO.PartybAUX.Utils;

import HowdyIO.PartybAUX.Model.PartyBox;
import HowdyIO.PartybAUX.Model.PartyBoxCallback;
import HowdyIO.PartybAUX.Tools.QuickTools;

/**
 * Created by Chris on 4/27/2019.
 */

public class StaticSocketWrapper {

    private static SocketProvider socketProvider;
    private static PartyBoxCallback partyBoxCallback;

    public static void initSocketProvider(String endpoint){
        //if(socketProvider != null) return;

        socketProvider = new SocketProvider(endpoint, new PartyBoxCallback() {
            @Override
            public void onResult(PartyBox partyBox) {
                if(partyBoxCallback != null) partyBoxCallback.onResult(partyBox);
            }
        });
    }

    public static void setCallback(PartyBoxCallback partyBoxCallback){
        StaticSocketWrapper.partyBoxCallback = partyBoxCallback;
    }

    public static void update(){
        if(socketProvider != null)
            socketProvider.sendEchoViaStomp("/app/update/" + QuickTools.partyID);
    }
}
