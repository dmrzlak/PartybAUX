package HowdyIO.PartybAUX.Model;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import HowdyIO.PartybAUX.Fragments.FriendsFragment;
import HowdyIO.PartybAUX.Fragments.HomeFragment;
import HowdyIO.PartybAUX.Fragments.PartyViewFragment;
import HowdyIO.PartybAUX.Fragments.PartybAUXFragment;
import HowdyIO.PartybAUX.Fragments.SearchFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private int COUNT = 3;
    private FriendsFragment friendsFragment;
    private HomeFragment homeFragment;
    private PartyViewFragment partyViewFragment;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        //searchFragment = new SearchFragment();
    }

    @Override
    public PartybAUXFragment getItem(int position) {
        PartybAUXFragment fragment = null;
        switch (position) {
            case 0:
                fragment = friendsFragment != null? friendsFragment: (friendsFragment = new FriendsFragment());
                break;
            case 1:
                fragment = homeFragment != null? homeFragment: (homeFragment = new HomeFragment());
                break;
            case 2:
                fragment = partyViewFragment != null? partyViewFragment: (partyViewFragment = new PartyViewFragment());
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "Tab " + (position + 1);
    }

/*    public void connected() {
        searchFragment.onConnected();
    }*/

    private int lastPage = 1;
    public void updateCurrentFrag(int position) {
        getItem(position).isCurrentFrag = true;
        getItem(position).justPaused = false;
        getItem(position).onResume();

        if(lastPage != position) {
            getItem(lastPage).isCurrentFrag = false;
            getItem(position).justPaused = true;
            getItem(lastPage).onPause();
        }
        lastPage = position;
    }
}
