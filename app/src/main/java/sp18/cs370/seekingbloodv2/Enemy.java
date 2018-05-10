package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

class Enemy extends Entity implements GameObject {
    // Extra variables can go here if necessary
    Animation bleed;
    Animation clawGround;
    Animation emerge1;
    Animation emerge2;
    Animation stomp;
    Bitmap sample;

    boolean isBleeding;
    boolean isDying2;
    boolean isStomping;
    boolean clawingGround;
    boolean hitHero;
    boolean boss;
    boolean emerging1;
    boolean emerging2;
    boolean finishedEmerging;
    boolean dormant;
    int bossCooldown;
    int timer;

    // Constructor
    Enemy(Rect visualHitbox) {
        this.draw = false;
        this.health = 150.0;
        this.timer = 0;
        this.visualHitbox = visualHitbox;
        this.physicalHitbox = visualHitbox;
        this.attackHitbox = new Rect(0, 0, 0 ,0);
        this.isDying = false;
        this.isFacingLeft = true;
        this.isWalking = false;
        this.isRunning = false;
        this.isFalling = true;
        this.isLanding = false;
        this.onGround = false;
        this.startJump = false;
        this.entityHeight = 0;
        this.xVelocity = 0;
        this.xVelocityInitial = 0;
        this.yVelocity = 0;
        this.yVelocityInitial = Constants.HEROJUMPVELOCITY;
        this.isBleeding = false;
        this.isStomping = false;
        this.boss = false;
        this.emerging1 = false;
        this.emerging2 = false;
        this.finishedEmerging = false;
        this.dormant = true;
        this.bossCooldown = 0;
        this.clawingGround = false;
    }

    Enemy(Enemy enemy) {
        this.idle = new Animation(enemy.idle);
        this.walk = new Animation(enemy.walk);
        this.jumpStart = new Animation(enemy.jumpStart);
        this.airborne = new Animation(enemy.airborne);
        this.land = new Animation(enemy.land);
        this.bleed = new Animation(enemy.bleed);
        this.emerge1 = new Animation(enemy.emerge1);
        this.emerge2 = new Animation(enemy.emerge2);
        this.stomp = new Animation(enemy.stomp);
        this.dying = new Animation(enemy.dying);
        this.draw = enemy.draw;
        this.health = enemy.health;
        this.timer = enemy.timer;
        this.visualHitbox = enemy.physicalHitbox;
        this.physicalHitbox = enemy.physicalHitbox;
        this.isDying = enemy.isDying;
        this.isFacingLeft = enemy.isFacingLeft;
        this.isWalking = enemy.isWalking;
        this.isRunning = enemy.isRunning;
        this.health = enemy.health;
        this.isFalling = enemy.isFalling;
        this.isLanding = enemy.isLanding;
        this.onGround = enemy.onGround;
        this.startJump = enemy.startJump;
        this.entityHeight = enemy.entityHeight;
        this.xVelocity = enemy.xVelocityInitial;
        this.xVelocityInitial = enemy.xVelocityInitial;
        this.yVelocity = enemy.yVelocity;
        this.yVelocityInitial = enemy.yVelocityInitial;
        this.isBleeding = enemy.isBleeding;
        this.isStomping = enemy.isStomping;
        this.emerging1 = enemy.emerging1;
        this.emerging2 = enemy.emerging2;
        this.boss = enemy.boss;
        this.finishedEmerging = enemy.finishedEmerging;
        this.bossCooldown = enemy.bossCooldown;
        this.sample = Bitmap.createBitmap(enemy.sample);
        this.attackHitbox = new Rect(enemy.attackHitbox);
        this.clawingGround = enemy.clawingGround;
    }

