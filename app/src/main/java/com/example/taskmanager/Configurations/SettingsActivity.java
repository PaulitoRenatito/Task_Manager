package com.example.taskmanager.Configurations;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import com.example.taskmanager.R;

public class SettingsActivity extends AppCompatActivity implements
PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TITLE_TAG = "settingsActivityTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        }
        else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                            setTitle(R.string.title_activity_settings);
                        }
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }

    public static class HeaderFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey);
        }
    }

    public static class ThemeFragment extends PreferenceFragmentCompat {

        public static final String
                KEY_PREF_THEME_SWITCH = "theme_switch";

        private static String stringDarkThemeOn;
        private static String stringDarkThemeOff;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.theme_preferences, rootKey);

            stringDarkThemeOn = getResources().getString(R.string.theme_dark_on);
            stringDarkThemeOff = getResources().getString(R.string.theme_dark_off);

            SwitchPreferenceCompat switchTheme =
                    (SwitchPreferenceCompat) findPreference(KEY_PREF_THEME_SWITCH);

            PreferenceManager.setDefaultValues(getContext(), R.xml.theme_preferences, false);
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getContext());

            final Boolean[] themeDark = {sharedPreferences
                    .getBoolean(KEY_PREF_THEME_SWITCH, true)};

            if (switchTheme != null) {

                setSummary(switchTheme, themeDark);

                setChangeListener(switchTheme, themeDark);

            }
        }

        private void setSummary(SwitchPreferenceCompat switchTheme, Boolean[] themeDark) {
            if (themeDark[0]) {
                switchTheme.setSummary(stringDarkThemeOn);
            }
            else {
                switchTheme.setSummary(stringDarkThemeOff);
            }
        }

        private void setChangeListener(SwitchPreferenceCompat switchTheme, Boolean[] themeDark) {
            switchTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    themeDark[0] = (Boolean) newValue;
                    if (themeDark[0]){
                        preference.setSummary(stringDarkThemeOn);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    else {
                        preference.setSummary(stringDarkThemeOff);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    return true;
                }
            });
        }
    }

    public static class NotificationsFragment extends PreferenceFragmentCompat {

        public static final String
                KEY_PREF_NOTIFICATIONS_ENABLED_SWITCH = "notifications_enabled_switch";
        public static final String
                KEY_PREF_CONFIGURE_NORMAL_BTN = "configure_normal_channel";
        public static final String
                KEY_PREF_CONFIGURE_HIGH_BTN = "configure_high_channel";
        public static final String
                KEY_PREF_CONFIGURE_VERY_HIGH_BTN = "configure_very_high_channel";

        private NotificationManager mNotificationManager;

        private static String stringNotificationEnabled;
        private static String stringNotificationDisabled;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.notifications_preferences, rootKey);

            mNotificationManager =
                    (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);

            stringNotificationEnabled = getResources().getString(R.string.notification_enabled);
            stringNotificationDisabled = getResources().getString(R.string.notification_disabled);

            PreferenceManager.setDefaultValues(getContext(),
                    R.xml.notifications_preferences, false);
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getContext());

            SwitchPreferenceCompat switchNotificationsEnabled =
                    findPreference(KEY_PREF_NOTIFICATIONS_ENABLED_SWITCH);

            setConfigureButtons();

            final Boolean[] enableNotifications = {sharedPreferences
                    .getBoolean(KEY_PREF_NOTIFICATIONS_ENABLED_SWITCH, true)};

            if (switchNotificationsEnabled != null) {

                setSummary(switchNotificationsEnabled, enableNotifications);

                setNotificationsEnabledChangeListener(switchNotificationsEnabled, enableNotifications);

            }
        }

        private void setConfigureButtons() {
            Preference configure_normal_btn =
                    findPreference(KEY_PREF_CONFIGURE_NORMAL_BTN);
            Preference configure_high_btn =
                    findPreference(KEY_PREF_CONFIGURE_HIGH_BTN);
            Preference configure_very_high_btn =
                    findPreference(KEY_PREF_CONFIGURE_VERY_HIGH_BTN);

            configure_normal_btn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID,
                            mNotificationManager.
                                    getNotificationChannel(PrefConfig.NORMAL_PRIORITY_CHANNEL_ID)
                                    .getId());
                    startActivity(intent);
                    return true;
                }
            });

            configure_high_btn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID,
                            mNotificationManager.
                                    getNotificationChannel(PrefConfig.HIGH_PRIORITY_CHANNEL_ID)
                                    .getId());
                    startActivity(intent);
                    return true;
                }
            });

            configure_very_high_btn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID,
                            mNotificationManager.
                                    getNotificationChannel(PrefConfig.VERY_HIGH_PRIORITY_CHANNEL_ID)
                                    .getId());
                    startActivity(intent);
                    return true;
                }
            });
        }

        private void setSummary(SwitchPreferenceCompat switchNotificationsEnabled,
                                Boolean[] enableNotifications) {
            if (enableNotifications[0]) {
                switchNotificationsEnabled.setSummary(stringNotificationEnabled);
            }
            else {
                switchNotificationsEnabled.setSummary(stringNotificationDisabled);
            }
        }

        private void setNotificationsEnabledChangeListener(
                SwitchPreferenceCompat switchNotificationsEnabled,
                Boolean[] enableNotifications) {
            switchNotificationsEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    enableNotifications[0] = (Boolean) newValue;
                    if (enableNotifications[0]){
                        preference.setSummary(stringNotificationEnabled);
                        PrefConfig.aux = enableNotifications[0];
                        PrefConfig.createNotificationChannelNHVh(getContext());
                    }
                    else {
                        preference.setSummary(stringNotificationDisabled);
                        PrefConfig.aux = enableNotifications[0];
                        mNotificationManager.deleteNotificationChannel(PrefConfig.NORMAL_PRIORITY_CHANNEL_ID);
                        mNotificationManager.deleteNotificationChannel(PrefConfig.HIGH_PRIORITY_CHANNEL_ID);
                        mNotificationManager.deleteNotificationChannel(PrefConfig.VERY_HIGH_PRIORITY_CHANNEL_ID);
                        mNotificationManager.cancelAll();
                    }
                    return true;
                }
            });
        }
    }

    public static class TimeFragment extends PreferenceFragmentCompat {

        public static final String
                KEY_PREF_TIME_MODE_SWITCH = "time_mode_switch";

        private static String string12hTimeMode;
        private static String string24hTimeMode;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.time_preferences, rootKey);

            string12hTimeMode = getResources().getString(R.string.time_mode_12h);
            string24hTimeMode = getResources().getString(R.string.time_mode_24h);

            SwitchPreferenceCompat switchTimeMode =
                    (SwitchPreferenceCompat) findPreference(KEY_PREF_TIME_MODE_SWITCH);

            PreferenceManager.setDefaultValues(getContext(), R.xml.time_preferences, false);
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getContext());

            final Boolean[] timeMode12h = {sharedPreferences
                    .getBoolean(KEY_PREF_TIME_MODE_SWITCH, true)};

            if (switchTimeMode != null) {

                setSummary(switchTimeMode, timeMode12h);

                setChangeListener(switchTimeMode, timeMode12h);

            }

        }

        private void setSummary(SwitchPreferenceCompat switchTimeMode,
                                Boolean[] timeMode12h) {
            if (timeMode12h[0]) {
                switchTimeMode.setSummary(string12hTimeMode);
            }
            else {
                switchTimeMode.setSummary(string24hTimeMode);
            }
        }

        private void setChangeListener(SwitchPreferenceCompat switchTimeMode,
                                       Boolean[] timeMode12h) {
            switchTimeMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    timeMode12h[0] = (Boolean) newValue;
                    if (timeMode12h[0]){
                        preference.setSummary(string12hTimeMode);
                    }
                    else {
                        preference.setSummary(string24hTimeMode);
                    }
                    PrefConfig.changeTimeMode(getContext());
                    return true;
                }
            });
        }
    }

}