package com.ptrprograms.androidautomessenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by paulruiz on 2/18/15.
 */
public class AutoMessageReadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int conversationId = intent.getIntExtra(MainActivity.MESSAGE_CONVERSATION_ID_KEY, -1);
        Log.d( "Message", "id: " + conversationId );
        NotificationManagerCompat.from( context ).cancel( conversationId );
    }
}
