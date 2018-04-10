package sp18.cs370.seekingbloodv2;

import android.graphics.Canvas;

public class Enemy extends Entity implements GameObject {
    // Extra variables can go here if necessary

    // Constructor
    Enemy() {
        health = 200.0;
    }

    @Override
    public void draw(Canvas canvas) {
        // Update the enemy visually to the screen (Look at the Hero's draw method for an example)
    }

    @Override
    public void update() {
        // Conditions/Logic/AI go in here (This method runs approximately 50 times a second)
    }
}
