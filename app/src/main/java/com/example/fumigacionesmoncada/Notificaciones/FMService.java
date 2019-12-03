package com.example.fumigacionesmoncada.Notificaciones;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;

public class FMService extends FirebaseMessagingServices{

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i("Teste", remoteMessage.getMessageId());
    }


}
