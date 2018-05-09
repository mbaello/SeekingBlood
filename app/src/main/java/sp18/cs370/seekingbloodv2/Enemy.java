package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

public class Enemy extends Entity implements GameObject {
    // Extra variables can go here if necessary
    private int timer;
    private int bossInit = 0;
    public ArrayList<Rect> screenZones;

    Bitmap bleedBmp;
    Rect bleedBox;
    private boolean isAttacking;
    boolean isBleeding;
    private boolean isBossRoom; //Set to true when in boss room
    int bleedFrame;
    int bleedTimer;
    int hitTimer;
    int bossCooldown;
    int screenWidth = Constants.SCREENWIDTH;
    int screenHeight = Constants.SCREENHEIGHT;

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
        this.isBossRoom = false;
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
        if(!isBossRoom) { //Regular enemy logic
            MoveToPlayer(obstructables, hero);
            jumpToPlayer(hero);
            detectFall(obstructables, hero);
            Fall(obstructables);
            platformFall(hero);
            Jump(obstructables);
            this.update();
        }
        else{ //Boss logic
            if(bossInit == 0){
                getScreenZones();
                bossInit = 1;
            }
            meleeAttack(hero);
            int attackChoice = new Random().nextInt((3 - 1) + 1) + 1; //Randomly choose one of 3 special attacks
            if(attackChoice == 1)
                groundPound(hero);
            else if(attackChoice == 2)
                rainAttack(screenZones, hero);
            else
                rootAttack(screenZones, hero);
            cooldownTimer(); //Wait before going back into actions
        }
    }

    private void MoveToPlayer(ArrayList<Obstructable> obstructables, Hero hero) {
        int distanceToHero = physicalHitbox.left - hero.physicalHitbox.left; // If positive, Hero is to the left of the enemy.
        int heightDifference = physicalHitbox.top - hero.physicalHitbox.bottom;
        if(distanceToHero > 0) {
            if(distanceToHero > 1000)
                xVelocity = 0;
            else if(distanceToHero > 300){ //If over 300 units away enemy will sprint
                if(heightDifference > 0)
                    if(reachablePlatform(obstructables))
                        startJump = true;
                if(detectObstructionLeft(obstructables) && isOnGround() && !detectCollision(hero) && !startJump)
                    startJump = true;
                if(detectCollision(hero) && onGround)
                    xVelocity = 0;
                else {
                    xVelocity = -Constants.ENEMYFASTMOVE;
                    Move(obstructables);
                }
            }
            else {
                if(heightDifference > 0)
                    if(reachablePlatform(obstructables))
                        startJump = true;
                if(detectObstructionLeft(obstructables) && isOnGround() && !detectCollision(hero) &&!startJump)
                    startJump = true;
                if(detectCollision(hero) && onGround)
                    xVelocity = 0;
                else {
                    xVelocity = -Constants.ENEMYSLOWMOVE;
                    Move(obstructables);
                }
            }
        } else {
            if(distanceToHero < -1000)
                xVelocity = 0;
            else if(distanceToHero < -300){ //If over 300 units away enemy will sprint
                if(heightDifference > 0)
                    if(reachablePlatform(obstructables))
                        startJump = true;
                if(detectObstructionRight(obstructables) && isOnGround() && !startJump && !detectCollision(hero))
                    startJump = true;
                if(detectCollision(hero) && onGround)
                    xVelocity = 0;
                else {
                    xVelocity = Constants.ENEMYFASTMOVE;
                    Move(obstructables);
                }
            }
            else {
                if(heightDifference > 0)
                    if(reachablePlatform(obstructables))
                        startJump = true;
                if(detectObstructionRight(obstructables) && isOnGround() && !startJump && !detectCollision(hero))
                    startJump = true;
                if(detectCollision(hero) && onGround)
                    xVelocity = 0;
                else {
                    xVelocity = Constants.ENEMYSLOWMOVE;
                    Move(obstructables);
                }
            }
        }
    }

    private void jumpToPlayer(Hero hero){
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

    private boolean detectObstructionLeft(ArrayList<Obstructable> obstructables){ //Looks for any obstacles to the left, return true if there exists an obstacle
        Rect obstructionSearchLeft = new Rect(physicalHitbox.left + xVelocity, 0, physicalHitbox.right, 0);
        for (int i = 0; i < obstructables.size(); i++)
            if(Rect.intersects(obstructionSearchLeft, obstructables.get(i).getHitbox()) && !obstructables.get(i).isNotPhysical)
                return true;
        return false;
    }

    private boolean detectObstructionRight(ArrayList<Obstructable> obstructables){ //Looks for any obstacles to the right, return true if there exists an obstacle
        Rect obstructionSearchRight = new Rect(physicalHitbox.left, 0, physicalHitbox.right + xVelocity, 0);
        for (int i = 0; i < obstructables.size(); i++)
            if(Rect.intersects(obstructionSearchRight, obstructables.get(i).getHitbox()) && !obstructables.get(i).isNotPhysical)
                return true;
        return false;
    }

    private boolean upcomingFall(ArrayList<Obstructable> obstructables){
        Rect tempHitbox = new Rect(physicalHitbox.left + xVelocity, physicalHitbox.top, physicalHitbox.right + xVelocity, physicalHitbox.bottom + 75);
        for(int i = 0; i < obstructables.size(); i++)
            if (Rect.intersects(tempHitbox, obstructables.get(i).getHitbox()) && !obstructables.get(i).isNotPhysical)
                return false; //Return false if platform detected under tempHitbox
        return true; //Return that fall is upcoming
    }

    private void detectFall(ArrayList<Obstructable> obstructables, Hero hero) {
        int heightDifference = physicalHitbox.top - hero.physicalHitbox.bottom;
        if(heightDifference > 0)
            if (upcomingFall(obstructables) && onGround)
                startJump = true;
    }

    private boolean detectCollision(Hero hero){ //Detect if enemy collides with hero
        if(Rect.intersects(physicalHitbox, hero.physicalHitbox))
            return true;
        else
            return false;
    }

    private void platformFall(Hero hero){
        int heightDifference = hero.physicalHitbox.top - physicalHitbox.bottom;
        if(heightDifference > 0 && isOnGround()){
            physicalHitbox = new Rect(physicalHitbox.left, physicalHitbox.top - 1, physicalHitbox.right, physicalHitbox.bottom + 1); //Force the hitbox down by 1 unit
            visualHitbox = physicalHitbox; //Update visual hitbot
            onGround = false;
            isFalling = true;
        }
    }

    private boolean reachablePlatform(ArrayList<Obstructable> obstructables){ //Check if a platform above is reachable. If reachable return true, otherwise false
        Rect detectPlatform = new Rect(physicalHitbox.left, physicalHitbox.top + 200, physicalHitbox.right, physicalHitbox.bottom); //Check 200 units above
        for(int i = 0; i < obstructables.size(); i++)
            if(Rect.intersects(detectPlatform, obstructables.get(i).getHitbox()) && !obstructables.get(i).isNotPhysical)
                return true;
        return false;
    }

    /*
    Boss Functions Below
     */

    private void meleeAttack(Hero hero){
        int distanceToHero = physicalHitbox.left - hero.physicalHitbox.left; //If positive, hero is to the left of the enemy
        if(distanceToHero <= 200 && distanceToHero > 0) { //Attack left side
            isAttacking = true;
            hero.health -= 25;
        }
        else if(distanceToHero >= -200 && distanceToHero < 0){ //Attack right side
            isAttacking = true;
            hero.health -= 25;
        }
        else
            isAttacking = false;
    }

    private void cooldownTimer(){
        Random r = new Random();
        bossCooldown = r.nextInt((420 - 220) + 1) + 220; //Random cooldown time between 420 and 220
        timer = 0;
        while(bossCooldown > 0){ //Cooldown period
            if(timer >= bossCooldown)
                bossCooldown = 0;
            else timer++;
        }
    }

    private void groundPound(Hero hero){
        Rect groundSearch = new Rect(physicalHitbox.left + screenWidth, physicalHitbox.bottom + 10, physicalHitbox.right, physicalHitbox.bottom);
        if(Rect.intersects(groundSearch, hero.physicalHitbox))
            hero.health -= 20;
        groundSearch.setEmpty();
    }

    private void getScreenZones(){ //Set zones on screen for rain or zone attacks. One time initialization.
        screenZones = new ArrayList<>();
        Rect zone1 = new Rect(0, screenHeight, screenWidth / 5, 0); //Initialize the zones
        Rect zone2 = new Rect(screenWidth / 5, screenHeight, (screenWidth / 5) * 2, 0);
        Rect zone3 = new Rect((screenWidth / 5) * 2, screenHeight, (screenWidth / 5) * 3, 0);
        Rect zone4 = new Rect((screenWidth / 5) * 3, screenHeight, (screenWidth / 5) * 4, 0);
        Rect zone5 = new Rect((screenWidth / 5) * 4, screenHeight, screenWidth, 0);
        screenZones.add(zone1); //Add zones to the ArrayList
        screenZones.add(zone2);
        screenZones.add(zone3);
        screenZones.add(zone4);
        screenZones.add(zone5);
    }

    private void rainAttack(ArrayList<Rect> screenZones, Hero hero){
        int rainZone = new Random().nextInt(screenZones.size());
        Rect tempZone = screenZones.get(rainZone);
        timer = 0;
        while(timer < 200) //Set timer to 200 before attack goes off
            timer++;
        if(Rect.intersects(tempZone, hero.physicalHitbox))
            hero.health -= 30;
        tempZone.setEmpty();
    }

    private void rootAttack(ArrayList<Rect> screenZones, Hero hero){
        int rootZone = new Random().nextInt(screenZones.size());
        Rect tempZone = screenZones.get(rootZone);
        timer = 0;
        while(timer < 200) //Set timer to 200 before attack goes off
            timer++;
        if(Rect.intersects(tempZone, hero.physicalHitbox))
            hero.health -= 30;
        tempZone.setEmpty();
    }
}
