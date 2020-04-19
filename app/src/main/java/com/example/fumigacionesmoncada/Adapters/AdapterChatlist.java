package com.example.fumigacionesmoncada.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.fumigacionesmoncada.ChatActivity;
import com.example.fumigacionesmoncada.Models.ModelUsers;
import com.example.fumigacionesmoncada.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder> {

    Context context;
    List<ModelUsers> usersList; //obtener info user
    private HashMap<String, String> lastMessageMap;

    public AdapterChatlist(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contactos_mensajes, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String hisUid = usersList.get(position).getUid();
        String userImage = usersList.get(position).getImage();
        String userName = usersList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);

        //set data
        holder.nameTv.setText(userName);

        if (lastMessage ==null || lastMessage.equals("default")){
            holder.lastMessageTv.setVisibility(View.GONE);
        }
        else {
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);
        }
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_admin_green)
                    .into(holder.profileIv);
        }
        catch (Exception e){
            Picasso.get().load(R.drawable.ic_admin_green)
                    .into(holder.profileIv);
        }

        //others status users
        if (usersList.get(position).getOnlineStatus().equals("online")){
            holder.onlineStatusIv.setImageResource(R.drawable.circle_online);
        }
        else {
            holder.onlineStatusIv.setImageResource(R.drawable.circle_offline);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);

            }
        });
    }

    public void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIv, onlineStatusIv;
        TextView nameTv, lastMessageTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profileIv = itemView.findViewById(R.id.imageView);
            onlineStatusIv = itemView.findViewById(R.id.onlineStatusIv);
            nameTv = itemView.findViewById(R.id.textView);
            lastMessageTv = itemView.findViewById(R.id.textView2);

        }
    }
}