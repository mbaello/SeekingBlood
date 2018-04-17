package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.lang.reflect.Array;
import java.util.ArrayList;

class Entity {
    Bitmap leftBmp;
    Bitmap rightBmp;
    Rect physicalHitbox;
    Rect visualHitbox;
    boolean isFalling;
    boolean isFacingLeft;
    boolean isWalking;
    boolean isRunning;
    boolean onGround;
    boolean startJump;
    double health;
    int entityHeight;
    int xFrame;
    int xVelocity;
    int xVelocityInitial;
    int yFrame;
    int yVelocity;
    int yVelocityInitial;

    public void setLeftBmp(Bitmap leftBmp) {
        this.leftBmp = leftBmp;
        System.out.println("leftBmp WIDTH = " + leftBmp.getWidth());
        System.out.println("leftBmp HEIGHT = " + leftBmp.getHeight());
    }

    public void setRightBmp(Bitmap rightBmp) {
        this.rightBmp = rightBmp;
        System.out.println("rightBmp WIDTH = " + rightBmp.getWidth());
        System.out.println("rightBmp HEIGHT = " + rightBmp.getHeight());
    }

    public Rect getPhysicalHitbox() {
        return physicalHitbox;
    }

    public void setPhysicalHitbox(Rect physicalHitbox) {
        this.physicalHitbox = physicalHitbox;
    }

    public Rect getVisualHitbox() {
        return visualHitbox;
    }

    public void setVisualHitbox(Rect visualHitbox) {
        this.visualHitbox = visualHitbox;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public boolean isOnGround() {
        return onGround;
    }


    public void setStartJump(boolean startJump) {
        this.startJump = startJump;
    }

    void Fall(ArrayList<Obstructable> obstructables) {
        if (onGround) {
            int collisionCount = 0;
            int tempVelocity = yVelocity + Constants.GRAVITY;
            Rect newHitbox = new Rect(physicalHitbox.left, physicalHitbox.top + tempVelocity, physicalHitbox.right, physicalHitbox.bottom + tempVelocity);
            for (int i = 0; i < obstructables.size(); i++)
                if (newHitbox.intersect(obstructables.get(i).getHitbox())) // If the character is over nothing...
                    collisionCount++;
            if(collisionCount == 0) {
                // Then mark them as airborne and have them fall.
                System.out.println("No floor detected!");
                isFalling = true;
                onGround = false;
            }
        }
    }

    void Jump(ArrayList<Obstructable> obstructables) {
        if (startJump) {
            startJump = false;
            yVelocity = yVelocityInitial;
            physicalHitbox = new Rect(physicalHitbox.left, physicalHitbox.top + yVelocity, physicalHitbox.right, physicalHitbox.bottom + yVelocity);
            onGround = false;
        } else if (!onGround) {
            for (int i = 0; i < obstructables.size(); i++) {
                Rect newHitbox = new Rect(physicalHitbox.left, physicalHitbox.bottom - (entityHeight / 3), physicalHitbox.right, physicalHitbox.bottom + yVelocity);
                if (newHitbox.intersect(obstructables.get(i).getHitbox()) && isFalling) {
                    int bottom = obstructables.get(i).getHitbox().top;
                    physicalHitbox = new Rect(physicalHitbox.left, bottom - entityHeight, physicalHitbox.right, bottom);
                    onGround = true;
                    isFalling = false;
                    yVelocity = 0;
                    break;
                }
            }
            if (!onGround) {
                physicalHitbox = new Rect(physicalHitbox.left, physicalHitbox.top + yVelocity, physicalHitbox.right, physicalHitbox.bottom + yVelocity);
                yVelocity += Constants.GRAVITY;
                if(yVelocity > 0 && !isFalling) {
                    isFalling = true;
                }
            }
        }
    }

    void Move(ArrayList<Obstructable> obstructables) {
        if(IsMoveValid(obstructables))
            physicalHitbox = new Rect(physicalHitbox.left + xVelocity, physicalHitbox.top, physicalHitbox.right + xVelocity, physicalHitbox.bottom);
    }

    boolean IsMoveValid(ArrayList<Obstructable> obstructables) {
        for(int i = 0; i < obstructables.size(); i++) {
            Rect newHitbox = new Rect(physicalHitbox.left + xVelocity, physicalHitbox.top, physicalHitbox.right + xVelocity, physicalHitbox.bottom);
            if(newHitbox.intersect(obstructables.get(i).getHitbox()))
                return false;
        }
        return true;
    }
}
