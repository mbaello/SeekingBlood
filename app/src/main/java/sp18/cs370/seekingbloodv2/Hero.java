package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

class Hero extends Entity implements GameObject {
    Bitmap leftRunBmp;
    Bitmap rightRunBmp;
    Bitmap leftJumpBmp;
    Bitmap rightJumpBmp;
    Bitmap leftAirborneBmp;
    Bitmap rightAirborneBmp;
    Bitmap leftLandingBmp;
    Bitmap rightLandingBmp;
    Bitmap leftForwardAttackBmp;
    Bitmap rightForwardAttackBmp;
    Bitmap sample;
    Rect attackHitbox;
    private boolean isAttacking;
    private boolean isLanding;
    boolean isRecovering;
    boolean phaseThrough;
    double stamina;
    double staminaRestoreCooldown;
    double reserve;
    double reserveRestoreCooldown;
    private int runFrame;
    private int jumpFrame;
    private int airFrame;
    private int landFrame;
    private int attackFrame;

    Hero(Rect visualHitbox) {
        this.isAttacking = false;
        this.isLanding = false;
        this.isRecovering = false;
        this.phaseThrough = false;
        this.runFrame = 0;
        this.jumpFrame = 0;
        this.airFrame = 0;
        this.landFrame = 0;
        this.attackFrame = 0;
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
        this.frame = 0;
        this.xVelocity = 0;
        this.xVelocityInitial = 0;
        this.yVelocity = 0;
        this.yVelocityInitial = Constants.HEROJUMPVELOCITY;
    }

    // Overridden GameObject methods

