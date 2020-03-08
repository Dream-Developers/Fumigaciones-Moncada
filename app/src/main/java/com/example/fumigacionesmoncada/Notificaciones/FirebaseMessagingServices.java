package com.example.fumigacionesmoncada.Notificaciones;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.fumigacionesmoncada.MenuActivity;
import com.example.fumigacionesmoncada.NavegacionAdministradorActivity;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.ui.citas.Detalle_Cita;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static android.graphics.Color.rgb;

public class FirebaseMessagingServices extends FirebaseMessagingService {
    int i = 0;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        guardarTokenFirebase(s);
        Log.i("token firebase", "" + s);


    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getData().get("body") != null) {
            sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
        } else {
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }
        Log.i("data firebase", String.valueOf(remoteMessage.getNotification().getBody()));
    }


    //Notificacion
    private void sendNotification(String body, String title) {

        Intent intent = null;
        String NOTIFICATION_CHANNEL_ID = getString(R.string.default_notification_channel_id);

        if (body != null) {
            if (obtener_rol_usuario() == 1) {
                intent = new Intent(this, NavegacionAdministradorActivity.class);

            } else {
                intent = new Intent(this, MenuActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notificationBuilder
                    .setSmallIcon(R.drawable.logo)
                    .setColor(rgb(255, 160, 0))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setContentInfo("info");

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT);

                notificationChannel.setDescription("Descripcion");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableLights(true);

                notificationManager.createNotificationChannel(notificationChannel);
            }
            i = (int) (Math.random() * 1000 + 1);
            notificationManager.notify(i /* ID of notification */, notificationBuilder.build());
        }
    }

    private void guardarTokenFirebase(String token) {
        //Este token es para identificar el dispositivo a que se le mandara la notificacion
        //este token varia cuando se hace una nueva instalacion de la aplicacion

        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token_firebase", token);
        editor.apply();

    }

    public int obtener_rol_usuario() {
        SharedPreferences sharedPreferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        return Integer.parseInt(sharedPreferences.getString("rol", ""));
    }

}
