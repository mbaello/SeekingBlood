package sp18.cs370.seekingbloodv2;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

class Hero extends Entity implements GameObject {
    Animation run;
    Animation fAttack;
    Animation bAttack;
    Animation hit;
    Animation dash;
    boolean isAttacking;
    boolean isRecovering;
    boolean phaseThrough;
    boolean isDashing;
    double stamina;
    double staminaRestoreCooldown;
    double reserve;
    double reserveRestoreCooldown;
    int attackType;
    // Bitmap sample;

    Hero(Rect visualHitbox) {
        this.attackHitbox = new Rect(0, 0, 0, 0);
        this.isIdle = false;
        this.isAttacking = false;
        this.isLanding = false;
        this.isRecovering = false;
        this.isHit = false;
        this.phaseThrough = false;
        this.visualHitbox = visualHitbox;
        this.physicalHitbox = new Rect(visualHitbox.left, visualHitbox.top, visualHitbox.left + ((visualHitbox.right - visualHitbox.left) / 2), visualHitbox.bottom);
        this.isFacingLeft = false;
        this.isWalking = false;
        this.isRunning = false;
        this.draw = true;
        this.health = 100.0;
        this.reserve = 50.0;
        this.stamina = 100.0;
        this.isFalling = true;
        this.onGround = false;
        this.startJump = false;
        this.entityHeight = 0;
        this.xVelocity = 0;
        this.xVelocityInitial = 0;
        this.yVelocity = 0;
        this.yVelocityInitial = Constants.HEROJUMPVELOCITY;
        this.attackType = 0;
        this.recentlyHitTimer = 0;
        this.isDying = false;
    }

    Hero(Hero hero) {
        this.idle = hero.idle;
        this.walk = hero.walk;
        this.run = hero.run;
        this.jumpStart = hero.jumpStart;
        this.airborne = hero.airborne;
        this.land = hero.land;
        this.fAttack = hero.fAttack;
        this.bAttack = hero.bAttack;
        this.hit = hero.hit;
        this.dash = hero.dash;
        this.dying = hero.dying;
        this.attackHitbox = hero.attackHitbox;
        this.isDying = hero.isDying;
        this.isIdle = hero.isIdle;
        this.isAttacking = hero.isAttacking;
        this.isLanding = hero.isLanding;
        this.isRecovering = hero.isRecovering;
        this.isHit = hero.isHit;
        this.phaseThrough = hero.phaseThrough;
        this.visualHitbox = hero.visualHitbox;
        this.physicalHitbox = hero.physicalHitbox;
        this.isFacingLeft = hero.isFacingLeft;
        this.isWalking = hero.isWalking;
        this.isRunning = hero.isRunning;
        this.draw = hero.draw;
        this.health = hero.health;
        this.reserve = hero.reserve;
        this.stamina = hero.stamina;
        this.isFalling = hero.isFalling;
        this.onGround = hero.onGround;
        this.startJump = hero.startJump;
        this.entityHeight = hero.entityHeight;
        this.xVelocity = hero.xVelocity;
        this.xVelocityInitial = hero.xVelocityInitial;
        this.yVelocity = hero.yVelocity;
        this.yVelocityInitial = hero.yVelocityInitial;
        this.recentlyHitTimer = hero.recentlyHitTimer;
    }

    // Overridden GameObject methods