    @Override
    public void draw(Canvas canvas) {
        Bitmap bmp;
        Rect rect;
        if(isFacingLeft) { // Facing Left
            visualHitbox = new Rect(physicalHitbox.right - ((physicalHitbox.right - physicalHitbox.left) * 2), physicalHitbox.top, physicalHitbox.right,
                    physicalHitbox.bottom);
            if (!onGround) { // Airborne
                bmp = leftAirborneBmp;
                rect = new Rect((int)(Constants.HEROAIRBORNESPRITEWIDTH * airFrame), 0, (int)(Constants.HEROAIRBORNESPRITEWIDTH * (airFrame + 1) - 1),
                        (int)Constants.HEROAIRBORNESPRITEHEIGHT - 1);
            } else if (isAttacking) { // Attacking
                bmp = leftForwardAttackBmp;
                rect = new Rect((int)(Constants.HEROFORATKSPRITEWIDTH * attackFrame), 0, (int)(Constants.HEROFORATKSPRITEWIDTH * (attackFrame + 1) - 1),
                        (int)Constants.HEROFORATKSPRITEHEIGHT - 1);
                visualHitbox = new Rect(visualHitbox.right - (int)((visualHitbox.right - visualHitbox.left) * 1.15), visualHitbox.top,
                        visualHitbox.right, visualHitbox.bottom);
                if(attackFrame < 20) {
                    attackHitbox = new Rect(visualHitbox.left, visualHitbox.top, visualHitbox.left + 20, visualHitbox.bottom);
                }
            } else if (startJump) { // Starting Jump
                bmp = leftJumpBmp;
                rect = new Rect((int)(Constants.HEROJUMPSPRITEWIDTH * jumpFrame), 0, (int)(Constants.HEROJUMPSPRITEWIDTH * (jumpFrame + 1) - 1),
                        (int)Constants.HEROJUMPSPRITEHEIGHT - 1);
            } else if (isLanding) { // Landing
                bmp = leftLandingBmp;
                rect = new Rect((int)(Constants.HEROLANDSPRITEWIDTH * landFrame), 0, (int)(Constants.HEROLANDSPRITEWIDTH * (landFrame + 1) - 1),
                        (int)Constants.HEROLANDSPRITEHEIGHT - 1);
            } else if (isRunning) { // Running
                bmp = leftRunBmp;
                rect = new Rect((int)(Constants.HERORUNSPRITEWIDTH * runFrame), 0, (int)(Constants.HERORUNSPRITEWIDTH * (runFrame + 1) - 1),
                        (int)Constants.HERORUNSPRITEHEIGHT - 1);
                visualHitbox = new Rect(visualHitbox.right - (int)((visualHitbox.right - visualHitbox.left) * 0.8), visualHitbox.bottom - (int)((visualHitbox.bottom - visualHitbox.top) * 1.3),
                        visualHitbox.right, visualHitbox.bottom);
            } else if (isWalking) { // Walking
                bmp = leftBmp;
                rect = new Rect((int)(Constants.HEROWALKSPRITEWIDTH * frame), 0, (int)(Constants.HEROWALKSPRITEWIDTH * (frame + 1) - 1),
                        (int)Constants.HEROWALKSPRITEHEIGHT - 1);
            } else { // Idle
                bmp = leftBmp;
                rect = new Rect(0, 0, (int)Constants.HEROWALKSPRITEWIDTH - 1, (int)Constants.HEROWALKSPRITEHEIGHT - 1);
            }
        } else { // Facing Right
            visualHitbox = new Rect(physicalHitbox.left, physicalHitbox.top, physicalHitbox.left + ((physicalHitbox.right - physicalHitbox.left) * 2),
                    physicalHitbox.bottom);
            if(!onGround) { // Airborne
                bmp = rightAirborneBmp;
                rect = new Rect((int)(Constants.HEROAIRBORNESPRITEWIDTH * airFrame), 0, (int)(Constants.HEROAIRBORNESPRITEWIDTH * (airFrame + 1) - 1),
                        (int)Constants.HEROAIRBORNESPRITEHEIGHT - 1);
            } else if(isAttacking) { // Attacking
                bmp = rightForwardAttackBmp;
                rect = new Rect((int)(Constants.HEROFORATKSPRITEWIDTH * attackFrame), 0, (int)(Constants.HEROFORATKSPRITEWIDTH * (attackFrame + 1) - 1),
                        (int)Constants.HEROFORATKSPRITEHEIGHT - 1);
                visualHitbox = new Rect(visualHitbox.left, visualHitbox.top,
                        visualHitbox.left + (int)((visualHitbox.right - visualHitbox.left) * 1.15), visualHitbox.bottom);
                if(attackFrame > 80) {
                    attackHitbox = new Rect(visualHitbox.right - 20, visualHitbox.top, visualHitbox.right, visualHitbox.bottom);
                }
            } else if(startJump) { // Starting Jump
                bmp = rightJumpBmp;
                rect = new Rect((int)(Constants.HEROJUMPSPRITEWIDTH * jumpFrame), 0, (int)(Constants.HEROJUMPSPRITEWIDTH * (jumpFrame + 1) - 1),
                        (int)Constants.HEROJUMPSPRITEHEIGHT - 1);
            } else if(isLanding) { // Landing
                bmp = rightLandingBmp;
                rect = new Rect((int)(Constants.HEROLANDSPRITEWIDTH * landFrame), 0, (int)(Constants.HEROLANDSPRITEWIDTH * (landFrame + 1) - 1),
                        (int)Constants.HEROLANDSPRITEHEIGHT - 1);
            } else if(isRunning) { // Running
                bmp = rightRunBmp;
                rect = new Rect((int)(Constants.HERORUNSPRITEWIDTH * runFrame), 0, (int)(Constants.HERORUNSPRITEWIDTH * (runFrame + 1) - 1),
                        (int)Constants.HERORUNSPRITEHEIGHT - 1);
                visualHitbox = new Rect(visualHitbox.left, visualHitbox.bottom - (int)((visualHitbox.bottom - visualHitbox.top) * 1.3),
                        visualHitbox.left + (int)((visualHitbox.right - visualHitbox.left) * 0.8), visualHitbox.bottom);
            } else if(isWalking) { // Walking
                bmp = rightBmp;
                rect = new Rect((int)(Constants.HEROWALKSPRITEWIDTH * frame), 0, (int)(Constants.HEROWALKSPRITEWIDTH * (frame + 1) - 1),
                        (int)Constants.HEROWALKSPRITEHEIGHT - 1);
            } else { // Idle
                bmp = rightBmp;
                rect = new Rect(0, 0, (int)Constants.HEROWALKSPRITEWIDTH - 1, (int)Constants.HEROWALKSPRITEHEIGHT - 1);
            }
        }
        canvas.drawBitmap(bmp, rect, visualHitbox, null);
        /*
        if(isAttacking)
            canvas.drawBitmap(sample, new Rect(0, 0, sample.getWidth(), sample.getHeight()), attackHitbox, null);
            */
        if(!isAttacking)
            attackHitbox = new Rect(0, 0, 0, 0);
    }

