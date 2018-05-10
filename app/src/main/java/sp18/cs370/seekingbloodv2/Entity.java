package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.ArrayList;

class Entity {
    Animation idle;
    Animation walk;
    Animation jumpStart;
    Animation airborne;
    Animation land;
    Animation dying;
    Rect physicalHitbox;
    Rect visualHitbox;
    Rect attackHitbox;
    boolean isDying;
    boolean isFalling;
    boolean isFacingLeft;
    boolean isIdle;
    boolean isLanding;
    boolean isWalking;
    boolean isRunning;
    boolean onGround;
    boolean startJump;
    boolean draw;
    boolean isHit;
    double health;
    int entityHeight;
    int recentlyHitTimer;
    int xVelocity;
    int xVelocityInitial;
    int yVelocity;
    int yVelocityInitial;

    void Fall(ArrayList<Obstructable> obstructables) {
        if (onGround) {
            int collisionCount = 0;
            int tempVelocity = yVelocity + Constants.GRAVITY;
            Rect newHitbox = new Rect(physicalHitbox.left, physicalHitbox.bottom, physicalHitbox.right, physicalHitbox.bottom + tempVelocity);
            for (int i = 0; i < obstructables.size(); i++)
                if(Rect.intersects(newHitbox, obstructables.get(i).physicalHitbox) && !obstructables.get(i).isNotPhysical) { // Check for a platform under the character
                    collisionCount++; // Increase the count for each platform under the character
                }
            if(collisionCount == 0) {
                // If there are no platforms under the character, have them fall.
                // System.out.println("Entity - No floor detected!");
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
            // System.out.println("Entity - Jumping!");
        } else if (!onGround) {
            for (int i = 0; i < obstructables.size(); i++) {
                Rect newHitbox = new Rect(physicalHitbox.left, physicalHitbox.bottom, physicalHitbox.right, physicalHitbox.bottom + yVelocity);
                if(Rect.intersects(newHitbox, obstructables.get(i).physicalHitbox) && isFalling && !obstructables.get(i).isNotPhysical) {
                    int bottom = obstructables.get(i).physicalHitbox.top;
                    physicalHitbox = new Rect(physicalHitbox.left, bottom - entityHeight, physicalHitbox.right, bottom);
                    onGround = true;
                    isFalling = false;
                    isLanding = true;
                    isWalking = false;
                    yVelocity = 0;
                    // System.out.println("Entity - Landed!");
                    break;
                }
            }
            if (!onGround) {
                physicalHitbox = new Rect(physicalHitbox.left, physicalHitbox.top + yVelocity, physicalHitbox.right, physicalHitbox.bottom + yVelocity);
                yVelocity += Constants.GRAVITY;
                if(yVelocity > 0 && !isFalling) {
                    isFalling = true;
                    // System.out.println("Entity - Falling!");
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
            Rect newHitbox = new Rect(physicalHitbox.left + xVelocity, physicalHitbox.bottom, physicalHitbox.right + xVelocity, physicalHitbox.bottom);
            if(Rect.intersects(newHitbox, obstructables.get(i).physicalHitbox) && !obstructables.get(i).isNotPhysical) {
                return false;
            }
        }
        return true;
    }

    void Knockback(Hero hero, ArrayList<Obstructable> obstructables) {
        if(recentlyHitTimer > 10)
            if (physicalHitbox.right - hero.physicalHitbox.right > 0) {
                xVelocity = Constants.KNOCKBACKVELOCITY;
                Move(obstructables);
            } else {
                xVelocity = -Constants.KNOCKBACKVELOCITY;
                Move(obstructables);
            }
    }
}