    @Override
    public void draw(Canvas canvas) {
        // Update the enemy visually to the screen (Look at the Hero's draw method for an example)
        if(health <= 0 && !isDying) {
            System.out.println("Drawing nothing");
        } else if(isDying) {
            dying.play(visualHitbox, canvas);
        }else {
            if (boss) {
                if (emerging1) {
                    emerge1.play(visualHitbox, canvas);
                } else if (emerging2) {
                    emerge2.play(visualHitbox, canvas);
                } else if (isStomping) {
                    System.out.println("Stomp Frame = " + stomp.frame);
                    stomp.play(visualHitbox, canvas);
                } else if (clawingGround) {
                    clawGround.play(visualHitbox, canvas);
                } else if (isIdle) {
                    idle.play(visualHitbox, canvas);
                } else {
                    emerge1.play(visualHitbox, canvas);
                    emerge1.frame = 1;
                }
            } else {
                if (!onGround)
                    airborne.play(visualHitbox, canvas);
                else if (startJump)
                    airborne.play(visualHitbox, canvas);
                else if (isLanding)
                    land.play(visualHitbox, canvas);
                else if (isWalking)
                    walk.play(visualHitbox, canvas);
                else
                    idle.play(visualHitbox, canvas);
                if (isBleeding)
                    bleed.play(physicalHitbox, canvas);
            }
        }
    }

    @Override
    public void update() {
        if(boss) {
            if (recentlyHitTimer > 0)
                recentlyHitTimer--;
            else
                isHit = false;
            if(emerge1.frame == 2)
                Constants.sounds.play(Constants.soundIndex[10], 1, 1, 1, -1, 1);
            if(emerging2 && (emerge2.frame == emerge2.maxFrame)) {
                emerging2 = false;
                finishedEmerging = true;
                isIdle = true;
            }
            if(isStomping && (stomp.frame == stomp.maxFrame)) {
                isStomping = false;
                isIdle = true;
            }
            if(clawingGround && (clawGround.frame == clawGround.maxFrame)) {
                clawingGround = false;
                isIdle = true;
            }
            if(isDying && (dying.frame == dying.maxFrame)) {
                isDying = false;
                isDying2 = true;
            }

            if(emerging1)
                emerge1.update(this);
            else if(emerging2)
                emerge2.update(this);
            else if(isDying)
                dying.update(this);
            else if(isStomping)
                stomp.update(this);
            else if(clawingGround)
                clawGround.update(this);
            else
                idle.update(this);

            if (!emerging1 && emerge1.active)
                emerge1.reset();
            if (!emerging2 && emerge2.active)
                emerge2.reset();
            if (!isStomping && stomp.active)
                stomp.reset();
            if (!clawingGround && clawGround.active)
                clawGround.reset();

            if(emerging1 && (emerge1.frame == emerge1.maxFrame)) {
                emerging1 = false;
                emerging2 = true;
            }

            if(emerging1 || emerging2)
                visualHitbox = new Rect(physicalHitbox.left, physicalHitbox.top + 800, physicalHitbox.right, physicalHitbox.bottom + 800);
            else if (isDying)
                visualHitbox = new Rect(physicalHitbox.left - 250, physicalHitbox.top + 1600, physicalHitbox.right, physicalHitbox.bottom + 1600);
            else if (isIdle)
                visualHitbox = new Rect(physicalHitbox.left, physicalHitbox.top + 800, physicalHitbox.right, physicalHitbox.bottom);
            else if(isStomping)
                visualHitbox = new Rect(physicalHitbox.left - 150, physicalHitbox.top + 800, physicalHitbox.right, physicalHitbox.bottom);
            else if(clawingGround)
                visualHitbox = new Rect(physicalHitbox.left - 250, physicalHitbox.top + 800, physicalHitbox.right, physicalHitbox.bottom);
        } else {
            if (recentlyHitTimer > 0)
                recentlyHitTimer--;
            else
                isHit = false;

            if (isDying && (dying.frame == dying.maxFrame))
                isDying = false;
            if (isLanding && (land.frame == land.maxFrame))
                isLanding = false;
            if (startJump && (jumpStart.frame == jumpStart.maxFrame))
                startJump = false;
            if (isBleeding && (bleed.frame == bleed.maxFrame))
                isBleeding = false;

            if (!isBleeding && bleed.active)
                bleed.reset();
            if (!startJump && jumpStart.active)
                jumpStart.reset();
            if (!isLanding && land.active)
                land.reset();
            if (!isWalking && walk.active)
                walk.reset();
            if (!isIdle && idle.active)
                idle.reset();

            if (isBleeding)
                bleed.update(this);
            if (isDying) {
                dying.update(this);
            } else if (!onGround) {
                airborne.update(this);
            } else if (startJump) {
                jumpStart.update(this);
            } else if (isLanding) {
                land.update(this);
            } else if (isWalking) {
                walk.update(this);
            } else
                idle.update(this);

            visualHitbox = physicalHitbox;
            if(isDying)
                visualHitbox = new Rect(physicalHitbox.left + 35, physicalHitbox.top + 50, physicalHitbox.right -35, physicalHitbox.bottom + 100);
            else if(isIdle)
                visualHitbox = new Rect(physicalHitbox.left + 35, physicalHitbox.top + 35, physicalHitbox.right -35, physicalHitbox.bottom);
            else if(isLanding || !onGround)
                visualHitbox = new Rect(physicalHitbox.left + 30, physicalHitbox.top, physicalHitbox.right - 30, physicalHitbox.bottom);
        }
    }

