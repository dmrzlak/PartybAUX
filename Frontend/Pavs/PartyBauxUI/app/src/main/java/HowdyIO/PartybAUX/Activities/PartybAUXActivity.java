package HowdyIO.PartybAUX.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;

import HowdyIO.PartybAUX.Fragments.PartybAUXFragment;
import HowdyIO.PartybAUX.Views.CustomProgressBar;
import PartyBauxUI.R;

/**
 * Created by Chris on 2/26/2019.
 */

public class PartybAUXActivity extends AppCompatActivity {
    public CustomProgressBar progressBar;
    SparseBooleanArray containerHasFragment = new SparseBooleanArray();
    public static final int ANIM_SLIDE_IN_OVER = 0;
    public static final int ANIM_SLIDE_IN_OUT = 1;

    public void startFragment(PartybAUXFragment fragment, String tag, String title, int containerResID, int animType) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().addToBackStack(tag);
        if(containerHasFragment.get(containerResID)){ //Replace if we've already added a fragment to this container
            setAnimationFor(fragmentTransaction, animType);
            fragmentTransaction.replace(containerResID, fragment, tag);
            fragmentTransaction.commitAllowingStateLoss();
        }
        else {
            containerHasFragment.put(containerResID, true);
            fragmentTransaction
                    .add(containerResID, fragment, tag)
                    .commitAllowingStateLoss();
        }
    }

    private void setAnimationFor(FragmentTransaction fragmentTransaction, int animType) {
        if(animType == ANIM_SLIDE_IN_OVER){
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, 0, 0, R.anim.slide_out_bottom);
        }else if(animType == ANIM_SLIDE_IN_OUT){
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom);
        }
    }

    public void popupFragment(PartybAUXFragment fragment, String tag, String title, int containerResID) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(containerHasFragment.get(containerResID)){ //Replace if we've already added a fragment to this container
            fragmentManager.beginTransaction()
                    .replace(containerResID, fragment, tag)
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_out_bottom, R.anim.slide_in_top)
                    .commitAllowingStateLoss();
        }
        else {
            containerHasFragment.put(containerResID, true);
            fragmentManager.beginTransaction()
                    .add(containerResID, fragment, tag)
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    .commitAllowingStateLoss();
        }
    }

    private void replaceFragment (Fragment fragment){
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.module_container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public PartybAUXFragment getCurrentFragment(FragmentManager fm){
        int backStackCount = fm.getBackStackEntryCount();
        if(backStackCount <= 0) return null;
        FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(backStackCount - 1);
        String tag = backStackEntry.getName();

        return (PartybAUXFragment) fm.findFragmentByTag(tag);
    }

    @Override
    public void onBackPressed() {
        onBackPressed(false);
    }

    public void onBackPressed(boolean force){
        if(force) {
            finish();
            return;
        }

        FragmentManager fm = getSupportFragmentManager();

        PartybAUXFragment currentFragment = getCurrentFragment(fm);
        if(currentFragment == null) {
            super.onBackPressed();
            return;
        }

        if(currentFragment.onBackPressed())return;
        if(fm.getBackStackEntryCount() > 1){
            fm.popBackStackImmediate();
            return;
        }

        if(this instanceof PartyActivity){
            ((PartyActivity)this).attemptLeaveParty();
        }else
            finish();

    }
}