    @Override
    public void draw(Canvas canvas) { // Play Animations
        if(health <= 0 && !isDying) {
            System.out.println("Drawing nothing");
        } else if(isDying)
            dying.play(visualHitbox, canvas);
        else if (isHit) {
            hit.play(visualHitbox, canvas);
        } else if (isAttacking && attackType == 1) {
            fAttack.play(visualHitbox, canvas);
        } else if (isAttacking && attackType == 2) {
            bAttack.play(visualHitbox, canvas);
        } else if (!onGround) {
            airborne.play(visualHitbox, canvas);
        } else if (startJump) {
            jumpStart.play(visualHitbox, canvas);
        } else if (isLanding) {
            land.play(visualHitbox, canvas);
        } else if (isWalking) {
            walk.play(visualHitbox, canvas);
        } else if (isRunning) {
            run.play(visualHitbox, canvas);
        } else if (isDashing) {
            dash.play(visualHitbox, canvas);
        } else {
            idle.play(visualHitbox, canvas);
        }
        // canvas.drawBitmap(sample, new Rect(0, 0, sample.getWidth(), sample.getHeight()), attackHitbox, null);
        /*
        // Facing Left
        visualHitbox = new Rect(physicalHitbox.right - ((physicalHitbox.right - physicalHitbox.left) * 2), physicalHitbox.top, physicalHitbox.right,
                physicalHitbox.bottom);
        // Left Attack
        visualHitbox = new Rect(visualHitbox.right - (int)((visualHitbox.right - visualHitbox.left) * 1.15), visualHitbox.top,
                visualHitbox.right, visualHitbox.bottom);
        // Left Run
        visualHitbox = new Rect(visualHitbox.right - (int)((visualHitbox.right - visualHitbox.left) * 0.8), visualHitbox.bottom - (int)((visualHitbox.bottom - visualHitbox.top) * 1.3),
                visualHitbox.right, visualHitbox.bottom);
        // Facing Right
        visualHitbox = new Rect(physicalHitbox.left, physicalHitbox.top, physicalHitbox.left + ((physicalHitbox.right - physicalHitbox.left) * 2),
                physicalHitbox.bottom);
        // Right Attack
        visualHitbox = new Rect(visualHitbox.left, visualHitbox.top,
                visualHitbox.left + (int)((visualHitbox.right - visualHitbox.left) * 1.15), visualHitbox.bottom);
        // Right Run
        visualHitbox = new Rect(visualHitbox.left, visualHitbox.bottom - (int)((visualHitbox.bottom - visualHitbox.top) * 1.3),
                visualHitbox.left + (int)((visualHitbox.right - visualHitbox.left) * 0.8), visualHitbox.bottom);
        // Draw Attacking Hitbox
            canvas.drawBitmap(sample, new Rect(0, 0, sample.getWidth(), sample.getHeight()), attackHitbox, null);
    */
    }

