package com.boots.firebasetaskapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskActionListener {
        void onToggleDone(Task task, boolean isDone);

        void onDelete(Task task);
    }

    private final List<Task> tasks = new ArrayList<>();
    private final OnTaskActionListener listener;

    public TaskAdapter(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.tvTitle.setText(task.getTitle());

        // Pehle listener hatao, phir checkbox set karo (warna ghalat trigger hota hai)
        holder.cbDone.setOnCheckedChangeListener(null);
        holder.cbDone.setChecked(task.isDone());
        updateStrikeThrough(holder.tvTitle, task.isDone());

        holder.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateStrikeThrough(holder.tvTitle, isChecked);
            listener.onToggleDone(task, isChecked);
        });

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(task));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private void updateStrikeThrough(TextView textView, boolean done) {
        if (done) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setAlpha(0.5f);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setAlpha(1f);
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbDone;
        TextView tvTitle;
        ImageButton btnDelete;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbDone = itemView.findViewById(R.id.cbDone);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}