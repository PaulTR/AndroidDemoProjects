package com.ptrprograms.androidtvmediaplayer.Bonus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import com.ptrprograms.androidtvmediaplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SlothActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        RaiseView raiseView = new RaiseView(this);
        setContentView(raiseView);
        raiseView.setBackgroundResource( R.drawable.poledancingsloth );

    }

    private class RaiseView extends View {
        private int money_count = 10;
        private final List<Drawable> drawables = new ArrayList<Drawable>();
        private int[][] coords;
        private final Drawable money_sign;

        public RaiseView(Context context) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);

            money_sign = context.getResources().getDrawable( R.drawable.dollar );
            money_sign.setBounds(0, 0, money_sign.getIntrinsicWidth(), money_sign
                    .getIntrinsicHeight());
        }

        @Override
        protected void onSizeChanged(int width, int height, int oldw, int oldh) {
            super.onSizeChanged(width, height, oldw, oldh);
            Random random = new Random();
            Interpolator interpolator = new LinearInterpolator();

            money_count = Math.max(width, height) / 30;
            coords = new int[money_count][];
            drawables.clear();
            for (int i = 0; i < money_count; i++) {
                Animation animation = new TranslateAnimation(0, height / 10
                        - random.nextInt(height / 5), 0, height + 30);
                animation.setDuration(10 * height + random.nextInt(5 * height));
                animation.setRepeatCount(-1);
                animation.initialize(10, 10, 10, 10);
                animation.setInterpolator(interpolator);

                coords[i] = new int[] { random.nextInt(width - 30), -80 };

                drawables.add(new AnimateDrawable(money_sign, animation));
                animation.setStartOffset(random.nextInt(20 * height));
                animation.startNow();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (int i = 0; i < money_count; i++) {
                Drawable drawable = drawables.get(i);
                canvas.save();
                canvas.translate(coords[i][0], coords[i][1]);
                drawable.draw(canvas);
                canvas.restore();
            }
            invalidate();
        }

    }
}