    @Override
    public void update() { // Checks the state, updates animations, and applies appropriate hitboxes.
        // Play sounds at certain frames
        if (fAttack.frame == 25)
            Constants.sounds.play(Constants.soundIndex[1], 1, 1, 1, 0, 1);
        if ((walk.active && (walk.frame == 1 || walk.frame == 35)) || (run.active && (run.frame == 1 || run.frame == 25)))
            Constants.sounds.play(Constants.soundIndex[0], 1, 1, 1, 0, 1);
        // One-time animations need to be checked so that they don't loop
        if (isLanding && (land.frame == land.maxFrame))
            isLanding = false;
        if (!isAttacking && (fAttack.active || bAttack.active)) {
            attackHitbox = new Rect(0, 0, 0, 0);
            fAttack.reset();
            bAttack.reset();
        }
        if (isHit && (hit.frame == hit.maxFrame))
            isHit = false;
        if (isDashing && (dash.frame == dash.maxFrame)) {
            isDashing = false;
            xVelocity = 0;
        }
        if (isDying && (dying.frame == dying.maxFrame)) {
            isDying = false;
        }

        if (!isDashing && dash.active)
            dash.reset();
        if (!startJump && jumpStart.active)
            jumpStart.reset();
        if (!isLanding && land.active)
            land.reset();
        if (!isRunning && run.active)
            run.reset();
        if (!isWalking && walk.active)
            walk.reset();
        if (!isIdle && idle.active)
            idle.reset();
        if (!isHit && hit.active)
            hit.reset();

        if(isDying) {
            dying.update(this);
        } else if (isHit) {
            hit.update(this);
            isAttacking = false;
            startJump = false;
            isLanding = false;
            isWalking = false;
            isRunning = false;
            jumpStart.reset();
            land.reset();
            run.reset();
            walk.reset();
            idle.reset();
        } else
        if (isDashing) {
            dash.update(this);
        } else if (startJump) {
            jumpStart.update(this);
        } else if (isLanding) {
            land.update(this);
        } else if (isWalking) {
            walk.update(this);
        } else if (isRunning) {
            run.update(this);
        } else if (!onGround) {
            airborne.update(this);
        } else
            idle.update(this);

        // Adjust hitboxes
        if (isFacingLeft) {
            visualHitbox = new Rect(physicalHitbox.right - ((physicalHitbox.right - physicalHitbox.left) * 2), physicalHitbox.top, physicalHitbox.right,
                    physicalHitbox.bottom);
            if (isAttacking && !isHit)
                if ((attackType == 1) && (fAttack.frame != fAttack.maxFrame)) {
                    visualHitbox = new Rect(visualHitbox.right - (int) ((visualHitbox.right - visualHitbox.left) * 1.15), visualHitbox.top, visualHitbox.right, visualHitbox.bottom);
                    if (fAttack.frame > 25) {
                        attackHitbox = new Rect(visualHitbox.left, visualHitbox.top + 60, visualHitbox.left + 20, visualHitbox.top + 80);
                    }
                    fAttack.update(this);
                } else if ((attackType == 2) && (bAttack.frame != bAttack.maxFrame)) {
                    visualHitbox = new Rect(visualHitbox.right - (int) ((visualHitbox.right - visualHitbox.left) * 1.1), visualHitbox.bottom - (int) ((visualHitbox.bottom - visualHitbox.top) * 1.4),
                            visualHitbox.right, visualHitbox.bottom);
                    if (bAttack.frame > 10)
                        attackHitbox = new Rect(physicalHitbox.left - 30, visualHitbox.top + ((visualHitbox.bottom - visualHitbox.top) / 2),
                                physicalHitbox.left, visualHitbox.bottom);
                    bAttack.update(this);
                } else {
                    isAttacking = false;
                }
            else if (isRunning && !isLanding && onGround && !isHit) {
                visualHitbox = new Rect(visualHitbox.right - (int) ((visualHitbox.right - visualHitbox.left) * 0.8), visualHitbox.bottom - (int) ((visualHitbox.bottom - visualHitbox.top) * 1.3),
                        visualHitbox.right, visualHitbox.bottom);
            }
        } else {
            visualHitbox = new Rect(physicalHitbox.left, physicalHitbox.top, physicalHitbox.left + ((physicalHitbox.right - physicalHitbox.left) * 2),
                    physicalHitbox.bottom);
            if (isAttacking && !isHit)
                if ((attackType == 1) && (fAttack.frame != fAttack.maxFrame)) {
                    visualHitbox = new Rect(visualHitbox.left, visualHitbox.top, visualHitbox.left + (int) ((visualHitbox.right - visualHitbox.left) * 1.15), visualHitbox.bottom);
                    if (fAttack.frame > 25) {
                        attackHitbox = new Rect(visualHitbox.right - 20, visualHitbox.top + 60, visualHitbox.right, visualHitbox.top + 80);
                    }
                    fAttack.update(this);
                } else if ((attackType == 2) && (bAttack.frame != bAttack.maxFrame)) {
                    visualHitbox = new Rect(visualHitbox.left, visualHitbox.bottom - (int) ((visualHitbox.bottom - visualHitbox.top) * 1.4),
                            visualHitbox.left + (int) ((visualHitbox.right - visualHitbox.left) * 1.1), visualHitbox.bottom);
                    if (bAttack.frame > 10)
                        attackHitbox = new Rect(physicalHitbox.right, visualHitbox.top + ((visualHitbox.bottom - visualHitbox.top) / 2),
                                physicalHitbox.right + 30, visualHitbox.bottom);
                    bAttack.update(this);
                } else {
                    isAttacking = false;
                }
            else if (isRunning && !isLanding && onGround && !isHit)
                visualHitbox = new Rect(visualHitbox.left, visualHitbox.bottom - (int) ((visualHitbox.bottom - visualHitbox.top) * 1.3),
                        visualHitbox.left + (int) ((visualHitbox.right - visualHitbox.left) * 0.8), visualHitbox.bottom);
        }
    }

