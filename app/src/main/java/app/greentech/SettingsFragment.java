package app.greentech;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Cyril on 3/2/16.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the settings from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}

