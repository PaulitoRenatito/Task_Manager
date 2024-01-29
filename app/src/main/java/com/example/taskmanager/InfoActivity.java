package com.example.taskmanager;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class InfoActivity extends AppCompatActivity {

    private static final String VIDEO_ADD_TASK_1 = "add_task_screen_1";
    private static final String VIDEO_ADD_TASK_2 = "add_task_screen_2";
    private static final String VIDEO_ADD_TASK_3 = "add_task_screen_3";

    private static final String VIDEO_PRIORITY_1 = "priority_screen_1";
    private static final String VIDEO_PRIORITY_2 = "priority_screen_2";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setToolBar();

        setAddTaskGuide();

        setPriorityGuide();


    }

    @Override
    protected void onPause() {
        super.onPause();
        setAddTaskPlayer(false);
        setPriorityPlayer(false);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_info);
        toolbar.setTitle(getResources().getString(R.string.header_info));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setAddTaskGuide() {
        Button btnAddTaskGuide = findViewById(R.id.info_btn_addTaskGuide);
        View includeAddTask = findViewById(R.id.info_include_addTask);

        btnAddTaskGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (includeAddTask.getVisibility() == View.GONE) {
                    includeAddTask.setVisibility(View.VISIBLE);
                    btnAddTaskGuide
                            .setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.drawable.ic_arrow_up, 0);
                    setAddTaskPlayer(true);
                }
                else {
                    includeAddTask.setVisibility(View.GONE);
                    setAddTaskPlayer(false);
                    btnAddTaskGuide
                            .setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.drawable.ic_arrow_down, 0);
                }
            }
        });
    }

    private void setPriorityGuide() {
        Button btnPriorityGuide = findViewById(R.id.info_btn_priorityGuide);
        View includePriority = findViewById(R.id.info_include_priority);

        btnPriorityGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (includePriority.getVisibility() == View.GONE) {
                    includePriority.setVisibility(View.VISIBLE);
                    btnPriorityGuide
                            .setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.drawable.ic_arrow_up, 0);
                    setPriorityPlayer(true);
                }
                else {
                    includePriority.setVisibility(View.GONE);
                    btnPriorityGuide
                            .setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.drawable.ic_arrow_down, 0);
                    setPriorityPlayer(false);
                }
            }
        });
    }

    private void setAddTaskPlayer(boolean enable) {
        VideoView videoView_1 = findViewById(R.id.frag_addTask_videoView_1);
        VideoView videoView_2 = findViewById(R.id.frag_addTask_videoView_2);
        VideoView videoView_3 = findViewById(R.id.frag_addTask_videoView_3);

        if (enable) {
            initializePlayer(videoView_1, VIDEO_ADD_TASK_1);
            initializePlayer(videoView_2, VIDEO_ADD_TASK_2);
            initializePlayer(videoView_3, VIDEO_ADD_TASK_3);
        }
        else {
            releasePlayer(videoView_1);
            releasePlayer(videoView_2);
            releasePlayer(videoView_3);
        }
    }

    private void setPriorityPlayer(boolean enable) {
        VideoView videoView_1 = findViewById(R.id.frag_priority_videoView_1);
        VideoView videoView_2 = findViewById(R.id.frag_priority_videoView_2);

        if (enable) {
            initializePlayer(videoView_1, VIDEO_PRIORITY_1);
            initializePlayer(videoView_2, VIDEO_PRIORITY_2);
        }
        else {
            releasePlayer(videoView_1);
            releasePlayer(videoView_2);
        }
    }

    private void initializePlayer(@NonNull VideoView mVideoView, String mVideoName) {
        Uri videoUri = getMedia(mVideoName);
        mVideoView.setVideoURI(videoUri);
        mVideoView.start();
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVideoView.start();
            }
        });
    }

    private void releasePlayer(@NonNull VideoView mVideoView) {
        mVideoView.stopPlayback();
        mVideoView.suspend();
    }

    private Uri getMedia(String mediaName) {
        return Uri.parse("android.resource://" + getPackageName() + "/raw/" + mediaName);
    }
}
