package com.example.galib.lifeblood;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorListAdapter extends RecyclerView.Adapter<DonorListAdapter.ViewHolder>{

    List<UserLocation> usersList;
    public Context context;
    public String image_uri;

    public DonorListAdapter(List<UserLocation> usersList){
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.nameText.setText(usersList.get(position).getUser().getName());
        holder.phoneNoText.setText(usersList.get(position).getUser().getPhoneNo());
        image_uri = usersList.get(position).getUser().getImage();
        holder.setImage(image_uri);

        holder.phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+usersList.get(position).getUser().getPhoneNo().toString()));

                context.startActivity(callIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView nameText;
        public TextView phoneNoText;
        public CircleImageView profileImage;
        public ImageView phoneCall;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            nameText = (TextView) mView.findViewById(R.id.history_blood_group);
            phoneNoText = (TextView) mView.findViewById(R.id.phoneNo_text);
            phoneCall = (ImageView) mView.findViewById(R.id.phone_call);


        }

        public void setImage(String image){
            profileImage = (CircleImageView) mView.findViewById(R.id.profile_image);
            Glide.with(context).load(image_uri).into(profileImage);
        }
    }
}
