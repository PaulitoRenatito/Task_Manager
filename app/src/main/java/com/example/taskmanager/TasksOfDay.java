package com.example.taskmanager;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.Adapter.AdapterTask;
import com.example.taskmanager.Configurations.PrefConfig;
import com.example.taskmanager.Model.ModelTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;

public class TasksOfDay extends AppCompatActivity {


    private String key_day;

    // region Interface Variables
    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerView mRecyclerView;
    private ArrayList<ModelTask> mTaskData;
    private AdapterTask mAdapter;
    // endregion

    // region Dialog Variables Definitions
    private Dialog mDialogAddTask;
    private TimePicker mDialogTimePicker;
    private Spinner mSpinnerPriority;
    private EditText mDialogEditTextTitle;
    private EditText mDialogEditTextDescription;
    private Button dialogCancelTask;
    private Button dialogConfirmTask;
    // endregion

    // region Static Final Variables
    private static final String TITLE_KEY = "title_key";
    private static final String BUNDLE_KEY = "Task_data";
    private static final String DAY_KEY = "day_key";
    // endregion

    // region Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_of_day);

        setToolBar();

        //setDialog();

        key_day = getIntent().getStringExtra(DAY_KEY);

        mCoordinatorLayout = findViewById(R.id.tod_coordinator_layout);
        mRecyclerView = findViewById(R.id.tod_rv_task);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTaskData = PrefConfig.readListFromPref(this, key_day);

        if (mTaskData == null){
            mTaskData = new ArrayList<>();
        }

        checkSavedInstances(savedInstanceState);

