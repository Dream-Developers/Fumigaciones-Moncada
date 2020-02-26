package com.example.fumigacionesmoncada.Notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.example.fumigacionesmoncada.MenuActivity;
import com.example.fumigacionesmoncada.NavegacionAdministradorActivity;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.ui.citas.Detalle_Cita;

import org.json.JSONException;
import org.json.JSONObject;

import static android.graphics.Color.rgb;

public class FirebaseBackgroundService extends WakefulBroadcastReceiver {

    private static final String TAG = "FirebaseService";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "I'm in!!!");

        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                Log.e("FirebaseDataReceiver", "Key: " + key + " Value: " + value);
                if(key.equalsIgnoreCase("gcm.notification.body") && value != null) {
                    Bundle bundle = new Bundle();
                    Intent backgroundIntent = new Intent(context, Detalle_Cita.class);
                    bundle.putString("id_cita", value + "");
                    backgroundIntent.putExtras(bundle);
                    context.startService(backgroundIntent);
                    sendNotification(intent.getStringExtra("gcm.notification.body"), intent.getStringExtra("gcm.notification.title"),context);

                }
            }
        }
    }
    private void sendNotification(String body, String title,Context context) {

        Intent intent = null;
        String NOTIFICATION_CHANNEL_ID = context.getString(R.string.default_notification_channel_id);

        try {
            JSONObject data = new JSONObject(body);

            if (obtener_rol_usuario(context) == 1) {
                if (data.getString("id_cita") != null) {
                    intent = new Intent(context, Detalle_Cita.class);
                    intent.putExtra("id_citas",data.getString("id_cita"));

                }else {
                    intent = new Intent(context, NavegacionAdministradorActivity.class);


                }
            } else {

                intent = new Intent(context, MenuActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
            notificationBuilder
                    .setSmallIcon(R.drawable.logo)
                    .setColor(rgb(255, 160, 0))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                    .setContentText(data.getString(title))
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.iconolm)
                    .setContentIntent(pendingIntent)
                    .setContentInfo("info");

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        "Notification",
                        NotificationManager.IMPORTANCE_DEFAULT);

                notificationChannel.setDescription("Descripcion");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableLights(true);

                notificationManager.createNotificationChannel(notificationChannel);
            }
          int  i = (int) (Math.random() * 1000 + 1);
            notificationManager.notify(i /* ID of notification */, notificationBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public int obtener_rol_usuario(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        return Integer.parseInt(sharedPreferences.getString("rol", ""));
    }
}
