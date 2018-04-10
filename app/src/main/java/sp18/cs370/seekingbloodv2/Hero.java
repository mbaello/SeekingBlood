package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

public class Hero extends Entity implements GameObject {
    private ArrayList<Bitmap> bmp; // If using sprite sheets, this will just be a plain bmp.
    private boolean isWalking;
    private double stamina;
    private double reserve;

    Hero(Rect hitbox) {
        this.bmp = new ArrayList<>(60);
        this.isWalking = false;
        this.health = 100.0;
        this.reserve = 50.0;
        this.stamina = 100.0;
        this.hitbox = hitbox;
        this.isFalling = true;
        this.onGround = false;
        this.startJump = false;
        this.entityHeight = 0;
        this.frame = 0;
        this.xVelocity = 0;
        this.xVelocityInitial = 0;
        this.yVelocity = 0;
        this.yVelocityInitial = Constants.HEROJUMPVELOCITY;
    }

    public void addBmp(Bitmap bmp) {
        this.bmp.add(bmp);
    }

    // Overridden GameObject methods

    @Override
    public void draw(Canvas canvas) {
        if (!isOnGround()) { // Checks if Hero is in the air
            canvas.drawBitmap(bmp.get(11), new Rect(0, 0, bmp.get(11).getWidth(), bmp.get(11).getHeight()), hitbox, null);
        } else if(!isWalking) { // Checks if Hero is standing still
            canvas.drawBitmap(bmp.get(0), new Rect(0, 0, bmp.get(0).getWidth(), bmp.get(0).getHeight()), hitbox, null);
        } else { // Walking
            switch (frame) {
                case 1:
                    canvas.drawBitmap(bmp.get(1), new Rect(0, 0, bmp.get(1).getWidth(), bmp.get(1).getHeight()), hitbox, null);
                    break;
                case 2:
                    canvas.drawBitmap(bmp.get(2), new Rect(0, 0, bmp.get(2).getWidth(), bmp.get(2).getHeight()), hitbox, null);
                    break;
                case 3:
                    canvas.drawBitmap(bmp.get(3), new Rect(0, 0, bmp.get(3).getWidth(), bmp.get(3).getHeight()), hitbox, null);
                    break;
                case 4:
                    canvas.drawBitmap(bmp.get(4), new Rect(0, 0, bmp.get(4).getWidth(), bmp.get(4).getHeight()), hitbox, null);
                    break;
                case 5:
                    canvas.drawBitmap(bmp.get(5), new Rect(0, 0, bmp.get(5).getWidth(), bmp.get(5).getHeight()), hitbox, null);
                    break;
                case 6:
                    canvas.drawBitmap(bmp.get(6), new Rect(0, 0, bmp.get(6).getWidth(), bmp.get(6).getHeight()), hitbox, null);
                    break;
                case 7:
                    canvas.drawBitmap(bmp.get(7), new Rect(0, 0, bmp.get(7).getWidth(), bmp.get(7).getHeight()), hitbox, null);
                    break;
                case 8:
                    canvas.drawBitmap(bmp.get(8), new Rect(0, 0, bmp.get(8).getWidth(), bmp.get(8).getHeight()), hitbox, null);
                    break;
                case 9:
                    canvas.drawBitmap(bmp.get(9), new Rect(0, 0, bmp.get(9).getWidth(), bmp.get(9).getHeight()), hitbox, null);
                    break;
                case 10:
                    canvas.drawBitmap(bmp.get(10), new Rect(0, 0, bmp.get(10).getWidth(), bmp.get(10).getHeight()), hitbox, null);
                    break;
            }
        }
    }

    @Override
    public void update() {
        if(isWalking && frame == 0)
            frame = 1;
        else if(isWalking){
            if(frame < 10)
                frame++;
            else
                frame = 1;
        }
    }

    // Any horizontal movement will change the left and right bounds of the hitbox.
    // Any vertical movement will change the top and bottom bounds of the hitbox.
    public void update(int zone, ArrayList<Obstructable> obstructables) {
        if (zone == 1) { // Sprint Right
            isWalking = true;
            xVelocity = Constants.HEROFASTMOVE;
            MoveFastR(obstructables);
        } else if (zone == 2) { // Walk Right
            isWalking = true;
            xVelocity = Constants.HEROSLOWMOVE;
            MoveSlowR(obstructables);
        } else if (zone == 3) { // Walk Left
            isWalking = true;
            xVelocity = Constants.HEROSLOWMOVE * -1;
            MoveSlowL(obstructables);
        } else if (zone == 4) {// Sprint Left
            isWalking = true;
            xVelocity = Constants.HEROFASTMOVE * -1;
            MoveFastL(obstructables);
        } else {
            isWalking = false;
            frame = 0;
        }
        // Fall primarily checks for horizontal Obstructables
        Fall(obstructables);
        Jump(obstructables);
        this.update();
    }
}
