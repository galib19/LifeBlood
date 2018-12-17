package com.example.galib.lifeblood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    List<BloodRequest> bloodRequests;
    public Context context;

    public HistoryAdapter(List<BloodRequest> bloodRequests){
        this.bloodRequests= bloodRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.hospitalName.setText(bloodRequests.get(position).getHospital_name());
        holder.bloodGroup.setText(bloodRequests.get(position).getRequest_blood_group());
        holder.requestDetails.setText(bloodRequests.get(position).getRequest_details());
        holder.timestamp.setText(bloodRequests.get(position).getTimestamp().toString());

    }

    @Override
    public int getItemCount() {
        return bloodRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView hospitalName;
        public TextView bloodGroup;
        public TextView requestDetails;
        public TextView timestamp;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            hospitalName = (TextView) mView.findViewById(R.id.history_hospital_name_text);
            bloodGroup = (TextView) mView.findViewById(R.id.history_blood_group_text);
            requestDetails = (TextView) mView.findViewById(R.id.history_details_text);
            timestamp = (TextView) mView.findViewById(R.id.history_timestamp);

        }
    }
}