    @Override
    public void update() { // Apply the visual hitbox change, check the state, and update the frame
        // System.out.println("[Hero] Attack Frame = " + attackFrame + ", Attacking = " + isAttacking);
        // System.out.println("[Hero] Air Frame = " + airFrame + ", Airborne = " + !onGround);
        // System.out.println("[Hero] Stamina = " + stamina);
        // System.out.println("[Hero] Stamina CD = " + staminaRestoreCooldown);
        // System.out.println("[Hero] Reserve CD = " + reserveRestoreCooldown);
        if(isFacingLeft) {
            if (!onGround) { // Airborne
                if (airFrame < 1)
                    airFrame = 60;
                else
                    airFrame--;
            } else if (isAttacking) {
                if (attackFrame < 4) {
                    isAttacking = false;
                    attackFrame = 101;
                } else
                    attackFrame -= 3;
            } else if (startJump) {
                jumpFrame -= 4;
            } else if (isLanding) {
                if (landFrame < 3) {
                    landFrame = 44;
                    isLanding = false;
                } else
                    landFrame -= 2;
            } else if (isRunning) {
                if (runFrame < 1)
                    runFrame = 54;
                else
                    runFrame--;
            } else if (isWalking) {
                if (frame < 1)
                    frame = 67;
                else
                    frame--;
            }
        } else {
            if (!onGround) { // Airborne
                if (airFrame > 59)
                    airFrame = 0;
                else
                    airFrame++;
            } else if (isAttacking) { // Attacking
                if (attackFrame > 97) {
                    isAttacking = false;
                    attackFrame = 0;
                } else
                    attackFrame += 3;
            } else if (startJump) { // Starting Jump
                jumpFrame += 4;
            } else if (isLanding) { // Landing
                if (landFrame > 41) {
                    landFrame = 0;
                    isLanding = false;
                } else
                    landFrame += 2;
            } else if (isRunning) { // Running
                if (runFrame > 53)
                    runFrame = 0;
                else
                    runFrame++;
            } else if (isWalking) { // Walking
                if (frame > 66)
                    frame = 0;
                else
                    frame++;
            }
        }
    }

    // Any horizontal movement will change the left and right bounds of the hitbox.
    // Any vertical movement will change the top and bottom bounds of the hitbox.
    public void update(int zone, ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if(reserve == 50 && staminaRestoreCooldown == 0)
            isRecovering = false;
        if(onGround && !isLanding && !isAttacking && !startJump && !((isRecovering && zone == 1) || (isRecovering && zone == 4))) {
            if (zone == 1) { // Sprint Right
                if(!ConsumeStamina(0.4))
                    ConsumeReserve();
                isFacingLeft = false;
                isWalking = true;
                isRunning = true;
                landFrame = 0;
                attackFrame = 0;
                jumpFrame = 0;
                xVelocity = Constants.HEROFASTMOVE;
                HeroMove(obstructables, enemies);
            } else if (zone == 2) { // Walk Right
                isFacingLeft = false;
                isWalking = true;
                isRunning = false;
                landFrame = 0;
                attackFrame = 0;
                jumpFrame = 0;
                xVelocity = Constants.HEROSLOWMOVE;
                HeroMove(obstructables, enemies);
            } else if (zone == 3) { // Walk Left
                isFacingLeft = true;
                isWalking = true;
                isRunning = false;
                landFrame = 44;
                attackFrame = 101;
                jumpFrame = 48;
                xVelocity = -Constants.HEROSLOWMOVE;
                HeroMove(obstructables, enemies);
            } else if (zone == 4) {// Sprint Left
                if(!ConsumeStamina(0.4))
                    ConsumeReserve();
                isFacingLeft = true;
                isWalking = true;
                isRunning = true;
                landFrame = 44;
                attackFrame = 101;
                jumpFrame = 48;
                xVelocity = -Constants.HEROFASTMOVE;
                HeroMove(obstructables, enemies);
            } else {
                isWalking = false;
                isRunning = false;
                xVelocity = 0;
                frame = 0;
            }
        } else if (!onGround){
            switch(zone) {
                case 1:
                    xVelocity = Constants.HEROFASTMOVE;
                    break;
                case 2:
                    xVelocity = Constants.HEROSLOWMOVE;
                    break;
                case 3:
                    xVelocity = -Constants.HEROSLOWMOVE;
                    break;
                case 4:
                    xVelocity = -Constants.HEROFASTMOVE;
                    break;
            }
        } else {
            isWalking = false;
            isRunning = false;
            xVelocity = 0;
            frame = 0;
        }
        if(isAttacking)
            HeroAttack(enemies);
        if(isWalking)
            HeroMove(obstructables, enemies);
        // Fall primarily checks for horizontal Obstructables
        if(!phaseThrough)
            HeroFall(obstructables, enemies);
        HeroJump(obstructables, enemies);
        if(reserveRestoreCooldown == 0) {
            if(reserve + 0.4 >= 50)
                reserve = 50;
            else
                reserve += 0.4;
        } else {
            reserveRestoreCooldown--;
        }
        if (staminaRestoreCooldown == 0) {
            if (stamina + 0.4 >= 100)
                stamina = 100;
            else
                stamina += 0.4;
        } else
            staminaRestoreCooldown--;
        this.update();
    }

