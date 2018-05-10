package sp18.cs370.seekingbloodv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

    Button start_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        start_button = findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTitleActivity();
            }
        });
    }

    public void openTitleActivity() {
        Intent startNewGame = new Intent(this, TitleActivity.class);
        startActivity(startNewGame);
    }
}