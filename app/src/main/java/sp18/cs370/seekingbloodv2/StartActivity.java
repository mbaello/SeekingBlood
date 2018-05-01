package sp18.cs370.seekingbloodv2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

    private Button start_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        start_button = (Button) findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openLevelActivity();

            }
        });
    }

    public void openLevelActivity(){
        Intent startNewGame = new Intent(this, LevelActivity.class);
        startActivity(startNewGame);


    }
}