    void update(ArrayList<Obstructable> obstructables, Hero hero) {
        if(boss) {
            int distanceToHero = physicalHitbox.left - hero.physicalHitbox.left;
            if ((distanceToHero < Constants.SCREENWIDTH / 3) && !finishedEmerging && (!emerging1 && !emerging2)) {
                emerging1 = true;
            }
            if(emerging1 || emerging2)
                detectCollision(hero);
            if(finishedEmerging) {
                if(bossCooldown == 0 || isStomping || clawingGround) {
                    int attackChoice;
                    if(!isStomping && !clawingGround)
                        attackChoice = new Random(System.nanoTime()).nextInt(2) + 1; // Randomly choose one of 3 special attacks// Check to make sure boss isn't doing some sort of attack
                    else
                        attackChoice = 0;
                    if (attackChoice == 1 || isStomping)
                        stompAttack(hero);
                    else if (attackChoice == 2 || clawingGround)
                        clawGround(hero);
                    cooldownTimer();
                } else {
                    bossCooldown--;
                }
            }
        } else {
            int distanceToHero = physicalHitbox.left - hero.physicalHitbox.left;
            if (distanceToHero < Constants.SCREENWIDTH / 2) {
                if (!isHit && !isLanding) {
                    MoveToPlayer(obstructables, hero);
                    jumpToPlayer(hero);
                    detectFall(obstructables, hero);
                    Fall(obstructables);
                    platformFall(hero);
                    Jump(obstructables);
                } else
                    Knockback(hero, obstructables);
                detectCollision(hero);
            } else {
                isIdle = true;
            }
        }
        Fall(obstructables);
        Jump(obstructables);
        this.update();
    }

    void MoveToPlayer(ArrayList<Obstructable> obstructables, Hero hero) {
        int distanceToHero = physicalHitbox.left - hero.physicalHitbox.left; // If positive, Hero is to the left of the enemy.
        if (distanceToHero > 0) {
            if (distanceToHero > 300) { //If over 300 units away enemy will sprint
                xVelocity = -Constants.ENEMYFASTMOVE;
                isFacingLeft = true;
                isWalking = true;
                Move(obstructables);
            } else {
                xVelocity = -Constants.ENEMYSLOWMOVE;
                isFacingLeft = true;
                isWalking = true;
                Move(obstructables);
            }
        } else {
            if (distanceToHero < -300) { //If over 300 units away enemy will sprint
                xVelocity = Constants.ENEMYFASTMOVE;
                isFacingLeft = false;
                isWalking = true;
                Move(obstructables);
            } else {
                xVelocity = Constants.ENEMYSLOWMOVE;
                isFacingLeft = false;
                isWalking = true;
                Move(obstructables);
            }
        }
    }

    void jumpToPlayer(Hero hero){
        int heightDifference = physicalHitbox.top - hero.physicalHitbox.bottom;
        if(timer == 0) {
            if (heightDifference > 0 && !hero.onGround)
                if (onGround && !startJump) {
                    isIdle = false;
                    startJump = true;
                }
            timer = timer + 2;
        }
        else if(timer > 120)
            timer = 0;
        else
            timer = timer + 2;
    }

