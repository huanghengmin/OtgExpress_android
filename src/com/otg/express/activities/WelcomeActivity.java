package com.otg.express.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import com.otg.express.R;

public class WelcomeActivity extends Activity {

    private ImageView welcomeImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_welcome);

        this.welcomeImage = ((ImageView) findViewById(R.id.welcome_imageview));
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1F, 1.0F);
        alphaAnimation.setDuration(2000L);
        this.welcomeImage.startAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation paramAnonymousAnimation) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
//                Intent intent = new Intent(WelcomeActivity.this, MainFragmentActivity.class);
                startActivity(intent);
                finish();
            }

            public void onAnimationRepeat(Animation paramAnonymousAnimation) {
            }

            public void onAnimationStart(Animation paramAnonymousAnimation) {
            }


        });
    }



    protected void onDestroy() {
        super.onDestroy();
        this.welcomeImage.clearAnimation();
    }
}
