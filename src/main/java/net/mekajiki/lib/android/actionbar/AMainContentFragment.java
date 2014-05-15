package net.mekajiki.lib.android.actionbar;

import android.app.Activity;
import android.support.v4.app.Fragment;

public abstract class AMainContentFragment extends Fragment {
    private IActionBarActivity mainActivity;

    protected abstract CharSequence actionBarTitle();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mainActivity = (IActionBarActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewsListFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        
        mainActivity.setActionBarTitle(actionBarTitle());
    }

    public IActionBarActivity getMainActivity() {
        return mainActivity;
    }
}
