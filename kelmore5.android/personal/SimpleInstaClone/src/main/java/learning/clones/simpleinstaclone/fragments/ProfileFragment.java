package learning.clones.simpleinstaclone.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import learning.clones.bottom_navigator.R;

/**
 * The fragment for the profile view
 *
 * @author Kyle Elmore
 * @version 1.0
 * @since 2016-03-26
 */
public class ProfileFragment extends Fragment {

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
