package com.ptrprograms.androidtvmediaplayer.Bonus;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

/**
 * Created by PaulTR on 7/10/14.
 */
public class AnimateDrawable extends ProxyDrawable {

    private Animation mAnimation;
    private Transformation mTransformation = new Transformation();

    public AnimateDrawable(Drawable target) {
        super(target);
    }

    public AnimateDrawable(Drawable target, Animation animation) {
        super(target);
        mAnimation = animation;
    }

    public Animation getAnimation() {
        return mAnimation;
    }

    public void setAnimation(Animation anim) {
        mAnimation = anim;
    }

    public boolean hasStarted() {
        return mAnimation != null && mAnimation.hasStarted();
    }

    public boolean hasEnded() {
        return mAnimation == null || mAnimation.hasEnded();
    }

    @Override
    public void draw(Canvas canvas) {
        Drawable dr = getProxy();
        if (dr != null) {
            int sc = canvas.save();
            Animation anim = mAnimation;
            if (anim != null) {
                anim.getTransformation(
                        AnimationUtils.currentAnimationTimeMillis(),
                        mTransformation);
                canvas.concat(mTransformation.getMatrix());
            }
            dr.draw(canvas);
            canvas.restoreToCount(sc);
        }
    }
}