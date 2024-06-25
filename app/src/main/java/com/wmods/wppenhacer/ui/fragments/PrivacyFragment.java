package com.wmods.wppenhacer.ui.fragments;

import static com.wmods.wppenhacer.preference.ContactPickerPreference.REQUEST_CONTACT_PICKER;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.wmods.wppenhacer.R;
import com.wmods.wppenhacer.preference.ContactPickerPreference;
import com.wmods.wppenhacer.ui.fragments.base.BasePreferenceFragment;

public class PrivacyFragment extends BasePreferenceFragment {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        setPreferencesFromResource(R.xml.fragment_privacy, rootKey);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CONTACT_PICKER && resultCode == Activity.RESULT_OK) {
            ContactPickerPreference contactPickerPref = findPreference(data.getStringExtra("key"));
            if (contactPickerPref != null) {
                contactPickerPref.handleActivityResult(requestCode, resultCode, data);
            }
        }
    }

}
