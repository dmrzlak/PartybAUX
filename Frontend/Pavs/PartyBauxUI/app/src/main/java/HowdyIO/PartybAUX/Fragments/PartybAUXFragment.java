package HowdyIO.PartybAUX.Fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import HowdyIO.PartybAUX.Activities.MainActivity;
import HowdyIO.PartybAUX.Activities.PartyActivity;
import HowdyIO.PartybAUX.Activities.PartybAUXActivity;
import HowdyIO.PartybAUX.Model.FragmentForcedMethods;

/**
 * Created by Chris on 2/1/2019.
 */

public class PartybAUXFragment extends Fragment implements FragmentForcedMethods{

    PartybAUXActivity partybAUXActivity;

    private View rootView;
    public boolean isCurrentFrag;
    public boolean justPaused;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @CallSuper
    public void onCreateView(View rootView){
        this.rootView = rootView;
        partybAUXActivity = (PartybAUXActivity) getActivity();

        //toolbar = partyActivity.getToolbar();
    }

    public void startFragment(PartybAUXFragment fragment, String tag, String title){
        if(partybAUXActivity instanceof MainActivity)
            ((MainActivity)partybAUXActivity).startFragment(fragment, tag, title);
        else
            ((PartyActivity)partybAUXActivity).startFragment(fragment, tag, title);
    }

    public void startFragment(PartybAUXFragment fragment, String tag, String title, int animType){
        if(partybAUXActivity instanceof MainActivity)
            ((MainActivity)partybAUXActivity).startFragment(fragment, tag, title);
        else
            ((PartyActivity)partybAUXActivity).startFragment(fragment, tag, title, animType);
    }

    public void popupFragment(PartybAUXFragment fragment, String tag, String title){
        if(partybAUXActivity instanceof MainActivity)
            ((MainActivity)partybAUXActivity).popupFragment(fragment, tag, title);
        else
            ((PartyActivity)partybAUXActivity).popupFragment(fragment, tag, title);
    }

    public void onConnected(){

    }

    public void finish(){
        if(partybAUXActivity != null)
            partybAUXActivity.finish();
    }

    public void finishFragment(){
        partybAUXActivity.onBackPressed();
    }

    public boolean onBackPressed(){
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPause(){
        super.onPause();
        justPaused = false;
    }

}
