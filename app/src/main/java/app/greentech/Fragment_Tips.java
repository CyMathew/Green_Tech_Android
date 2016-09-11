package app.greentech;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import app.greentech.R;

/**
 * Fragment for Tips used to share About information with user and also direct them the Tips Activity for more detailed tips
 * @author Cyril Mathew
 */
public class Fragment_Tips extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tips, container, false);
    }
}