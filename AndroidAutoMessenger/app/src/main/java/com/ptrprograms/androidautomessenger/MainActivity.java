package com.ptrprograms.androidautomessenger;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import java.util.Calendar;


public class MainActivity extends Activity {

    private static final String MESSAGE_READ_ACTION = "com.ptrprograms.androidautomessenger.ACTION_MESSAGE_READ";
    private static final String MESSAGE_REPLY_ACTION = "com.ptrprograms.androidautomessenger.ACTION_MESSAGE_REPLY";
    public static final String MESSAGE_CONVERSATION_ID_KEY = "conversaton_key";
    public static final String VOICE_REPLY_KEY = "voice_reply_key";
    private static final String UNREAD_CONVERSATION_BUILDER_NAME = "Android Auto Messenger Demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main );

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder( getApplicationContext() )
                        .setSmallIcon( R.drawable.ic_launcher )
                        .setLargeIcon( BitmapFactory.decodeResource( getResources(), R.drawable.ic_launcher ) )
                        .setContentText( "content text" )
                        .setWhen( Calendar.getInstance().get( Calendar.SECOND ) )
                        .setContentTitle( "content title" );

        notificationBuilder.extend( new NotificationCompat.CarExtender()
                .setUnreadConversation( getUnreadConversation() ) )
                .setColor(getResources().getColor( android.R.color.holo_blue_bright ) );

        NotificationManagerCompat.from( this ).notify( 1, notificationBuilder.build() );
    }

    private Intent getMessageReadIntent() {
        return new Intent()
            .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            .setAction(MESSAGE_READ_ACTION)
            .putExtra(MESSAGE_CONVERSATION_ID_KEY, 1);
    }

    private PendingIntent getMessageReadPendingIntent() {
        return PendingIntent.getBroadcast( getApplicationContext(),
                1,
                getMessageReadIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT );
    }

    private Intent getMessageReplyIntent() {
        return new Intent()
                .addFlags( Intent.FLAG_INCLUDE_STOPPED_PACKAGES )
                .setAction( MESSAGE_REPLY_ACTION )
                .putExtra( MESSAGE_CONVERSATION_ID_KEY, 1 );
    }

    private PendingIntent getMessageReplyPendingIntent() {
        return PendingIntent.getBroadcast( getApplicationContext(),
                1,
                getMessageReplyIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT );
    }

    private RemoteInput getVoiceReplyRemoteInput() {
        return new RemoteInput.Builder( VOICE_REPLY_KEY )
                .setLabel( "Reply" )
                .build();
    }

    private NotificationCompat.CarExtender.UnreadConversation getUnreadConversation() {
        NotificationCompat.CarExtender.UnreadConversation.Builder unreadConversationBuilder =
                new NotificationCompat.CarExtender.UnreadConversation.Builder( UNREAD_CONVERSATION_BUILDER_NAME );

        unreadConversationBuilder.setReadPendingIntent( getMessageReadPendingIntent() );
        unreadConversationBuilder.setReplyAction( getMessageReplyPendingIntent(), getVoiceReplyRemoteInput() );
        unreadConversationBuilder.addMessage( "Okay I'm home now. Give me a buzz when you get in. I'll be here pretty much all night. Bye.");
        unreadConversationBuilder.addMessage( "Hey Steven. Quick question, give me a call when you get a chance." );
        unreadConversationBuilder.addMessage( "Hey man. It's me again. I was just taking a whizz. Thought you might have called. Okay, later." );
        unreadConversationBuilder.addMessage( "Sorry, I had call waiting, didn't get to it, thought it might have been you. All right, bye." );
        unreadConversationBuilder.addMessage( "We're having ourselves quite a little game of phone tag here. You're it!" );
        unreadConversationBuilder.addMessage( "I was just blow drying my hair, thought I heard the phone ring. Ah... has that ever happened to you? Anyway... call me, we'll talk about it." );
        unreadConversationBuilder.addMessage( "you're a tough man to reach." );
        unreadConversationBuilder.addMessage( "I guess you're too busy to call your friends." )
                .setLatestTimestamp(Calendar.getInstance().get( Calendar.SECOND ) );

        return unreadConversationBuilder.build();
    }
}
