package HowdyIO.PartybAUX.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import PartyBauxUI.R;

/**
 * Created by Chris on 1/24/2019.
 */

public class HomeFragment extends Fragment{

    private View root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);


        return root;
    }
}