        mAdapter = new AdapterTask(mTaskData, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        setItemTouchHelper();

        FloatingActionButton fab = findViewById(R.id.tod_fab);
        fab.setOnClickListener(this::FABlistener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_taskofday, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String key_day = getIntent().getStringExtra(DAY_KEY);

        int id = item.getItemId();

        if (id == R.id.action_clearAll) {

            if (mTaskData.size() > 0) {

                for (int i = 0; i < mTaskData.size(); i++) {
                    mTaskData.get(i).removeSchedule(this);
                }

                mTaskData.clear();

                PrefConfig.writeListInPref(this, mTaskData, key_day);

                Snackbar snackbar =
                        Snackbar.make(mCoordinatorLayout,
                                getResources().getString(R.string.all_removed),
                                Snackbar.LENGTH_LONG);
                snackbar.show();

                mAdapter.notifyDataSetChanged();
                return true;

            }
            else {
                Snackbar snackbar =
                        Snackbar.make(mCoordinatorLayout,
                                getResources().getString(R.string.no_task_to_remove),
                                Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the data changes before close/restart the app
     * (Usually for maintain data changes when the orientation changes).
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_KEY, mTaskData);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    // endregion

    // region Listeners
    /**
     * Set the Dialog to add new Task when FAB is clicked.
     */
    private void FABlistener(View view) {

        setDialog();

        setPriorityColors(mSpinnerPriority, mDialogTimePicker);

        dialogCancelTask.setOnClickListener(TasksOfDay.this::dialogCancelTaskListener);

        dialogConfirmTask.setOnClickListener(TasksOfDay.this::dialogConfirmTaskListener);

        //Show Dialog
        mDialogAddTask.show();
    }

    private  void dialogCancelTaskListener(View view) {
        mDialogAddTask.dismiss();
    }

    private void dialogConfirmTaskListener(View view) {

        // Get the time picked. eg: 13:20
        String time = getDialogTime(mDialogTimePicker);
        // Get the priority. eg: High
        String priority = mSpinnerPriority.getSelectedItem().toString();
        Log.d("TasksOfDay", priority);
        // Get the task title. eg: Workout
        String title = mDialogEditTextTitle.getText().toString();
        // Get the task description. eg: Leg day
        String description = mDialogEditTextDescription.getText().toString();

        if (title.equals("")) {
            mDialogEditTextTitle.setError(getResources().getString(R.string.error_need_title));
        }
        else if (PrefConfig.haveTaskWithSameHour(this, key_day, time)) {
            mDialogEditTextTitle.setError(getResources().getString(R.string.error_same_time));
        }
        else {
            //Dismiss Dialog
            mDialogAddTask.dismiss();
            // Add the task to the Array list
            mTaskData.add(new ModelTask(key_day, time, priority, title, description));
            mTaskData.get(mTaskData.size()-1).schedule(this);
            // Sort mTaskData by the time of the task
            // Sorted by crescent order
            sortTimeInCrescentOrder(mTaskData);
            // Write the saved the tasks in json file
            PrefConfig.writeListInPref(getApplicationContext(), mTaskData, key_day);
        }

        // Notify data Changed
        mAdapter.notifyDataSetChanged();
    }
    // endregion

    // region Helper Functions

    private void sortTimeInCrescentOrder(ArrayList<ModelTask> mTaskData) {
        mTaskData.sort(new Comparator<ModelTask>() {
            @Override
            public int compare(ModelTask modelTask, ModelTask t1) {
                if (PrefConfig.is12hConvention(TasksOfDay.this)) {
                    int index = modelTask.getTime().indexOf(" ");
                    float timeModelTask;
                    float timeT1;
                    if (modelTask.getTime().contains(" pm")){
                        timeModelTask = Float.parseFloat(modelTask.getTime()
                                .substring(0, index).replace(":", ".")) + 12.0f;
                        timeT1 = Float.parseFloat(t1.getTime()
                                .substring(0, index).replace(":", ".")) + 12.0f;
                    }
                    else {
                        timeModelTask = Float.parseFloat(modelTask.getTime()
                                .substring(0, index).replace(":", "."));
                        timeT1 = Float.parseFloat(t1.getTime()
                                .substring(0, index).replace(":", "."));
                    }
                    return new BigDecimal(String.valueOf(timeModelTask))
                            .compareTo(new BigDecimal(String.valueOf(timeT1)));
                }
                else {
                    return new BigDecimal(modelTask.getTime().replace(":", "."))
                            .compareTo(new BigDecimal(t1.getTime().replace(":", ".")));
                }
            }
        });
    }

    /**
     * Get and format the Time
     * @param mDialogTimePicker Dialog TimePicker.
     * @return the TimePicker time formatted correctly
     */
    private String getDialogTime(TimePicker mDialogTimePicker) {

        String minute;

        if (mDialogTimePicker.getMinute() < 10 || mDialogTimePicker.getMinute() == 0) {
            minute = "0" + mDialogTimePicker.getMinute();
        }
        else {
            minute = String.valueOf(mDialogTimePicker.getMinute());
        }

        if (PrefConfig.is12hConvention(this)) {
            mDialogTimePicker.setIs24HourView(false);
            if (mDialogTimePicker.getHour() > 12) {
                int hour = mDialogTimePicker.getHour() - 12;
                return hour + ":" + minute + " pm";
            }
            else {
                return mDialogTimePicker.getHour() + ":" + minute + " am";
            }

        }
        else {
            mDialogTimePicker.setIs24HourView(true);
            return mDialogTimePicker.getHour() + ":" + minute;
        }
    }

    /**
     * Change the TimePicker background color based on the priority
     * @param mSpinnerPriority Dialog change priority Spinner (Normal, Hight or Very Hight).
     * @param mDialogTimePicker Dialog TimePicker.
     */
    private void setPriorityColors(Spinner mSpinnerPriority, TimePicker mDialogTimePicker) {
        mSpinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerSelected = mSpinnerPriority.getSelectedItem().toString();
                String normal = getResources().getString(R.string.string_normal);
                String High = getResources().getString(R.string.string_high);
                String Very_High = getResources().getString(R.string.string_very_high);

                if (spinnerSelected.equals(normal)) {
                    mDialogTimePicker.setBackgroundResource(R.color.default_priority);
                }
                else if (spinnerSelected.equals(High)) {
                    mDialogTimePicker.setBackgroundResource(R.color.high_priority);
                }
                else if (spinnerSelected.equals(Very_High)) {
                    mDialogTimePicker.setBackgroundResource(R.color.very_high_priority);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Make the swipe to delete Logic
     */
    private void setItemTouchHelper() {
        int swipeDirs = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, swipeDirs) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                        String message =
                                mTaskData.get(viewHolder.getAdapterPosition()).getTitle()
                                        + " " + getResources().getString(R.string.removed);

                        Snackbar snackbar = Snackbar
                                .make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);

                        // Remove the swiped task schedule
                        mTaskData.get(viewHolder.getAdapterPosition()).removeSchedule(TasksOfDay.this);
                        // Remove the swiped task from the list
                        mTaskData.remove(viewHolder.getAdapterPosition());
                        // Notifies the adapter that a item has been removed
                        mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        // Save the new list(without the swiped task) in json file
                        PrefConfig.writeListInPref(getApplicationContext(), mTaskData, key_day);
                        // Show the title of the removed task
                        snackbar.show();
                    }
                });
        helper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * Check saved instances
     */
    private void checkSavedInstances(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mTaskData.clear();
            mTaskData = savedInstanceState.getParcelableArrayList(BUNDLE_KEY);
        }
    }

    /**
     *  Set ToolBar and ToolBar configs.
     */
    private void setToolBar() {

        String toolbarTitle = getIntent().getStringExtra(TITLE_KEY);

        Toolbar toolbar = findViewById(R.id.toolbar_tod);
        toolbar.setTitle(toolbarTitle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setDialog() {
        // region Dialog

        // region Dialog Configs
        mDialogAddTask = new Dialog(TasksOfDay.this);
        mDialogAddTask.setContentView(R.layout.dialog_create_task);
        mDialogAddTask.setCancelable(true);
        mDialogAddTask.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // endregion

        // region Dialog Variables IDs
        mDialogTimePicker = mDialogAddTask.findViewById(R.id.dialog_ct_tp_time);
        mSpinnerPriority = mDialogAddTask.findViewById(R.id.dialog_spin_priority);
        mDialogEditTextTitle = mDialogAddTask.findViewById(R.id.dialog_ct_et_title);
        mDialogEditTextDescription = mDialogAddTask.findViewById(R.id.dialog_ct_et_description);
        dialogCancelTask = mDialogAddTask.findViewById(R.id.dialog_ct_btn_cancel);
        dialogConfirmTask = mDialogAddTask.findViewById(R.id.dialog_ct_btn_confirm);
        // endregion

        // endregion
    }
    // endregion
}