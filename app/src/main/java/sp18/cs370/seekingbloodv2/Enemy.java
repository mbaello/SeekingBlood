package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

public class Enemy extends Entity implements GameObject {
    // Extra variables can go here if necessary
    private int timer;
    private int timerMax;

    Bitmap bleedBmp;
    Rect bleedBox;
    boolean isBleeding;
    int bleedFrame;
    int bleedTimer;
    int hitTimer;

    // Constructor
    Enemy(Rect visualHitbox) {
        Random rand = new Random(System.nanoTime());
        this.health = 200.0;
        this.timer = 0;
        this.timerMax = rand.nextInt(60) + 60;
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
        this.frame = 0;
        this.xVelocity = 0;
        this.xVelocityInitial = rand.nextInt(2) + 2;
        System.out.println("Enemy has " + xVelocityInitial + " speed!");
        this.yVelocity = 0;
        this.yVelocityInitial = Constants.HEROJUMPVELOCITY;
        this.isBleeding = false;
        this.bleedFrame = -1;
        this.hitTimer = 0;
        this.bleedTimer = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        // Update the enemy visually to the screen (Look at the Hero's draw method for an example)
        canvas.drawBitmap(rightBmp, new Rect(0, 0, rightBmp.getWidth(), rightBmp.getHeight()), visualHitbox, null);
        if(isBleeding) {
            canvas.drawBitmap(bleedBmp, new Rect(Constants.BLEEDSPRITEWIDTH * bleedFrame, 0,
                    Constants.BLEEDSPRITEWIDTH * (bleedFrame + 1) - 1, Constants.BLEEDSPRITEHEIGHT - 1), bleedBox, null);
        }
    }

    @Override
    public void update() {
        // Conditions/Logic/AI go in here (This method runs approximately 50 times a second)
        visualHitbox = physicalHitbox;
        bleedBox = visualHitbox;
        // System.out.println("Bleed Timer = " + bleedTimer + "Bleed Frame = " + bleedFrame);
        if(isBleeding) {
            if(bleedFrame < 5 && ((bleedTimer % 2) == 0)) {
                bleedFrame++;
                bleedTimer++;
            } else if(bleedTimer > 12){
                bleedFrame = -1;
                bleedTimer = 0;
                isBleeding = false;
            } else
                bleedTimer++;
        }
        if(hitTimer > 0)
            hitTimer--;
    }

    public void update(ArrayList<Obstructable> obstructables, Hero hero) {
        MoveToPlayer(obstructables, hero);
        jumpOnPlatform(obstructables, hero);
        Fall(obstructables);
        Jump(obstructables);
        this.update();
    }

    public void MoveToPlayer(ArrayList<Obstructable> obstructables, Hero hero) {
        int distanceToHero = physicalHitbox.left - hero.physicalHitbox.left; // If positive, Hero is to the left of the enemy.
        if(distanceToHero > 0) {
            xVelocity = -xVelocityInitial;
            Move(obstructables);
        } else {
            xVelocity = xVelocityInitial;
            Move(obstructables);
        }
    }

    public void jumpOnPlatform(ArrayList<Obstructable> obstructables, Hero hero){
        int heightDifference = physicalHitbox.top - hero.physicalHitbox.bottom;
        if(heightDifference > 0) {
            for (int i = 0; i < obstructables.size(); i++){
                Rect platformSearch = new Rect(physicalHitbox.left, physicalHitbox.top + 30, physicalHitbox.right, physicalHitbox.top);
                if(platformSearch.intersect(obstructables.get(i).getHitbox()) && !obstructables.get(i).isNotPhysical)
                    startJump = true;
            }
        }
    }
}
