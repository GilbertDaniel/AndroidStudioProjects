package scolttm.examples;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.util.Random;

public class MovingButtonActivity extends Activity {
    
    private Random randNumGen;
    private static final int NUM_SHADES = 256;

    public static int TWEEN = 0;
    public static int PROPERTY = 1;


    
    public static final String ANIMATION_TAG = "anim";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_move);
        randNumGen = new Random();
        Intent intent = getIntent();
        int animationType = TWEEN;
        if(intent.hasExtra(ANIMATION_TAG)) {
            animationType = intent.getExtras().getInt(ANIMATION_TAG);
        }
        if(animationType == TWEEN)
            tweenedAnimation();
        else
            propertyAnimation();
    }
    
    private void propertyAnimation() {
        Button movingButton 
            = (Button) findViewById(R.id.change_background);
        ObjectAnimator anim 
            = ObjectAnimator.ofFloat(movingButton, "y", 0, 700);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setDuration(2000);
        anim.start();
    }

    public void tweenedAnimation() {
        Button movingButton 
                = (Button) findViewById(R.id.change_background);
        
        movingButton.startAnimation(
                AnimationUtils.loadAnimation(this, 
                        R.anim.up_and_down));
    }
    
    public void changeBackground(View v) {
        // example of using ViewAnimator
//        if(v.getAlpha() == 0)
//            v.animate().setDuration(3000).alpha(1);
//        else
//            v.animate().alpha(0);
        View target = (View) findViewById(R.id.linear_layout_button);
        int red = randNumGen.nextInt(NUM_SHADES );
        int green = randNumGen.nextInt(NUM_SHADES );
        int blue = randNumGen.nextInt(NUM_SHADES );
        target.setBackgroundColor(Color.argb(255, red, green, blue));
    }
}
