package HowdyIO.PartybAUX.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import HowdyIO.PartybAUX.Activities.MainActivity;
import PartyBauxUI.R;

/**
 * Created by Chris on 2/12/2019.
 */

public class FriendsFragment extends PartybAUXFragment {

    private View root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_friends, container, false);
        //recyclerView = root.findViewById(R.id.recycler_view);

        onCreateView(root);
        return root;
    }
}
