package com.example.fumigacionesmoncada.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fumigacionesmoncada.Models.ModelChat;
import com.example.fumigacionesmoncada.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

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

        //long click delete
        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Borrar");
                builder.setMessage("¿Estás seguro que quieres eliminar este mensaje?");

                builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMessage(position);
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.create().show();
                return false;
            }
        });

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



    private void deleteMessage(int position) {
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Chats");

        Query query = databaseReference.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    if (ds.child("sender").getValue().equals(myUID)){
                        //delete
                        //ds.getRef().removeValue();

                        //was detete
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "⊘ Este mensaje ha sido eliminado...");
                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "Mensaje eliminado", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Solo puedes eliminar tus mensajes", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        ConstraintLayout messageLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeentTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }
}