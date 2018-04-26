package sp18.cs370.seekingbloodv2;

import android.app.Activity;
import android.gesture.Gesture;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class  LevelActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        // Get the width and height of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Constants.SCREENWIDTH = displayMetrics.widthPixels;
        Constants.SCREENHEIGHT = displayMetrics.heightPixels;
        // Set the content view
        //setContentView(new Game(this));
    }



}
