package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

public class Enemy extends Entity implements GameObject {
    // Extra variables can go here if necessary
    private int timer;

    Bitmap bleedBmp;
    Rect bleedBox;
    boolean isBleeding;
    int bleedFrame;
    int bleedTimer;
    int hitTimer;

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
        this.frame = 0;
        this.xVelocity = 0;
        this.xVelocityInitial = 0;
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
        visualHitbox = physicalHitbox;
        bleedBox = visualHitbox;
        // Conditions/Logic/AI go in here (This method runs approximately 50 times a second)
        // System.out.println("visualHitbox width = " + visualHitbox.width());
        // System.out.println("physicalHitbox width = " + physicalHitbox.width());
        // System.out.println("bleedBox width = " + bleedBox.width());
        if(timer == 0) {
            if(isOnGround())
                // startJump = true;
                timer++;
        } else if(timer > 120)
            timer = 0;
        else
            timer++;
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
        jumpToPlayer(hero);
        detectObstruction(obstructables);
        //detectFall(obstructables, hero);
        Fall(obstructables);
        Jump(obstructables);
        detectCollision(hero);
        this.update();
    }

    public void MoveToPlayer(ArrayList<Obstructable> obstructables, Hero hero) {
        int distanceToHero = physicalHitbox.left - hero.physicalHitbox.left; // If positive, Hero is to the left of the enemy.
        if(distanceToHero > 0) {
            if(distanceToHero > 300){ //If over 300 units away enemy will sprint
                xVelocity = -Constants.ENEMYFASTMOVE;
                Move(obstructables);
            }
            else {
                xVelocity = -Constants.ENEMYSLOWMOVE;
                Move(obstructables);
            }
        } else {
            if(distanceToHero < -300){ //If over 300 units away enemy will sprint
                xVelocity = Constants.ENEMYFASTMOVE;
                Move(obstructables);
            }
            else {
                xVelocity = Constants.ENEMYSLOWMOVE;
                Move(obstructables);
            }
        }
    }

    public void jumpToPlayer(Hero hero){
        int heightDifference = physicalHitbox.top - hero.physicalHitbox.bottom;
        if(timer == 0) {
            if (heightDifference > 0 && !hero.isOnGround())
                if (isOnGround() && !startJump)
                    startJump = true;
            timer = timer + 2;
        }
        else if(timer > 120)
            timer = 0;
        else
            timer = timer + 2;
    }

    public void jumpOnPlatform(ArrayList<Obstructable> obstructables, Hero hero){
        int heightDifference = physicalHitbox.top - hero.physicalHitbox.bottom;
        if(heightDifference > 0) {
            for (int i = 0; i < obstructables.size(); i++){
                Rect platformSearch = new Rect(physicalHitbox.left, physicalHitbox.top + 50, physicalHitbox.right, physicalHitbox.bottom);
                if(platformSearch.intersect(obstructables.get(i).getHitbox()) && !obstructables.get(i).isNotPhysical && !startJump)
                    startJump = true;
            }
        }
    }

    public void detectObstruction(ArrayList<Obstructable> obstructables){
        for (int i = 0; i < obstructables.size(); i++){
            Rect obstructionSearch = new Rect(physicalHitbox.left + 10, 0, physicalHitbox.right + 10, 0);
            if(Rect.intersects(obstructionSearch, obstructables.get(i).getHitbox()) && !obstructables.get(i).isNotPhysical)
                if(isOnGround() && !startJump)
                    startJump = true;
        }
    }

    public void detectFall(ArrayList<Obstructable> obstructables, Hero hero) {
        int collisionCount = 0;
        int heightDifference = physicalHitbox.top - hero.physicalHitbox.bottom;
        if(heightDifference > 0) {
            for (int i = 0; i < obstructables.size(); i++) {
                Rect fallSearch = new Rect(physicalHitbox.left + xVelocity, physicalHitbox.top, physicalHitbox.right + xVelocity, physicalHitbox.bottom + 50);
                if (fallSearch.intersect(obstructables.get(i).getHitbox()) && !obstructables.get(i).isNotPhysical) // If the character is over nothing...
                    collisionCount++;
            }
            if(collisionCount == 0){
                System.out.println("Fall detected");
                startJump = true;
            }
        }
    }

    public void detectCollision(Hero hero){
        if(Rect.intersects(physicalHitbox, hero.physicalHitbox)) {
            // System.out.println("Collision between hero and enemy!");
        }
    }
}
