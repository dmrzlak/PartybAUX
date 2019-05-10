package HowdyIO.PartybAUX.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import HowdyIO.PartybAUX.Activities.MainActivity;
import PartyBauxUI.R;

/**
 * Created by Chris on 2/1/2019.
 */

public class PartybAUXFragment extends Fragment {

    MainActivity mainActivity;

    private View rootView;
    public Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateView(View rootView){
        this.rootView = rootView;
        mainActivity = (MainActivity) getActivity();

        toolbar = mainActivity.getToolbar();
    }

    private void startFragment(PartybAUXFragment fragment, String tag){
        mainActivity.startFragment(fragment, tag);
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
}
