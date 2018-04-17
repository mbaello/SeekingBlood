package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Parcelable;

import java.util.ArrayList;

public class Hero extends Entity implements GameObject {
    Bitmap leftAirborneBmp;
    Bitmap rightAirborneBmp;
    Bitmap leftForwardAttackBmp;
    Bitmap rightForwardAttackBmp;
    Bitmap landingBmp;
    private boolean isAttacking;
    private boolean isLanding;
    private double stamina;
    private double reserve;
    private int attackTimer;
    private int jumpFrame;

    Hero(Rect visualHitbox) {
        this.isAttacking = false;
        this.isLanding = false;
        this.visualHitbox = visualHitbox;
        this.physicalHitbox = new Rect(visualHitbox.left, visualHitbox.top, visualHitbox.left + ((visualHitbox.right - visualHitbox.left) / 2), visualHitbox.bottom);
        this.isFacingLeft = false;
        this.isWalking = false;
        this.isRunning = false;
        this.health = 100.0;
        this.reserve = 50.0;
        this.stamina = 100.0;
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
        this.attackTimer = 0;
    }

    // Overridden GameObject methods

    @Override
    public void draw(Canvas canvas) {
        Bitmap bmp;
        if(isFacingLeft)
            bmp = leftBmp;
        else
            bmp = rightBmp;
        if(isLanding) {
            if(xFrame == 0)
                canvas.drawBitmap(landingBmp, new Rect(0, 0, Constants.HEROLANDSPRITEWIDTH - 1, Constants.HEROLANDSPRITEHEIGHT - 1),
                        visualHitbox, null);
            else
                canvas.drawBitmap(landingBmp, new Rect(Constants.HEROLANDSPRITEWIDTH * xFrame - 1, 0, Constants.HEROLANDSPRITEWIDTH * (xFrame + 1) - 1,
                        Constants.HEROLANDSPRITEHEIGHT - 1), visualHitbox, null);
        } else if (!onGround) {
            if (isFacingLeft)
                canvas.drawBitmap(leftAirborneBmp, new Rect(0, 0, leftAirborneBmp.getWidth(), leftAirborneBmp.getHeight()),
                        visualHitbox, null);
            else
                canvas.drawBitmap(rightAirborneBmp, new Rect(0, 0, rightAirborneBmp.getWidth(), rightAirborneBmp.getHeight()),
                        visualHitbox, null);
        } else if (!isWalking) // Hero is standing
            canvas.drawBitmap(bmp, new Rect(Constants.HEROWALKSPRITEWIDTH * 9 - 1, Constants.HEROWALKSPRITEHEIGHT * 9 - 1,
                    Constants.HEROWALKSPRITEWIDTH * 10 - 1, Constants.HEROWALKSPRITEHEIGHT * 10 - 1), visualHitbox, null);
        else { // Hero is walking
            if (xFrame == 0 && yFrame == 0)
                canvas.drawBitmap(bmp, new Rect(0, 0, Constants.HEROWALKSPRITEWIDTH - 1, Constants.HEROWALKSPRITEHEIGHT),
                        visualHitbox, null);
            else if (xFrame == 0)
                canvas.drawBitmap(bmp, new Rect(0, Constants.HEROWALKSPRITEHEIGHT * yFrame - 1, Constants.HEROWALKSPRITEWIDTH,
                        Constants.HEROWALKSPRITEHEIGHT * (yFrame + 1) - 1), visualHitbox, null);
            else if (yFrame == 0)
                canvas.drawBitmap(bmp, new Rect(Constants.HEROWALKSPRITEWIDTH * xFrame - 1, 0, Constants.HEROWALKSPRITEWIDTH * (xFrame + 1) - 1,
                        Constants.HEROWALKSPRITEHEIGHT - 1), visualHitbox, null);
            else
                canvas.drawBitmap(bmp, new Rect(Constants.HEROWALKSPRITEWIDTH * xFrame - 1, Constants.HEROWALKSPRITEHEIGHT * yFrame - 1,
                                Constants.HEROWALKSPRITEWIDTH * (xFrame + 1) - 1, Constants.HEROWALKSPRITEHEIGHT * (yFrame + 1) - 1),
                        visualHitbox, null);
        }
    }

