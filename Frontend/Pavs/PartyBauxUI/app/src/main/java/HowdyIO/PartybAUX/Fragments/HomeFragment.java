package HowdyIO.PartybAUX.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import HowdyIO.PartybAUX.Activities.MainActivity;
import HowdyIO.PartybAUX.Activities.PartyActivity;
import HowdyIO.PartybAUX.Model.Callback;
import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Utils.DataProvider;
import HowdyIO.PartybAUX.Views.CustomTextView;
import HowdyIO.PartybAUX.Views.PartyPopup;
import PartyBauxUI.R;

/**
 * Created by Chris on 1/24/2019.
 */

public class HomeFragment extends PartybAUXFragment {

    private Context context;
    private View root;
    private LinearLayout contentView;
    private ViewGroup logOutButton;
    private CardView joinPartyButton;
    private CardView hostPartyButton;
    private CustomTextView signedInText;

    private PartyPopup partyPopup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        root = inflater.inflate(R.layout.fragment_home, container, false);
        contentView = root.findViewById(R.id.container);
        contentView.setAlpha(0f);
        logOutButton = root.findViewById(R.id.button_log_out);
        joinPartyButton = root.findViewById(R.id.button_join_party);
        hostPartyButton = root.findViewById(R.id.button_host_party);
        signedInText = root.findViewById(R.id.textview_signed_in);
        signedInText.setText("Signed in as: " + QuickTools.userName);

        setUpPopup();
        setUpOnClicks();

        onCreateView(root);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                contentView.animate().alpha(1f).setDuration(250).start();
            }
        });
        return root;
    }

    private void setUpPopup() {
        partyPopup = new PartyPopup(context, root);
        partyPopup.setTitle("Join Party")
                .setIsInput(true)
                .setPositiveText("Join")
                .setNegativeText("Back")
                .setSubHeader("Whose party are you joining?")
                .setInputHint("Username")
                .setPositiveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hostname = partyPopup.getInput().split(" ")[0];
                partyPopup.dismiss();
/*                DataProvider.getUserByUsername("hostname", new Callback() {
                    @Override
                    public void onResult(String result) {
                        if()
                    }
                });*/
                if (hostname.isEmpty())  //TODO Stop if user doesn't exist
                    return;

                setClickable(false);
                QuickTools.hostName = hostname;

                startPartyActivity(QuickTools.ROLE_GUEST);
            }
        });
    }

    public static boolean activityLaunched;

    private void setUpOnClicks() {
        hostPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityLaunched) return;
                setClickable(false);

                startPartyActivity(QuickTools.ROLE_HOST);
            }
        });
        joinPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityLaunched) return;
                popupDialogue();

            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null)
                    ((MainActivity)getActivity()).logUserOut();
            }
        });
    }

    private void popupDialogue() {
        partyPopup.show();
    }

    private void setClickable(boolean clickable) {
        hostPartyButton.setClickable(clickable);
        joinPartyButton.setClickable(clickable);
    }

    private void startPartyActivity(int userType) {
        SharedPreferences sharedPreferences = QuickTools.sharedPrefs(getContext());
        sharedPreferences.edit().putInt(QuickTools.SHARED_PREFS_USERTYPE, userType).apply();

        Intent partyActivityIntent = new Intent(partybAUXActivity, PartyActivity.class);
        startActivityForResult(partyActivityIntent, 0);

        partybAUXActivity.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_big);
    }

    @Override
    public void onResume() {
        super.onResume();
        setClickable(true);
        partyPopup.setKeyboardShowing(false);
    }
}
