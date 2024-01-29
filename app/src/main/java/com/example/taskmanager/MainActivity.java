package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.taskmanager.Configurations.PrefConfig;
import com.example.taskmanager.Configurations.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    public static final String MONDAY_KEY = "monday_key";
    public static final String TUESDAY_KEY = "tuesday_key";
    public static final String WEDNESDAY_KEY = "wednesday_key";
    public static final String THURSDAY_KEY = "thursday_key";
    public static final String FRIDAY_KEY = "friday_key";
    public static final String SATURDAY_KEY = "saturday_key";
    public static final String SUNDAY_KEY = "sunday_key";

    private static final String DAY_KEY = "day_key";
    private static final String TITLE_KEY = "title_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolBar();

        PrefConfig.createNotificationChannelNHVh(this);

    }

    @Override
    protected void onStart() {
        savedThemeSettings();
        super.onStart();
    }

    /**
     *  Set ToolBar and ToolBar configs.
     */
    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    /**
     *  Check for the app saved settings.
     */
    private void savedThemeSettings() {

        if (PrefConfig.isThemeDarkOn(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Open TasksOfDay activity on button click.
     * Also put some extras to the new Intent
     * @param view
     */
    public void callTaskOfDay(View view) {

        Intent intent = new Intent(MainActivity.this, TasksOfDay.class);

        int id = view.getId();

        switch (id){
            case R.id.main_btn_Monday:
                intent.putExtra(DAY_KEY, MONDAY_KEY);
                intent.putExtra(TITLE_KEY, getResources().getString(R.string.monday));
                PrefConfig.checkTaskList(this, MONDAY_KEY);
                break;
            case R.id.main_btn_Tuesday:
                intent.putExtra(DAY_KEY, TUESDAY_KEY);
                intent.putExtra(TITLE_KEY, getResources().getString(R.string.tuesday));
                PrefConfig.checkTaskList(this, TUESDAY_KEY);
                break;
            case R.id.main_btn_Wednesday:
                intent.putExtra(DAY_KEY, WEDNESDAY_KEY);
                intent.putExtra(TITLE_KEY, getResources().getString(R.string.wednesday));
                PrefConfig.checkTaskList(this, WEDNESDAY_KEY);
                break;
            case R.id.main_btn_Thursday:
                intent.putExtra(DAY_KEY, THURSDAY_KEY);
                intent.putExtra(TITLE_KEY, getResources().getString(R.string.thursday));
                PrefConfig.checkTaskList(this, THURSDAY_KEY);
                break;
            case R.id.main_btn_Friday:
                intent.putExtra(DAY_KEY, FRIDAY_KEY);
                intent.putExtra(TITLE_KEY, getResources().getString(R.string.friday));
                PrefConfig.checkTaskList(this, FRIDAY_KEY);
                break;
            case R.id.main_btn_Saturday:
                intent.putExtra(DAY_KEY, SATURDAY_KEY);
                intent.putExtra(TITLE_KEY, getResources().getString(R.string.saturday));
                PrefConfig.checkTaskList(this, SATURDAY_KEY);
                break;
            case R.id.main_btn_Sunday:
                intent.putExtra(DAY_KEY, SUNDAY_KEY);
                intent.putExtra(TITLE_KEY, getResources().getString(R.string.sunday));
                PrefConfig.checkTaskList(this, SUNDAY_KEY);
                break;
            default:
                Toast.makeText(this, "No valid day", Toast.LENGTH_SHORT).show();
                break;
        }

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_main_settings) {
            Intent intentSettings = new Intent(this, SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}