    public void update(int zone, ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if(reserve == 50 && staminaRestoreCooldown == 0)
            isRecovering = false;
        if(onGround && !isLanding && !isAttacking && !startJump && !isHit && !phaseThrough && !isDashing) {
            if (zone == 1) { // Sprint Right
                if(!isRecovering) {
                    if (!ConsumeStamina(0.5))
                        ConsumeReserve();
                    isFacingLeft = false;
                    isWalking = false;
                    isRunning = true;
                    xVelocity = Constants.HEROFASTMOVE;
                    HeroMove(obstructables, enemies);
                } else {
                    isFacingLeft = false;
                    isWalking = true;
                    isRunning = false;
                    xVelocity = Constants.HEROSLOWMOVE;
                    HeroMove(obstructables, enemies);
                }
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
                xVelocity = -Constants.HEROSLOWMOVE;
                HeroMove(obstructables, enemies);
            } else if (zone == 4) { // Sprint Left
                if(!isRecovering) {
                    if (!ConsumeStamina(0.5))
                        ConsumeReserve();
                    isFacingLeft = true;
                    isWalking = false;
                    isRunning = true;
                    xVelocity = -Constants.HEROFASTMOVE;
                    HeroMove(obstructables, enemies);
                } else {
                    isFacingLeft = true;
                    isWalking = true;
                    isRunning = false;
                    xVelocity = -Constants.HEROSLOWMOVE;
                    HeroMove(obstructables, enemies);
                }
            } else {
                isWalking = false;
                isRunning = false;
                isIdle = true;
            }
        } else if (!onGround){
            switch(zone) {
                case 1:
                    xVelocity = Constants.HEROFASTMOVE * 2;
                    break;
                case 2:
                    xVelocity = Constants.HEROSLOWMOVE * 2;
                    break;
                case 3:
                    xVelocity = -Constants.HEROSLOWMOVE * 2;
                    break;
                case 4:
                    xVelocity = -Constants.HEROFASTMOVE * 2;
                    break;
            }
        } else {
            isWalking = false;
            isRunning = false;
            isIdle = true;
            if(!isDashing & !isHit)
                xVelocity = 0;
        }

        if(isAttacking)
            HeroAttack(enemies);
        if(isWalking || isRunning || isDashing && !isHit)
            HeroMove(obstructables, enemies);
        if(!phaseThrough)
            HeroFall(obstructables);
        HeroJump(obstructables, enemies);
        if(isHit)
            HeroKnockback(obstructables, enemies);

        if(recentlyHitTimer > 0)
            recentlyHitTimer--;
        if(reserveRestoreCooldown == 0) {
            if(reserve + 0.5 >= 50)
                reserve = 50;
            else
                reserve += 0.5;
        } else {
            reserveRestoreCooldown--;
        }
        if (staminaRestoreCooldown == 0) {
            if (stamina + 0.5 >= 100)
                stamina = 100;
            else
                stamina += 0.5;
        } else
            staminaRestoreCooldown--;
        this.update();
    }

    private void HeroFall(ArrayList<Obstructable> obstructables) {
        if (onGround) {
            int collisionCount = 0;
            int tempVelocity = yVelocity + Constants.GRAVITY;
            Rect newHitbox = new Rect(physicalHitbox.left, physicalHitbox.bottom, physicalHitbox.right, physicalHitbox.bottom + tempVelocity);
            for (int i = 0; i < obstructables.size(); i++)
                if(Rect.intersects(newHitbox, obstructables.get(i).physicalHitbox) && !obstructables.get(i).isNotPhysical) // Check if the character is over nothing
                    collisionCount++; // Increment if the character is over a platform
            if(collisionCount == 0) {
                // If the character is over nothing, have them fall.
                isFalling = true;
                onGround = false;
            }
        }
    }

    private void HeroJump(ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if (startJump && (jumpStart.frame == 12)) {
            if (!ConsumeStamina(10))
                ConsumeReserve();
            startJump = false;
            yVelocity = yVelocityInitial;
            // Instead of updating the character, update the screen.
            HeroMove(obstructables, enemies);
            isWalking = false;
            isRunning = false;
            for (Obstructable obs : obstructables) {
                obs.physicalHitbox = new Rect(obs.physicalHitbox.left, obs.physicalHitbox.top - yVelocity, obs.physicalHitbox.right, obs.physicalHitbox.bottom - yVelocity);
                obs.visualHitbox = new Rect(obs.visualHitbox.left, obs.visualHitbox.top - yVelocity, obs.visualHitbox.right, obs.visualHitbox.bottom - yVelocity);
            }
            for (Enemy enemy : enemies)
                enemy.physicalHitbox = new Rect(enemy.physicalHitbox.left, enemy.physicalHitbox.top - yVelocity, enemy.physicalHitbox.right,
                        enemy.physicalHitbox.bottom - yVelocity);
            onGround = false;
        } else if (!onGround) {
            for (int i = 0; i < obstructables.size(); i++) {
                Rect newHitbox = new Rect(physicalHitbox.left, physicalHitbox.bottom, physicalHitbox.right, physicalHitbox.bottom + yVelocity + 1);
                if (Rect.intersects(newHitbox, obstructables.get(i).physicalHitbox) && isFalling && !obstructables.get(i).isNotPhysical) {
                    int difference = physicalHitbox.bottom - obstructables.get(i).physicalHitbox.top;
                    HeroMove(obstructables, enemies);
                    for (Obstructable obs : obstructables) {
                        obs.physicalHitbox = (new Rect(obs.physicalHitbox.left, obs.physicalHitbox.top + difference, obs.physicalHitbox.right, obs.physicalHitbox.bottom + difference));
                        obs.visualHitbox = (new Rect(obs.visualHitbox.left, obs.visualHitbox.top + difference, obs.visualHitbox.right, obs.visualHitbox.bottom + difference));
                    }
                    for (Enemy enemy : enemies)
                        enemy.physicalHitbox = new Rect(enemy.physicalHitbox.left, enemy.physicalHitbox.top + difference, enemy.physicalHitbox.right,
                                enemy.physicalHitbox.bottom + difference);
                    onGround = true;
                    isFalling = false;
                    isLanding = true;
                    isWalking = false;
                    isRunning = false;
                    yVelocity = 0;
                    break;
                }
            }
            if (!onGround || phaseThrough) {
                yVelocity += Constants.GRAVITY;
                HeroMove(obstructables, enemies);
                for (Obstructable obs : obstructables) {
                    obs.physicalHitbox = (new Rect(obs.physicalHitbox.left, obs.physicalHitbox.top - yVelocity, obs.physicalHitbox.right, obs.physicalHitbox.bottom - yVelocity));
                    obs.visualHitbox = new Rect(obs.visualHitbox.left, obs.visualHitbox.top - yVelocity, obs.visualHitbox.right, obs.visualHitbox.bottom - yVelocity);
                }
                for (Enemy enemy : enemies)
                    enemy.physicalHitbox = new Rect(enemy.physicalHitbox.left, enemy.physicalHitbox.top - yVelocity, enemy.physicalHitbox.right,
                            enemy.physicalHitbox.bottom - yVelocity);
                onGround = false;
                phaseThrough = false;
                isWalking = false;
                isRunning = false;
                if (yVelocity > 0 && !isFalling) {
                    isFalling = true;
                }
            }
        }
    }

