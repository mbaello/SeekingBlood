package sp18.cs370.seekingbloodv2;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class  LevelActivity extends Activity {

    private Button menu_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the width and height of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Constants.SCREENWIDTH = displayMetrics.widthPixels;
        Constants.SCREENHEIGHT = displayMetrics.heightPixels;
        // Set the content view
        setContentView(new Game(this));

        menu_button = (Button) findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openTitleActivity();

            }
        });
    }

    public void openTitleActivity(){
        Intent intent = new Intent(this, TitleActivity.class);
        startActivity(intent);
    }


}


