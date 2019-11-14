package com.example.fumigacionesmoncada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

public class ChatActivity extends AppCompatActivity {

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        User user = getIntent().getExtras().getParcelable("user");
        getSupportActionBar().setTitle(user.getUsername());


        RecyclerView rv = findViewById(R.id.recycler_chat);
        adapter = new GroupAdapter<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        adapter.add(new MensajeItem(true));
        adapter.add(new MensajeItem(false));
        adapter.add(new MensajeItem(true));
        adapter.add(new MensajeItem(true));
        adapter.add(new MensajeItem(false));
        adapter.add(new MensajeItem(true));
        adapter.add(new MensajeItem(false));
        adapter.add(new MensajeItem(true));


    }

    private class MensajeItem extends Item<ViewHolder>{

        private final boolean isIzquierda;

        //Constructor
        private MensajeItem(boolean isIzquierda) {
            this.isIzquierda = isIzquierda;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

        }

        @Override
        public int getLayout() {
            return isIzquierda ? R.layout.item_from_mensaje : R.layout.item_to_mensajes;
        }
    }
}