    void jumpOnPlatform(ArrayList<Obstructable> obstructables, Hero hero){
        int heightDifference = physicalHitbox.top - hero.physicalHitbox.bottom;
        if(heightDifference > 0) {
            for (int i = 0; i < obstructables.size(); i++){
                Rect platformSearch = new Rect(physicalHitbox.left, physicalHitbox.top + 50, physicalHitbox.right, physicalHitbox.bottom);
                if(Rect.intersects(platformSearch, obstructables.get(i).physicalHitbox) && !obstructables.get(i).isNotPhysical && !startJump) {
                    startJump = true;
                    isIdle = false;
                }
            }
        }
    }

    void detectObstruction(ArrayList<Obstructable> obstructables){
        for (int i = 0; i < obstructables.size(); i++){
            Rect obstructionSearch = new Rect(physicalHitbox.left + 10, 0, physicalHitbox.right + 10, 0);
            if(Rect.intersects(obstructionSearch, obstructables.get(i).physicalHitbox) && !obstructables.get(i).isNotPhysical)
                if(onGround && !startJump) {
                    isIdle = false;
                    startJump = true;
                }
        }
    }

    void detectFall(ArrayList<Obstructable> obstructables, Hero hero) {
        int collisionCount = 0;
        int heightDifference = physicalHitbox.top - hero.physicalHitbox.bottom;
        if(heightDifference > 0) {
            for (int i = 0; i < obstructables.size(); i++) {
                Rect fallSearch = new Rect(physicalHitbox.left + xVelocity, physicalHitbox.top, physicalHitbox.right + xVelocity, physicalHitbox.bottom + 50);
                if (Rect.intersects(fallSearch, obstructables.get(i).physicalHitbox) && !obstructables.get(i).isNotPhysical) // If the character is over nothing...
                    collisionCount++;
            }
            if(collisionCount == 0) {
                startJump = true;
            }
        }
    }

    void detectCollision(Hero hero){
        if(Rect.intersects(physicalHitbox, hero.physicalHitbox) && (hero.recentlyHitTimer == 0) && !hero.isDashing && health > 0) {
            Constants.sounds.play(Constants.soundIndex[5], 1, 1, 1, 0, 1);
            hero.recentlyHitTimer = 75;
            hero.isHit = true;
            hero.isWalking = false;
            hero.isRunning = false;
            hero.health -= 10;
            if(hero.health <= 0)
                hero.isDying = true;
            hitHero = true;
        }
    }

    void platformFall(Hero hero){
        int heightDifference = hero.physicalHitbox.top - physicalHitbox.bottom;
        if(heightDifference > 0 && onGround){
            physicalHitbox = new Rect(physicalHitbox.left, physicalHitbox.top - 1, physicalHitbox.right, physicalHitbox.bottom + 1); //Force the hitbox down by 1 unit
            visualHitbox = physicalHitbox; //Update visual hitbox
            onGround = false;
            isFalling = true;
        }
    }

    void stompAttack(Hero hero){ // Previously groundPound, slightly modified.
        isStomping = true;
        isIdle = false;
        if(stomp.frame == 15)
            Constants.sounds.play(Constants.soundIndex[11], 1, 1, 1, 0, 1);
        if(stomp.frame > 15 && stomp.frame < 20 && (hero.recentlyHitTimer == 0)) {
            Rect groundSearch = new Rect(physicalHitbox.left - Constants.SCREENWIDTH, physicalHitbox.bottom - 20, physicalHitbox.right, physicalHitbox.bottom + 20);
            if (Rect.intersects(groundSearch, hero.physicalHitbox)) {
                Constants.sounds.play(Constants.soundIndex[6], 1, 1, 1, 0, 1);
                hero.recentlyHitTimer = 75;
                hero.health -= 10;
                hero.isHit = true;
                hero.isWalking = false;
                hero.isRunning = false;
                if(hero.health <= 0)
                    hero.isDying = true;
            }
        }
    }

    void clawGround(Hero hero) {
        clawingGround = true;
        isIdle = false;
    }

    void cooldownTimer() {
        Random r = new Random();
        bossCooldown = r.nextInt((420 - 220) + 1) + 220; //Random cooldown time between 420 and 220
    }
}
