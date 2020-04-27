package com.example.fumigacionesmoncada.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.fumigacionesmoncada.Models.ModelChat;
import com.example.fumigacionesmoncada.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterContactar extends RecyclerView.Adapter<AdapterContactar.MyHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    Context context;
    List<ModelChat> chatList;
    String imageUrl;

    FirebaseUser fUser;

    public AdapterContactar(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layouts
        if (viewType == MSG_TYPE_RIGHT){

            View view = LayoutInflater.from(context).inflate(R.layout.item_to_mensajes, parent, false);
            return new MyHolder(view);

        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_from_mensaje, parent, false);
            return new MyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();

        //comvertir timestamp to dd/mm/aa
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        //set Data
        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);

        try{
            Picasso.get().load(imageUrl).into(holder.profileIv);
        }catch (Exception e){

        }
        if (position == chatList.size() -1){
            if (chatList.get(position).isSeen()){
                holder.isSeenTv.setText("seen");
            }
            else {
                holder.isSeenTv.setText("✔✔");
            }
        }
        else {
            holder.isSeenTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    @Override
    public int getItemViewType(int position) {

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;

        }
        else {
            return MSG_TYPE_LEFT;
        }


    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView profileIv;
        TextView messageTv, timeTv, isSeenTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeentTv);
        }
    }
}