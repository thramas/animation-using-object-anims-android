package com.android.samarth.animationpoc;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.plattysoft.leonids.ParticleSystem;

import static android.view.View.SCALE_X;
import static android.view.View.SCALE_Y;
import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = (ImageView) findViewById(R.id.img_view);
        parentLayout = (RelativeLayout) findViewById(R.id.activity_main);
        final ImageView imageView1 = (ImageView) findViewById(R.id.img_view1);
        final ImageView imageView2 = (ImageView) findViewById(R.id.img_view2);
        final ImageView imageView3 = (ImageView) findViewById(R.id.img_view3);
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfParticles = 5;
                playAnimation(numberOfParticles,v.getContext());
//                setAnimation(150,imageView,0);
//                setAnimation(-150,imageView1,200);
//                setAnimation(50,imageView2,400);
//                setAnimation(100,imageView3,800);
            }
        });

    }

    private void playAnimation(int numberOfParticles, Context context) {
        int positionArray[] = {200,-200,150,-150,100,-100};
        for (int i = 0; i < numberOfParticles; i++) {
            ImageView imageView = new ImageView(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            imageView.setLayoutParams(params);
            imageView.setBackground(getResources().getDrawable(R.drawable.user_image_def));
            parentLayout.addView(imageView);
            setAnimation(positionArray[i%positionArray.length],imageView,i*200);
        }
    }

    private void setAnimation(float baseFactor, final ImageView imageView,long delay) {
        imageView.clearAnimation();
        final AnimatorSet animatorSet = new AnimatorSet();
        //first translation
        ObjectAnimator anim1 = getObjectAnimator(imageView, 0, 0, 2 * baseFactor,baseFactor < 0 ? baseFactor : -baseFactor, 0.1f, 0.8f, 1, 1, true, 500);

        //wave translation
        ValueAnimator anim2 = getValueAnimator(imageView, 2 * baseFactor,baseFactor < 0 ? baseFactor : -baseFactor);
        anim2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                float ix = imageView.getX() - imageView.getLeft();
                float iy = imageView.getY() - imageView.getTop();
                final ObjectAnimator anim3 = getObjectAnimator(imageView, ix, iy, ix, iy - 60, 0.8f, 0.8f, 1, 0, false, 400);
                anim3.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animatorSet.setStartDelay(delay);
        animatorSet.playSequentially(anim1, anim2);
        animatorSet.start();
    }

    private ValueAnimator getValueAnimator(final ImageView imageView, float originalX, float originalY) {
        final boolean[] isScaled = {false};
        ValueAnimator animator = new ValueAnimator();
        //these lines do nothing. But removing them crashes the app.
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, 100);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 100);
        animator.setValues(pvhX, pvhY);
        final float[] incrementalValue = {0,-originalY};
        animator.setDuration(2000);
        final float finalOriginalX = originalX;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                if(animation.getCurrentPlayTime() > 1000 && !isScaled[0]) {
                    animation.pause();
                    isScaled[0] = true;
                    float ix = imageView.getX() - imageView.getLeft();
                    float iy = imageView.getY() - imageView.getTop();
                    final ObjectAnimator anim3 = getObjectAnimator(imageView, ix, iy, ix, iy, 0.8f, 1f, 1, 1, false, 200);
                    anim3.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation2) {
                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.other_image));
                            float ix = imageView.getX() - imageView.getLeft();
                            float iy = imageView.getY() - imageView.getTop();
                            final ObjectAnimator anim4 = getObjectAnimator(imageView, ix, iy, ix, iy, 1f, 0.8f, 1, 1, false, 200);
                            anim4.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation2) {
                                    animation.resume();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            anim4.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    anim3.start();
                } else {
                    if (animation == null || animation.getAnimatedValue() == null) return;
                    float value = ((Float) (animation.getAnimatedValue()))
                            .floatValue();
                    float amplitude = 40f;
                    float tx = finalOriginalX - (float) (amplitude * Math.sin((incrementalValue[0]) * Math.PI));
                    float ty = -1 * (value + incrementalValue[1]);
                    imageView.setTranslationX(tx);
                    imageView.setTranslationY(ty);
                    Log.d("anim", "x:y" + String.valueOf(tx) + "," + String.valueOf(ty));
                    incrementalValue[0] += 0.02f;
                    incrementalValue[1] += 1.5f;
                }
            }
        });
        animator.setTarget(imageView);
        return animator;
    }

    @NonNull
    private ObjectAnimator getObjectAnimator(final ImageView imageView, float originalX, float originalY, float newX, float newY, float originalScale, float newScale, float orignialAlpha, float newAplha, boolean isAccelerated, int duration) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, originalX, newX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, originalY, newY);
        PropertyValuesHolder zoomX = PropertyValuesHolder.ofFloat(View.SCALE_X, originalScale, newScale);
        PropertyValuesHolder zoomY = PropertyValuesHolder.ofFloat(View.SCALE_Y, originalScale, newScale);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, orignialAlpha, newAplha);
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(imageView, pvhX, pvhY, zoomX, zoomY, alpha);
        if (isAccelerated) {
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        animation.setDuration(duration);
        return animation;
    }
}
