package HowdyIO.PartybAUX.Fragments;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import HowdyIO.PartybAUX.Model.Callback;
import HowdyIO.PartybAUX.Model.User;
import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Utils.DataProvider;
import HowdyIO.PartybAUX.Utils.StaticSocketWrapper;
import HowdyIO.PartybAUX.Views.TwoWayAnimatedDrawable;
import PartyBauxUI.R;

/**
 * Created by Chris on 3/30/2019.
 */

public class UserProfileFragment extends PartybAUXFragment{
    View root;
    private Context context;
    TextView usernameTextView;
    User user;
    String username;
    private CardView kickButton;
    private CardView promoteButton;
    private FrameLayout selectorPromoteButton;
    TwoWayAnimatedDrawable twoWayAnimatedDrawable;
    private ImageView addToCheckmark;

    public static final String BUNDLE_USERNAME = "username";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        usernameTextView = root.findViewById(R.id.textview_username);
        kickButton = root.findViewById(R.id.button_kick);
        promoteButton = root.findViewById(R.id.button_promote_user);
        selectorPromoteButton = root.findViewById(R.id.selector_promote);
        addToCheckmark = root.findViewById(R.id.imageanim_add_to_checkmark);


        context = getContext();
        pullUser();
        setUpDrawableAnim();
//        setUpButtons();
//        setUpOnClicks();
        onCreateView(root);
        return root;
    }




    private void setUpDrawableAnim() {
        twoWayAnimatedDrawable = new TwoWayAnimatedDrawable(addToCheckmark,
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.add_to_checkmark),
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.checkmark_to_add));

    }

    private void setUpOnClicks() {
        kickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataProvider.kickUser( QuickTools.getSavedUser(context).getUsername(), username, new Callback() {
                    @Override
                    public void onResult(String result) {
                        if(result.equals("0")) {
                            StaticSocketWrapper.update();
                            finishFragment();
                        }
                    }
                });
            }
        });
        promoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataProvider.updateUserType( QuickTools.hostName,username, 1, new Callback() {
                    @Override
                    public void onResult(String result) {
                        selectorPromoteButton.animate().alpha(0f);
                        promoteButton.setClickable(false);
                        promoteButton.setFocusable(false);

                        animateViewIn(kickButton, false);
                        StaticSocketWrapper.update();
                        twoWayAnimatedDrawable.start();
                    }
                });
            }
        });
    }

    private void setUpButtons() {
        //set up kick button
        //user needs to be in party and not a guest
        int userType = QuickTools.sharedPrefs(context).getInt(QuickTools.SHARED_PREFS_USERTYPE, QuickTools.ROLE_HOST);
        int viewedUserType= user.getUserType();

        // 1) in party
        // 2) User is Mod or Host
        // 3) current profile is not mod or host
        if(viewedUserType == QuickTools.ROLE_GUEST  && QuickTools.partyID >= 0 &&
            (userType == (QuickTools.ROLE_MOD) || userType == (QuickTools.ROLE_HOST) )
            && !QuickTools.getSavedUser(context).getUsername().equals(username)) {
            animateViewIn(kickButton, true);
        }


        //Promote
        if(QuickTools.partyID>=0 && userType == QuickTools.ROLE_HOST
                && viewedUserType < 2
                && !QuickTools.getSavedUser(context).getUsername().equals(username)){
            promoteButton.setVisibility(View.VISIBLE);
            if(viewedUserType == 1){
                twoWayAnimatedDrawable.setState(TwoWayAnimatedDrawable.DrawableState.STATE2);
                promoteButton.setClickable(false);
                promoteButton.setFocusable(false);
                selectorPromoteButton.setVisibility(View.GONE);
            }
        }
        //TODO
        //If more buttons are to be added add setups here
    }

    private void animateViewIn(View view, boolean show){
        view.setAlpha(show? 0f: 1f);
        view.setVisibility(View.VISIBLE);

        view.animate().alpha(show? 1f: 0f).setDuration(150).start();


        if(!show) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.GONE);
                }
            }, 150);
        }
    }

    private void pullUser(){
        Bundle bundle = this.getArguments();
        if(bundle == null)
            return;

        username = bundle.getString(BUNDLE_USERNAME, "");

        usernameTextView.setText(username);

        DataProvider.getUserByUsername(username, new Callback() {
            @Override
            public void onResult(String result) {
                if(result.isEmpty())return;

                user = QuickTools.parseUser(result);

                setContent();
            }
        });
    }

    private void setContent(){
        setUpButtons();
        setUpOnClicks();
    }

    public OnUserKickedListener onUserKickedListener;

    public void setOnUserKickedListener(OnUserKickedListener onUserKickedListener){
        this.onUserKickedListener = onUserKickedListener;
    }

    public interface OnUserKickedListener{
        void onUserKicked(String username);
    }
}