    private void HeroFall(ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if (onGround) {
            int collisionCount = 0;
            int tempVelocity = yVelocity + Constants.GRAVITY;
            Rect newHitbox = new Rect(physicalHitbox.left, physicalHitbox.bottom - 10, physicalHitbox.right, physicalHitbox.bottom + tempVelocity);
            for (int i = 0; i < obstructables.size(); i++)
                if (newHitbox.intersect(obstructables.get(i).getHitbox()) && !obstructables.get(i).isNotPhysical) // If the character is over nothing...
                    collisionCount++;
            if(collisionCount == 0) {
                // Then mark them as airborne and have them fall.
                System.out.println("[Hero] Falling!");
                isFalling = true;
                onGround = false;
            }
        }
    }

    private void HeroJump(ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if (startJump && ((!isFacingLeft && jumpFrame > 39) || (isFacingLeft && jumpFrame < 6))) {
            if(!ConsumeStamina(10))
                ConsumeReserve();
            startJump = false;
            if(isFacingLeft)
                jumpFrame = 46;
            else
                jumpFrame = 1;
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
                if (newHitbox.intersect(obstructables.get(i).getHitbox()) && isFalling && !obstructables.get(i).isNotPhysical) {
                    int difference = physicalHitbox.bottom - obstructables.get(i).getHitbox().top;
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
                    airFrame = 0;
                    yVelocity = 0;
                    break;
                }
            }
            if (!onGround || phaseThrough) {
                yVelocity += Constants.GRAVITY;
                HeroMove(obstructables, enemies);
                for(Obstructable obs : obstructables)
                    obs.setHitbox(new Rect(obs.getHitbox().left, obs.getHitbox().top - yVelocity, obs.getHitbox().right,
                            obs.getHitbox().bottom - yVelocity));
                for(Enemy enemy: enemies)
                    enemy.setPhysicalHitbox(new Rect(enemy.physicalHitbox.left, enemy.physicalHitbox.top - yVelocity, enemy.physicalHitbox.right,
                            enemy.physicalHitbox.bottom - yVelocity));
                onGround = false;
                phaseThrough = false;
                if(yVelocity > 0 && !isFalling) {
                    isFalling = true;
                }
            }
        }
    }

    private void HeroMove(ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if(!onGround)
            System.out.println("[Hero Midair] X-Velocity = " + xVelocity);
        if(IsMoveValid(obstructables)) {
            for (Obstructable obs : obstructables)
                obs.setHitbox(new Rect(obs.getHitbox().left - xVelocity, obs.getHitbox().top, obs.getHitbox().right - xVelocity,
                        obs.getHitbox().bottom));
            for (Enemy enemy : enemies)
                enemy.setPhysicalHitbox(new Rect(enemy.physicalHitbox.left - xVelocity, enemy.physicalHitbox.top, enemy.physicalHitbox.right - xVelocity,
                        enemy.physicalHitbox.bottom));
        }
    }

    void HeroAttack(ArrayList<Enemy> enemies) {
        for(Enemy enemy : enemies) {
            if(attackHitbox.intersect(enemy.visualHitbox) && enemy.hitTimer == 0) {
                System.out.println("Enemy was hit!");
                enemy.hitTimer = 12;
                enemy.isBleeding = true;
            }
        }
    }

    void MeleeAttack(int attack) { // Attack 1 is long-range, Attack 2 is short-range
        if(onGround && !isAttacking && !isRecovering) {
            switch(attack) {
                case 1: // Forward Attack
                    isAttacking = true;
                    if(!ConsumeStamina(15))
                        ConsumeReserve();
                    break;
                case 2: // Backward Attack
                    System.out.println("Do a Backward Attack!");
                    if(!ConsumeStamina(15))
                        ConsumeReserve();
                    break;
                default:
            }
        }
    }

    boolean ConsumeStamina(double staminaUsage) {
        staminaRestoreCooldown = 25;
        stamina -= staminaUsage;
        return !(stamina < 0);
    }

    void ConsumeReserve() {
        if(stamina < 0) { // Take the deduction from stamina and place it on reserve
            reserve += stamina;
            stamina = 0;
            reserveRestoreCooldown = 25;
            if(reserve < 0) {// If all reserve is depleted, take away health
                reserve = 0;
                isRecovering = true;
                reserveRestoreCooldown = 50;
            }
        }
    }
}
