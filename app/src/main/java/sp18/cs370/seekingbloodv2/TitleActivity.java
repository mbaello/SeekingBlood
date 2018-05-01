package sp18.cs370.seekingbloodv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TitleActivity extends AppCompatActivity {

    private Button help_button;
    private Button shop_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        help_button = (Button) findViewById(R.id.help_button);
        help_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openHelpActivity();
            }
        });


        shop_button = (Button) findViewById(R.id.shop_button);
        shop_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    openShopActivity();
            }
        });


        }

    public void openHelpActivity() {
        Intent startHelpActivity = new Intent(this, HelpActivity.class);
        startActivity(startHelpActivity);
    }

    public void openShopActivity(){
        Intent startShopActivity = new Intent(this, ShopActivity.class);
        startActivity(startShopActivity);


    }



}
