package sp18.cs370.seekingbloodv2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelpActivity extends Activity {
    private Button return_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        return_button = (Button) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               openLevelActivity();
            }
        });
    }

    public void openLevelActivity(){
        Intent continueGame = new Intent(this, LevelActivity.class);
        startActivity(continueGame);
    }
}
