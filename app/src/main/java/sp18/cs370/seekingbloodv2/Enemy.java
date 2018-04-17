package sp18.cs370.seekingbloodv2;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

public class Enemy extends Entity implements GameObject {
    // Extra variables can go here if necessary
    private int timer;

    // Constructor
    Enemy(Rect visualHitbox) {
        this.health = 200.0;
        this.timer = 0;
        this.visualHitbox = visualHitbox;
        this.physicalHitbox = visualHitbox;
        this.isFacingLeft = false;
        this.isWalking = false;
        this.isRunning = false;
        this.health = 100.0;
        this.isFalling = true;
        this.onGround = false;
        this.startJump = false;
        this.entityHeight = 0;
        this.xFrame = 0;
        this.xVelocity = 0;
        this.xVelocityInitial = 0;
        this.yFrame = 0;
        this.yVelocity = 0;
        this.yVelocityInitial = Constants.HEROJUMPVELOCITY;
    }

    @Override
    public void draw(Canvas canvas) {
        // Update the enemy visually to the screen (Look at the Hero's draw method for an example)
        canvas.drawBitmap(rightBmp, new Rect(0, 0, rightBmp.getWidth(), rightBmp.getHeight()), visualHitbox, null);
    }

    @Override
    public void update() {
        // Conditions/Logic/AI go in here (This method runs approximately 50 times a second)
        visualHitbox = physicalHitbox;
        if(timer == 0) {
            startJump = true;
            timer++;
        } else if(timer > 120)
            timer = 0;
        else
            timer++;
    }

    public void update(ArrayList<Obstructable> obstructables, Hero hero) {
        MoveToPlayer(obstructables, hero);
        Fall(obstructables);
        Jump(obstructables);
        this.update();
    }

    public void MoveToPlayer(ArrayList<Obstructable> obstructables, Hero hero) {
        int distanceToHero = physicalHitbox.left - hero.physicalHitbox.left; // If positive, Hero is to the left of the enemy.
        if(distanceToHero > 0) {
            xVelocity = Constants.ENEMYSLOWMOVE * -1;
            Move(obstructables);
        } else {
            xVelocity = Constants.ENEMYSLOWMOVE;
            Move(obstructables);
        }
    }
}