    @Override
    public void update() {
        if(isLanding) {
            if(xFrame < 0) {
                xFrame = 0;
            } else if(xFrame == 45) {
                isLanding = false;
            } else {
                xFrame++;
            }
        } else if(isAttacking) {
            System.out.println("Attacking!");
            attackTimer++;
            if(attackTimer == 20)
                isAttacking = false;
            // After certain frames, set isAttacking to false. Set hitbox to 0, 0, 0, 0.
        } else if(isRunning) { // Running
            if(isFacingLeft) { // Facing Left
                visualHitbox = new Rect(physicalHitbox.right - ((physicalHitbox.right - physicalHitbox.left) * 2), physicalHitbox.top, physicalHitbox.right,
                        physicalHitbox.bottom);
                xFrame -= 2;
                if(xFrame < 0) {
                    xFrame = 9;
                    yFrame -= 2;
                    if(yFrame < 0)
                        yFrame = 19;
                }
            } else { // Facing Right
                visualHitbox = new Rect(physicalHitbox.left, physicalHitbox.top, physicalHitbox.left + ((physicalHitbox.right - physicalHitbox.left) * 2),
                        physicalHitbox.bottom);
                xFrame += 2;
                if(xFrame > 9) {
                    xFrame = 0;
                    yFrame += 2;
                    if(yFrame > 19)
                        yFrame = 0;
                }
            }
        } else if(isWalking) { // Walking
            if(isFacingLeft) { // Facing Left
                visualHitbox = new Rect(physicalHitbox.right - ((physicalHitbox.right - physicalHitbox.left) * 2), physicalHitbox.top, physicalHitbox.right,
                        physicalHitbox.bottom);
                xFrame--;
                if(xFrame < 0) {
                    xFrame = 9;
                    yFrame--;
                    if(yFrame < 0)
                        yFrame = 19;
                }
            } else { // Facing Right
                visualHitbox = new Rect(physicalHitbox.left, physicalHitbox.top, physicalHitbox.left + ((physicalHitbox.right - physicalHitbox.left) * 2),
                        physicalHitbox.bottom);
                xFrame++;
                if(xFrame > 9) {
                    xFrame = 0;
                    yFrame++;
                    if(yFrame > 19)
                        yFrame = 0;
                }
            }
        }
    }

    // Any horizontal movement will change the left and right bounds of the hitbox.
    // Any vertical movement will change the top and bottom bounds of the hitbox.
    public void update(int zone, ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if(onGround) {
            if (zone == 1) { // Sprint Right
                isFacingLeft = false;
                isWalking = true;
                isRunning = true;
                xVelocity = Constants.HEROFASTMOVE;
                HeroMove(obstructables, enemies);
            } else if (zone == 2) { // Walk Right
                isFacingLeft = false;
                isWalking = true;
                isRunning = false;
                xVelocity = Constants.HEROSLOWMOVE;
                HeroMove(obstructables, enemies);
            } else if (zone == 3) { // Walk Left
                isFacingLeft = true;
                isWalking = true;
                isRunning = false;
                xVelocity = Constants.HEROSLOWMOVE * -1;
                HeroMove(obstructables, enemies);
            } else if (zone == 4) {// Sprint Left
                isFacingLeft = true;
                isWalking = true;
                isRunning = true;
                xVelocity = Constants.HEROFASTMOVE * -1;
                HeroMove(obstructables, enemies);
            } else {
                isWalking = false;
                isRunning = false;
                if(!isLanding)
                    xFrame = 9;
                yFrame = 19;
                xVelocity = 0;
            }
        }
        if(isWalking)
            HeroMove(obstructables, enemies);
        // Fall primarily checks for horizontal Obstructables
        HeroFall(obstructables, enemies);
        HeroJump(obstructables, enemies);
        this.update();
    }

