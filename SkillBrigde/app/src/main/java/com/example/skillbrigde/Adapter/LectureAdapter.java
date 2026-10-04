package com.example.skillbrigde.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillbrigde.Model.LectureModel;
import com.example.skillbrigde.R;

import java.util.List;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.ViewHolder> {

    private final List<LectureModel> list;
    private final OnLectureClick listener;

    public interface OnLectureClick {
        void onClick(String videoUrl);
    }

    public LectureAdapter(List<LectureModel> list, OnLectureClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lecture, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LectureModel model = list.get(position);
        holder.title.setText(model.title);
        holder.duration.setText(model.duration);

        holder.itemView.setOnClickListener(v ->
                listener.onClick(model.videoUrl)
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.lectureTitle);
            duration = itemView.findViewById(R.id.lectureDuration);
        }
    }
}
