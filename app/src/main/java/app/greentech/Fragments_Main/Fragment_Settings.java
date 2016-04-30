package app.greentech.Fragments_Main;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import app.greentech.R;

/**
 * Created by Cyril on 3/2/16.
 */
public class Fragment_Settings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the settings from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}