    private void HeroFall(ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
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

    private void HeroJump(ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if (startJump) {
            System.out.println("Jumping now!");
            startJump = false;
            yVelocity = yVelocityInitial;
            // Instead of updating the character, update the screen.
            HeroMove(obstructables, enemies);
            for(Obstructable obs : obstructables)
                obs.setHitbox(new Rect(obs.getHitbox().left, obs.getHitbox().top - yVelocity, obs.getHitbox().right,
                        obs.getHitbox().bottom - yVelocity));
            for(Enemy enemy: enemies)
                enemy.setPhysicalHitbox(new Rect(enemy.physicalHitbox.left, enemy.physicalHitbox.top - yVelocity, enemy.physicalHitbox.right,
                        enemy.physicalHitbox.bottom - yVelocity));
            onGround = false;
        } else if (!onGround) {
            for (int i = 0; i < obstructables.size(); i++) {
                Rect newHitbox = new Rect(physicalHitbox.left, physicalHitbox.bottom, physicalHitbox.right, physicalHitbox.bottom + yVelocity);
                if (newHitbox.intersect(obstructables.get(i).getHitbox()) && isFalling) {
                    System.out.println("Touched down - Not falling anymore!");
                    int difference = physicalHitbox.bottom - obstructables.get(i).getHitbox().top;
                    System.out.println("Difference = " + difference);
                    HeroMove(obstructables, enemies);
                    for(Obstructable obs : obstructables)
                        obs.setHitbox(new Rect(obs.getHitbox().left, obs.getHitbox().top + difference, obs.getHitbox().right,
                                obs.getHitbox().bottom + difference));
                    for(Enemy enemy: enemies)
                        enemy.setPhysicalHitbox(new Rect(enemy.physicalHitbox.left, enemy.physicalHitbox.top + difference, enemy.physicalHitbox.right,
                                enemy.physicalHitbox.bottom + difference));
                    onGround = true;
                    isFalling = false;
                    isLanding = true;
                    yVelocity = 0;
                    System.out.println("Ground Top = " + obstructables.get(i).getHitbox().top);
                    System.out.println("Hero Bottom = " + physicalHitbox.bottom);
                    break;
                }
            }
            if (!onGround) {
                HeroMove(obstructables, enemies);
                for(Obstructable obs : obstructables)
                    obs.setHitbox(new Rect(obs.getHitbox().left, obs.getHitbox().top - yVelocity, obs.getHitbox().right,
                            obs.getHitbox().bottom - yVelocity));
                for(Enemy enemy: enemies)
                    enemy.setPhysicalHitbox(new Rect(enemy.physicalHitbox.left, enemy.physicalHitbox.top - yVelocity, enemy.physicalHitbox.right,
                            enemy.physicalHitbox.bottom - yVelocity));
                yVelocity += Constants.GRAVITY;
                if(yVelocity > 0 && !isFalling) {
                    isFalling = true;
                    System.out.println("Falling now!");
                }
            }
        }
    }

    private void HeroMove(ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if(IsMoveValid(obstructables)) {
            for (Obstructable obs : obstructables)
                obs.setHitbox(new Rect(obs.getHitbox().left - xVelocity, obs.getHitbox().top, obs.getHitbox().right - xVelocity,
                        obs.getHitbox().bottom));
            for (Enemy enemy : enemies)
                enemy.setPhysicalHitbox(new Rect(enemy.physicalHitbox.left - xVelocity, enemy.physicalHitbox.top, enemy.physicalHitbox.right - xVelocity,
                        enemy.physicalHitbox.bottom));
        }
    }

    void MeleeAttack(int attack) { // Attack 1 is long-range, Attack 2 is short-range
        if(onGround && !isAttacking) {
            isAttacking = true;
            attackTimer = 0;
        }
    }
}
