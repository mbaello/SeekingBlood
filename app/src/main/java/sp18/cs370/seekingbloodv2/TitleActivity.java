package sp18.cs370.seekingbloodv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TitleActivity extends AppCompatActivity {

    public void sendGame(View view) {
        Intent startNewActivity = new Intent(this, LevelActivity.class);
        startActivity(startNewActivity);

    }

    public Button start_button;
    private Button menu_button;
    private Button help_button;
    private Button return_button;
    private Button shop_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);


    }
}
