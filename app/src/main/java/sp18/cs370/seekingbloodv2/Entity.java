package sp18.cs370.seekingbloodv2;

import android.graphics.Rect;

import java.lang.reflect.Array;
import java.util.ArrayList;

class Entity {
    Rect hitbox;
    boolean isFalling;
    boolean onGround;
    boolean startJump;
    double health;
    int entityHeight;
    int frame;
    int xVelocity;
    int xVelocityInitial;
    int yVelocity;
    int yVelocityInitial;

    public Rect getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rect hitbox) {
        this.hitbox = hitbox;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setStartJump(boolean startJump) {
        this.startJump = startJump;
    }

    void Fall(ArrayList<Obstructable> obstructables) {
        int collisionCount = 0;
        if (onGround) {
            int tempVelocity = yVelocity + Constants.GRAVITY;
            Rect newHitbox = new Rect(hitbox.left, hitbox.top + tempVelocity, hitbox.right, hitbox.bottom + tempVelocity);
            for (int i = 0; i < obstructables.size(); i++)
                if (newHitbox.intersect(obstructables.get(i).getHitbox())) // If the character is over nothing...
                    collisionCount++;
            if(collisionCount == 0) {
                // Then mark them as airborne and have them fall.
                setOnGround(false);
            }
        }
    }

    void Jump(ArrayList<Obstructable> obstructables) {
        if (startJump) {
            startJump = false;
            yVelocity = yVelocityInitial;
            hitbox = new Rect(hitbox.left, hitbox.top + yVelocity, hitbox.right, hitbox.bottom + yVelocity);
            setOnGround(false);
        } else if (!onGround) {
            for (int i = 0; i < obstructables.size(); i++) {
                Rect newHitbox = new Rect(hitbox.left, hitbox.top + yVelocity, hitbox.right, hitbox.bottom + yVelocity);
                if (newHitbox.intersect(obstructables.get(i).getHitbox())) {
                    int bottom = obstructables.get(i).getHitboxTop();
                    hitbox = new Rect(hitbox.left, bottom - Constants.HEROHEIGHT, hitbox.right, bottom);
                    setOnGround(true);
                    yVelocity = 0;
                    break;
                }
            }
            if (!onGround) {
                hitbox = new Rect(hitbox.left, hitbox.top + yVelocity, hitbox.right, hitbox.bottom + yVelocity);
                yVelocity += Constants.GRAVITY;
            }
        }
    }

    void MoveFastR(ArrayList<Obstructable> obstructables) {
        if(IsMoveValid(obstructables))
            hitbox = new Rect(hitbox.left + xVelocity, hitbox.top, hitbox.right + xVelocity, hitbox.bottom);
    }

    void MoveSlowR(ArrayList<Obstructable> obstructables) {
        if(IsMoveValid(obstructables))
            hitbox = new Rect(hitbox.left + xVelocity, hitbox.top, hitbox.right + xVelocity, hitbox.bottom);
    }

    void MoveSlowL(ArrayList<Obstructable> obstructables) {
        if(IsMoveValid(obstructables))
            hitbox = new Rect(hitbox.left + xVelocity, hitbox.top, hitbox.right + xVelocity, hitbox.bottom);
    }

    void MoveFastL(ArrayList<Obstructable> obstructables) {
        if(IsMoveValid(obstructables))
            hitbox = new Rect(hitbox.left + xVelocity, hitbox.top, hitbox.right + xVelocity, hitbox.bottom);
    }

    boolean IsMoveValid(ArrayList<Obstructable> obstructables) {
        for(int i = 0; i < obstructables.size(); i++) {
            if(!obstructables.get(i).isHorizontal()) { // Loops through vertical obstructables
                Rect newHitbox = new Rect(hitbox.left + xVelocity, hitbox.top, hitbox.right + xVelocity, hitbox.bottom);
                if(newHitbox.intersect(obstructables.get(i).getHitbox()))
                    return false;
            }
        }
        return true;
    }
}