    private void HeroMove(ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if(IsMoveValid(obstructables)) {
            for (Obstructable obs : obstructables) {
                obs.physicalHitbox = new Rect(obs.physicalHitbox.left - xVelocity, obs.physicalHitbox.top, obs.physicalHitbox.right - xVelocity, obs.physicalHitbox.bottom);
                obs.visualHitbox = new Rect(obs.visualHitbox.left - xVelocity, obs.visualHitbox.top, obs.visualHitbox.right - xVelocity, obs.visualHitbox.bottom);
            }
            for (Enemy enemy : enemies) {
                enemy.physicalHitbox = new Rect(enemy.physicalHitbox.left - xVelocity, enemy.physicalHitbox.top, enemy.physicalHitbox.right - xVelocity,
                        enemy.physicalHitbox.bottom);
            }
        }
    }

    void HeroAttack(ArrayList<Enemy> enemies) {
        for(Enemy enemy : enemies) {
            if(Rect.intersects(attackHitbox, enemy.visualHitbox) && (enemy.recentlyHitTimer == 0) && !enemy.isDying) {
                enemy.health -= 50;
                enemy.recentlyHitTimer = 20;
                enemy.isBleeding = true;
                enemy.isHit = true;
                if(!enemy.boss) {
                    if (enemy.health > 0)
                        Constants.sounds.play(Constants.soundIndex[3], 1, 1, 1, 0, 1);
                    else {
                        enemy.isDying = true;
                        Constants.sounds.play(Constants.soundIndex[4], 1, 1, 1, 0, 1);
                    }
                } else { // Boss hit
                    if (enemy.health > 0) {
                        Constants.sounds.play(Constants.soundIndex[7], 1, 1, 1, 0, 1);
                    } else {
                        Constants.sounds.play(Constants.soundIndex[8], 1, 1, 1, 0, 1);
                        enemy.isDying = true;
                    }
                }
            }
        }
    }

    void RegisterAttack(int attack) { // Attack 1 is long-range, Attack 2 is short-range
        if(!isAttacking && !isRecovering && !isHit) {
            int consume;
            isAttacking = true;
            attackType = attack;
            if(attackType == 1)
                consume = 15;
            else
                consume = 5;
            if(!ConsumeStamina(consume))
                ConsumeReserve();
        }
    }

    void HeroKnockback(ArrayList<Obstructable> obstructables, ArrayList<Enemy> enemies) {
        if(hit.frame < 15)
            for (int i = 0; i < enemies.size(); i++) {
                System.out.println("[HERO] Hero was knocked back " + xVelocity + "!");
                if (enemies.get(i).hitHero) {
                    if (enemies.get(i).physicalHitbox.right - physicalHitbox.right > 0) {
                        xVelocity = -Constants.KNOCKBACKVELOCITY;
                        HeroMove(obstructables, enemies);
                    } else {
                        xVelocity = Constants.KNOCKBACKVELOCITY;
                        HeroMove(obstructables, enemies);
                    }
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
            reserveRestoreCooldown = 20;
            if(reserve < 0) { // If all reserve is depleted, take away health
                health += reserve / 4;
                reserve = 0;
                isRecovering = true;
                reserveRestoreCooldown = 40;
            }
        }
    }
}
