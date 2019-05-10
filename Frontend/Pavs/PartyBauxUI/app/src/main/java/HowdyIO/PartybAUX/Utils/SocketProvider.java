package HowdyIO.PartybAUX.Utils;


import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import HowdyIO.PartybAUX.Model.PartyBox;
import HowdyIO.PartybAUX.Model.PartyBoxCallback;
import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import ua.naiksoftware.stomp.*;
import ua.naiksoftware.stomp.dto.StompHeader;


public class SocketProvider{


    StompClient stomp;

    private Disposable mRestPingDisposable;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private Gson mGson = new GsonBuilder().create();
    private List<String> mDataSet = new ArrayList<>();

    private final String TAG = "STOMPBOI";
    private String destinationPath; // : /topic/members/{pid}

    private CompositeDisposable compositeDisposable;
    private PartyBoxCallback dataCallback;

    public static final String STOMP_UPDATE_PARTY = "/app/update_party/";
    // We need to hardcode
    public SocketProvider(String destinationPath, PartyBoxCallback callback){
        dataCallback = callback;
        this.destinationPath = destinationPath;
        stomp = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://cs309-bs-6.misc.iastate.edu:8080/example-endpoint/websocket");

        resetSubscriptions();

        connectStomp();
//        stomp.topic("/topic/greetings").subscribe(topicMessage -> {
//            Log.d(TAG, topicMessage.getPayload());
//        });
    }

    public void disconnectStomp() {
        stomp.disconnect();
    }


    public void connectStomp() {

        List<StompHeader> headers = new ArrayList<>();
//        headers.add(new StompHeader(LOGIN, "guest"));
//        headers.add(new StompHeader(PASSCODE, "guest"));

        stomp.withClientHeartbeat(1000).withServerHeartbeat(1000);

        resetSubscriptions();

        Disposable dispLifecycle = stomp.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            break;
                        case ERROR:
                            Log.e("Error", "Stomp connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                             resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);

        // Receive greetings
        Disposable dispTopic = stomp.topic(destinationPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d("Hallo", "Received " + topicMessage.getPayload());
                    dataCallback.onResult(mGson.fromJson(topicMessage.getPayload(), PartyBox.class));
                }, throwable -> {
                    Log.e("Hallo", "Error on subscribe topic", throwable);
                });

        compositeDisposable.add(dispTopic);

        stomp.connect(headers);
    }

    public void sendEchoViaStomp(String endPoint){
        sendEchoViaStomp(endPoint, "");
    }

    public void sendEchoViaStomp(String endPoint, String send) {
//        if (!stomp.isConnected()) return;
        if(send == null) send = "";

        compositeDisposable.add(stomp.send(endPoint, send)
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d("Good", "STOMP echo send successfully");
                }, throwable -> {
                    Log.e("Error", "Error send STOMP echo", throwable);
                }));
    }

//    public void sendEchoViaRest(View v) {
//        mRestPingDisposable = RestClient.getInstance().getExampleRepository()
//                .sendRestEcho("PartyBox REST " + mTimeFormat.format(new Date()))
//                .compose(applySchedulers())
//                .subscribe(() -> {
//                    Log.d("Good", "REST echo send successfully");
//                }, throwable -> {
//                    Log.e("Error", "Error send REST echo", throwable);
//                    toast(throwable.getMessage());
//                });
//    }

    private void addItem(PartyBox echoModel) {
        mDataSet.add(echoModel.getPartyMembers() + " - " + mTimeFormat.format(new Date()));
    }


    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }






}

