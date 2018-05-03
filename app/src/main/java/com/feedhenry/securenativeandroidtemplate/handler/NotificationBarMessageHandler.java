package com.feedhenry.securenativeandroidtemplate.handler;

import java.util.Map;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.MainActivity;
import org.aerogear.mobile.push.MessageHandler;
import org.aerogear.mobile.push.UnifiedPushMessage;

/**
 * Created by tjackman on 02/05/18.
 */

public class NotificationBarMessageHandler implements MessageHandler {

    private static final String CHANNEL_ID = "AEROGEAR_PUSH_EXAMPLE";
    private static final String CHANNEL_NAME = "AeroGear Android Push";
    private static final String CHANNEL_DESCRIPTION = "AeroGear Android Push example";

    public static final int NOTIFICATION_ID = 1;

    private static final NotificationBarMessageHandler instance =
            new NotificationBarMessageHandler();

    public static NotificationBarMessageHandler getInstance() {
        return instance;
    }

    @Override
    public void onMessage(Context context, Map<String, String> message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context);
        }

        displayMessageOnNotificationBar(context, message.get(UnifiedPushMessage.MESSAGE));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(Context context) {

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(CHANNEL_DESCRIPTION);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[] {100, 200, 300, 400, 500, 400, 300, 200, 400});

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

    }

    private void displayMessageOnNotificationBar(Context context, String message) {
        Intent intent = new Intent(context, MainActivity.class).putExtra(UnifiedPushMessage.MESSAGE,
                message);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(message).setContentIntent(contentIntent);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
