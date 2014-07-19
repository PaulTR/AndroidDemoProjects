package com.ptrprograms.androidtvmediaplayer.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.ptrprograms.androidtvmediaplayer.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * A collection of utility methods, all static.
 */
public class Utils {

    /*
     * Making sure public utility methods remain static
     */
    private Utils() {
    }

    /**
     * Formats time in milliseconds to hh:mm:ss string format.
     *
     * @param millis
     * @return
     */
    public static String formatMillis(int millis) {
        String result = "";
        int hr = millis / 3600000;
        millis %= 3600000;
        int min = millis / 60000;
        millis %= 60000;
        int sec = millis / 1000;
        if (hr > 0) {
            result += hr + ":";
        }
        if (min >= 0) {
            if (min > 9) {
                result += min + ":";
            } else {
                result += "0" + min + ":";
            }
        }
        if (sec > 9) {
            result += sec;
        } else {
            result += "0" + sec;
        }
        return result;
    }

    public static int dpToPx(int dp, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static String loadJSONFromResource( Context context, int resource ) {
        if( resource <= 0 )
            return null;

        String json = null;
        InputStream is = context.getResources().openRawResource( resource );
        try {
            if( is != null ) {
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                json = new String(buffer, "UTF-8");
            }
        } catch( IOException e ) {

        } finally {
            try {
                if( is != null )
                    is.close();
            } catch( IOException e ) {}
        }

        return json;
    }
}
