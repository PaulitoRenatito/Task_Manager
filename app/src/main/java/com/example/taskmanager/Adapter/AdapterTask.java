package com.example.taskmanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanager.Model.ModelTask;
import com.example.taskmanager.R;
import java.util.ArrayList;

public class AdapterTask extends RecyclerView.Adapter<AdapterTask.ViewHolder> {

    private ArrayList<ModelTask> mTaskData;
    private Context mContext;

    public AdapterTask(ArrayList<ModelTask> mTaskData, Context mContext) {
        this.mTaskData = mTaskData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdapterTask.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_list_tod, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTask.ViewHolder holder, int position) {

        ModelTask currentTask = mTaskData.get(position);

        holder.bindTo(currentTask);
    }

    @Override
    public int getItemCount() {
        return mTaskData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_priority_indicator;
        private TextView tv_mTime;
        private TextView tv_mTitle;
        private TextView tv_mDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_priority_indicator = itemView.findViewById(R.id.ll_priority_indicator);
            tv_mTime = itemView.findViewById(R.id.tod_tv_time);
            tv_mTitle = itemView.findViewById(R.id.tod_tv_title);
            tv_mDescription = itemView.findViewById(R.id.tod_tv_description);
        }

        void bindTo(ModelTask currentTask) {
            tv_mTime.setText(currentTask.getTime());

            String normal = itemView.getContext().getResources().getString(R.string.string_normal);
            String High = itemView.getContext().getResources().getString(R.string.string_high);
            String Very_High = itemView.getContext().getResources().getString(R.string.string_very_high);

            if (currentTask.getPriority().equals(normal)) {
                ll_priority_indicator.setBackgroundResource(R.color.default_priority);
            }
            else if (currentTask.getPriority().equals(High)) {
                ll_priority_indicator.setBackgroundResource(R.color.high_priority);
            }
            else if (currentTask.getPriority().equals(Very_High)) {
                ll_priority_indicator.setBackgroundResource(R.color.very_high_priority);
            }

            tv_mTitle.setText(currentTask.getTitle());
            tv_mDescription.setText(currentTask.getDescription());
        }
    }
}
