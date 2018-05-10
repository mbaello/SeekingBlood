package sp18.cs370.seekingbloodv2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

public class Game extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener {
    GameThread gameThread;
    GestureDetector gestureDetector;
    Hero hero;
    Rect fastMoveZoneR;
    Rect slowMoveZoneR;
    Rect slowMoveZoneL;
    Rect fastMoveZoneL;
    Rect menuZone;
    Rect actionZone;
    int entityHeight;
    int primaryZone;
    int secondaryTouchX;
    int secondaryTouchY;
    Animation tempAnimation;
    HUDElement tempElement;
    Rect tempRect1; // Visual hitbox
    Rect tempRect2; // Physical hitbox
    Rect screen; // Rect that represents the visible screen
    ArrayList<Obstructable> obstructables;
    ArrayList<Enemy> enemies;
    ArrayList<HUDElement> elements;

    Hero checkpointHero;
    ArrayList<Obstructable> checkpointObs;
    ArrayList<Enemy> checkpointEnemies;

    public Game(Context context) {
        super(context);
        getHolder().addCallback(this);

        // BitmapFactory options
        final BitmapFactory.Options noScale = new BitmapFactory.Options();
        final BitmapFactory.Options scaleDown2 = new BitmapFactory.Options();
        noScale.inScaled = false;
        scaleDown2.inScaled = false;
        scaleDown2.inSampleSize = 2;

        // Initialize Screen Region
        screen = new Rect(-200, 0, Constants.SCREENWIDTH + 200, Constants.SCREENHEIGHT);

        // Initialize a global height
        entityHeight = ((Constants.SCREENHEIGHT * 8) / 10) - ((Constants.SCREENHEIGHT * 6) / 10);

        Thread heroThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Create the Hero - Has animations for: idling, walking, running, beginning a jump, airborne,
                // landing, and attacking.

                System.out.println("[HERO] Hero Thread started!");
                hero = new Hero(new Rect((Constants.SCREENWIDTH * 2) / 5, ((Constants.SCREENHEIGHT * 6) / 10),
                        Constants.SCREENWIDTH / 2, (Constants.SCREENHEIGHT * 8) / 10));
                System.out.println("[HERO] Initializing Hero location!");
                hero.entityHeight = entityHeight; // Store the Hero's height
                System.out.println("Hero's Height = " + hero.entityHeight);
                // Load the Idle Sprite Sheet
                Bitmap heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_idle_sprites_left, noScale);
                Bitmap heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_idle_sprites_right, noScale);
                hero.idle = new Animation(heroLeftBmp, heroRightBmp, 68);
                // Load the Walking Sprite Sheet
                heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_walking_sprites_left, noScale);
                heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_walking_sprites_right, noScale);
                hero.walk = new Animation(heroLeftBmp, heroRightBmp, 68);
                // Load the Running Sprite Sheet
                heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_running_sprites_left, noScale);
                heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_running_sprites_right, noScale);
                hero.run = new Animation(heroLeftBmp, heroRightBmp, 55);
                // Load the Jump-Start Sprite Sheet
                heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_jump_start_sprites_left, noScale);
                heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_jump_start_sprites_right, noScale);
                hero.jumpStart = new Animation(heroLeftBmp, heroRightBmp, 24);
                // Load the Airborne Sprite Sheet
                heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_airborne_sprites_left, noScale);
                heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_airborne_sprites_right, noScale);
                hero.airborne = new Animation(heroLeftBmp, heroRightBmp, 61);
                // Load the Landing Sprite Sheet
                heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_landing_sprites_left, noScale);
                heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_landing_sprites_right, noScale);
                hero.land = new Animation(heroLeftBmp, heroRightBmp, 23);
                // Load the Forward Attack Sprite Sheet
                heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_forward_attack_sprites_left, noScale);
                heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_forward_attack_sprites_right, noScale);
                hero.fAttack = new Animation(heroLeftBmp, heroRightBmp, 35);
                // Load the Backward Attack Sprite Sheet
                heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_backward_attack_sprites_left, noScale);
                heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_backward_attack_sprites_right, noScale);
                hero.bAttack = new Animation(heroLeftBmp, heroRightBmp, 20);
                // Load the Received Hit Sprite Sheet
                heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_received_hit_sprites_left, noScale);
                heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_received_hit_sprites_right, noScale);
                hero.hit = new Animation(heroLeftBmp, heroRightBmp, 35);
                // Load the Dashing Sprite Sheet
                heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.flash_effect_left, noScale);
                heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.flash_effect_right, noScale);
                hero.dash = new Animation(heroLeftBmp, heroRightBmp, 6);
                // Load the Dying Sprite Sheet
                heroLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_dying_sprites_left, noScale);
                heroRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_dying_sprites_right, noScale);
                hero.dying = new Animation(heroLeftBmp, heroRightBmp, 123);

                System.out.println("Hero Thread ended!");
            }
        });

        // Hero Testing
        // hero.sample = BitmapFactory.decodeResource(getResources(), R.drawable.test_platform_asset, noScale);
        Thread enemyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Enemy Thread started!");
                // Initialize the Enemies array
                enemies = new ArrayList<>();
                // Enemy 1
                Enemy tempEnemy = new Enemy(new Rect((Constants.SCREENWIDTH * 80) / 100, (Constants.SCREENHEIGHT * 4) / 10,
                        (Constants.SCREENWIDTH * 95) / 100, (Constants.SCREENHEIGHT * 6) / 10));
                tempEnemy.entityHeight = entityHeight;
                // Load the Idle Sprite Sheet
                Bitmap enemyLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_idle_sprites_left, noScale);
                Bitmap enemyRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_idle_sprites_right, noScale);
                tempEnemy.idle = new Animation(enemyLeftBmp, enemyRightBmp, 68);
                // Load the Walking Sprite Sheet
                enemyLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_walk_sprites_left, noScale);
                enemyRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_walk_sprites_right, noScale);
                tempEnemy.walk = new Animation(enemyLeftBmp, enemyRightBmp, 118);
                // Load the Jump-Start Sprite Sheet
                enemyLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_jump_start_sprites_left, noScale);
                enemyRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_jump_start_sprites_right, noScale);
                tempEnemy.jumpStart = new Animation(enemyLeftBmp, enemyRightBmp, 35);
                // Load the Airborne Sprite Sheet
                enemyLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_airborne_sprites_left, noScale);
                enemyRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_airborne_sprites_right, noScale);
                tempEnemy.airborne = new Animation(enemyLeftBmp, enemyRightBmp, 68);
                // Load the Landing Sprite Sheet
                enemyLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_landing_sprites_left, noScale);
                enemyRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_landing_sprites_right, noScale);
                tempEnemy.land = new Animation(enemyLeftBmp, enemyRightBmp, 26);
                // Load the Dying Sprite Sheet
                enemyLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_dying_sprites_left, noScale);
                enemyRightBmp = BitmapFactory.decodeResource(getResources(), R.drawable.phasad_dying_sprites_right, noScale);
                tempEnemy.dying = new Animation(enemyLeftBmp, enemyRightBmp, 68);
                // Load the Bleed Effects
                Bitmap tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.blood_effect, noScale);
                tempAnimation = new Animation(tempBmp, tempBmp, 6);
                tempAnimation.framesToSkip = 2;
                tempAnimation.skipFrame = 2;
                tempEnemy.bleed = tempAnimation;
                // Load Extra Sprite Sheets
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.transparent, noScale);
                tempAnimation = new Animation(tempBmp, tempBmp, 1);
                tempEnemy.emerge1 = tempAnimation;
                tempAnimation = new Animation(tempBmp, tempBmp, 1);
                tempEnemy.emerge2 = tempAnimation;
                tempAnimation = new Animation(tempBmp, tempBmp, 1);
                tempEnemy.stomp = tempAnimation;
                tempAnimation = new Animation(tempBmp, tempBmp, 1);
                tempEnemy.clawGround = tempAnimation;
                tempEnemy.sample = tempBmp;
                // Add the Phasad to the Array List
                enemies.add(tempEnemy);

                // Enemy 2
                Enemy anotherEnemy = new Enemy(tempEnemy);
                anotherEnemy.physicalHitbox = new Rect((Constants.SCREENWIDTH * 180) / 100, (Constants.SCREENHEIGHT * 4) / 10,
                        (Constants.SCREENWIDTH * 195) / 100, (Constants.SCREENHEIGHT * 6) / 10);
                anotherEnemy.entityHeight = entityHeight;
                enemies.add(anotherEnemy);

                // Enemy 3
                anotherEnemy = new Enemy(tempEnemy);
                anotherEnemy.physicalHitbox = new Rect((Constants.SCREENWIDTH * 230) / 100, (Constants.SCREENHEIGHT * 4) / 10,
                        (Constants.SCREENWIDTH * 245) / 100, (Constants.SCREENHEIGHT * 6) / 10);
                anotherEnemy.entityHeight = entityHeight;
                enemies.add(anotherEnemy);

                // Enemy 4
                tempEnemy = new Enemy(anotherEnemy);
                tempEnemy.physicalHitbox = new Rect((Constants.SCREENWIDTH * 280) / 100, 0,
                        (Constants.SCREENWIDTH * 295) / 100, (Constants.SCREENHEIGHT * 2) / 10);
                tempEnemy.entityHeight = entityHeight;
                enemies.add(tempEnemy);

                // Enemy 5
                anotherEnemy = new Enemy(tempEnemy);
                anotherEnemy.physicalHitbox = new Rect((Constants.SCREENWIDTH * 300) / 100, (Constants.SCREENHEIGHT * 4) / 10,
                        (Constants.SCREENWIDTH * 315) / 100, (Constants.SCREENHEIGHT * 6) / 10);
                anotherEnemy.entityHeight = entityHeight;
                enemies.add(anotherEnemy);

                // Enemy 6
                tempEnemy = new Enemy(anotherEnemy);
                tempEnemy.physicalHitbox = new Rect((Constants.SCREENWIDTH * 320) / 100, 0,
                        (Constants.SCREENWIDTH * 335) / 100, (Constants.SCREENHEIGHT * 2) / 10);
                tempEnemy.entityHeight = entityHeight;
                enemies.add(tempEnemy);

                // Enemy 7
                anotherEnemy = new Enemy(tempEnemy);
                anotherEnemy.physicalHitbox = new Rect((Constants.SCREENWIDTH * 300) / 100, (Constants.SCREENHEIGHT * 4) / 10,
                        (Constants.SCREENWIDTH * 315) / 100, (Constants.SCREENHEIGHT * 6) / 10);
                anotherEnemy.entityHeight = entityHeight;
                enemies.add(anotherEnemy);

                // Enemy 8
                tempEnemy = new Enemy(anotherEnemy);
                tempEnemy.physicalHitbox = new Rect((Constants.SCREENWIDTH * 350) / 100, 0,
                        (Constants.SCREENWIDTH * 365) / 100, (Constants.SCREENHEIGHT * 2) / 10);
                tempEnemy.entityHeight = entityHeight;
                enemies.add(tempEnemy);

                /*
                // Load Scahtur
                Enemy bossEnemy = new Enemy(new Rect((Constants.SCREENWIDTH * 80) / 100, -1000, (Constants.SCREENWIDTH * 100) / 100, 500));
                bossEnemy.entityHeight = 1500;
                // Load Emerging Animation
                enemyLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.scahtur_emerging_1_sprites_left, noScale);
                tempAnimation = new Animation(enemyLeftBmp, enemyLeftBmp, 76);
                tempAnimation.skipFrame = 1;
                tempAnimation.framesToSkip = 1;
                bossEnemy.emerge1 = tempAnimation;
                enemyLeftBmp = BitmapFactory.decodeResource(getResources(), R.drawable.scahtur_emerging_2_sprites_left, noScale);
                tempAnimation = new Animation(enemyLeftBmp, enemyLeftBmp, 76);
                tempAnimation.skipFrame = 1;
                tempAnimation.framesToSkip = 1;
                bossEnemy.emerge2 = tempAnimation;
                // Load Idle Animation
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.scahtur_idle_sprites_left, noScale);
                tempAnimation = new Animation(tempBmp, tempBmp, 77);
                bossEnemy.idle = tempAnimation;
                // Load Stomp Animation
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.scahtur_stomp_sprites_left, noScale);
                tempAnimation = new Animation(tempBmp, tempBmp, 61);
                tempAnimation.skipFrame = 1;
                tempAnimation.framesToSkip = 1;
                bossEnemy.stomp = tempAnimation;
                // Load Claw Ground Animation
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.scahtur_claw_ground_sprites_left, noScale);
                tempAnimation = new Animation(tempBmp, tempBmp, 102);
                bossEnemy.clawGround = tempAnimation;
                // Load Dying Animation 1
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.scahtur_dying_sprites_1_left, noScale);
                tempAnimation = new Animation(tempBmp, tempBmp, 76);
                bossEnemy.dying = tempAnimation;
                // Load Dying Animation 2
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.scahtur_dying_sprites_2_left, noScale);
                tempAnimation = new Animation(tempBmp, tempBmp, 76);
                bossEnemy.dying = tempAnimation;
                // Load Extra Sprite Sheets
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.transparent, noScale);
                tempAnimation = new Animation(tempBmp, tempBmp, 1);
                bossEnemy.walk = tempAnimation;
                tempAnimation = new Animation(tempBmp, tempBmp, 1);
                bossEnemy.jumpStart = tempAnimation;
                tempAnimation = new Animation(tempBmp, tempBmp, 1);
                bossEnemy.airborne = tempAnimation;
                tempAnimation = new Animation(tempBmp, tempBmp, 1);
                bossEnemy.land = tempAnimation;
                tempAnimation = new Animation(tempBmp, tempBmp, 1);
                bossEnemy.bleed = tempAnimation;
                bossEnemy.boss = true;
                bossEnemy.health = 100;
                // Sample
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.resource_health, noScale);
                bossEnemy.sample = tempBmp;

                enemies.add(bossEnemy);
                */
                System.out.println("Enemy Thread ended!");
            }
        });

        Thread otherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Other Thread started!");
                // Define the region boundaries
                fastMoveZoneR = new Rect(0, 0, Constants.SCREENWIDTH / 6, Constants.SCREENHEIGHT / 4);
                slowMoveZoneR = new Rect(0, Constants.SCREENHEIGHT / 4, Constants.SCREENWIDTH / 6, Constants.SCREENHEIGHT / 2);
                slowMoveZoneL = new Rect(0, Constants.SCREENHEIGHT / 2, Constants.SCREENWIDTH / 6, (Constants.SCREENHEIGHT * 3) / 4);
                fastMoveZoneL = new Rect(0, (Constants.SCREENHEIGHT * 3) / 4, Constants.SCREENWIDTH / 6, Constants.SCREENHEIGHT);
                actionZone = new Rect(Constants.SCREENWIDTH / 6, 0, Constants.SCREENWIDTH, Constants.SCREENHEIGHT);
                menuZone = new Rect((Constants.SCREENWIDTH * 40) / 100 - 10, Constants.SCREENHEIGHT / 10 - 50, (Constants.SCREENWIDTH * 60) / 100 + 10, Constants.SCREENHEIGHT / 10 + 90);

                // Define the HUD Elements (health, stamina, reserve, menu)
                elements = new ArrayList<>();
                tempElement = new HUDElement(new Rect((Constants.SCREENWIDTH * 40) / 100, Constants.SCREENHEIGHT / 10 - 40, (Constants.SCREENWIDTH * 60)/ 100,
                        Constants.SCREENHEIGHT / 10 - 20), 1);
                tempElement.mainRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.resource_health);
                tempElement.secondaryRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.resource_empty);
                elements.add(tempElement);
                tempElement = new HUDElement(new Rect((Constants.SCREENWIDTH * 40) / 100, Constants.SCREENHEIGHT / 10 + 10, (Constants.SCREENWIDTH * 50) / 100,
                        Constants.SCREENHEIGHT / 10 + 30), 2);
                tempElement.mainRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.resource_reserve);
                tempElement.secondaryRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.resource_empty);
                elements.add(tempElement);
                tempElement = new HUDElement(new Rect((Constants.SCREENWIDTH * 40) / 100, Constants.SCREENHEIGHT / 10 + 60, (Constants.SCREENWIDTH * 60) / 100,
                        Constants.SCREENHEIGHT / 10 + 80), 3);
                tempElement.mainRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.resource_stamina);
                tempElement.secondaryRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.resource_empty);
                elements.add(tempElement);
                tempElement = new HUDElement(new Rect((Constants.SCREENWIDTH * 54) / 100, Constants.SCREENHEIGHT / 10 - 5, (Constants.SCREENWIDTH * 54) / 100 + 10,
                        Constants.SCREENHEIGHT / 10 + 45), 4);
                tempElement.mainRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.utility_pause);
                elements.add(tempElement);
                tempElement = new HUDElement(new Rect((Constants.SCREENWIDTH * 55) / 100, Constants.SCREENHEIGHT / 10 - 5, (Constants.SCREENWIDTH * 55) / 100 + 10,
                        Constants.SCREENHEIGHT / 10 + 45), 4);
                tempElement.mainRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.utility_pause);
                elements.add(tempElement);
                tempElement = new HUDElement(new Rect((Constants.SCREENWIDTH * 40) / 100 - 10, Constants.SCREENHEIGHT / 10 - 50, (Constants.SCREENWIDTH * 60) / 100 + 10,
                        Constants.SCREENHEIGHT / 10 + 90), 4);
                tempElement.mainRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.resource_empty);
                elements.add(tempElement);

                // Define the Boundaries (Floors, walls, and background)
                obstructables = new ArrayList<>();
                // Background
                Bitmap tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.background, scaleDown2);
                tempRect1 = new Rect(-Constants.SCREENWIDTH, -((Constants.SCREENHEIGHT * 20) / 10), 2, (Constants.SCREENHEIGHT * 13) / 10);
                Obstructable background = new Obstructable(tempRect1, tempRect1, tempBmp, false);
                obstructables.add(background); // 0

                tempRect1 = new Rect(0, -((Constants.SCREENHEIGHT * 20) / 10), Constants.SCREENWIDTH + 2, (Constants.SCREENHEIGHT * 13) / 10);
                background = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                obstructables.add(background); // 1

                tempRect1 = new Rect(Constants.SCREENWIDTH, -((Constants.SCREENHEIGHT * 20) / 10), Constants.SCREENWIDTH * 2 + 2, (Constants.SCREENHEIGHT * 13) / 10);
                background = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                obstructables.add(background); // 2

                tempRect1 = new Rect(Constants.SCREENWIDTH * 2, -((Constants.SCREENHEIGHT * 20) / 10), Constants.SCREENWIDTH * 3 + 2, (Constants.SCREENHEIGHT * 13) / 10);
                background = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                obstructables.add(background); // 3

                tempRect1 = new Rect(Constants.SCREENWIDTH * 3, -((Constants.SCREENHEIGHT * 20) / 10), Constants.SCREENWIDTH * 4 + 2, (Constants.SCREENHEIGHT * 13) / 10);
                background = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                obstructables.add(background); // 4

                tempRect1 = new Rect(Constants.SCREENWIDTH * 4, -((Constants.SCREENHEIGHT * 20) / 10), Constants.SCREENWIDTH * 5 + 2, (Constants.SCREENHEIGHT * 13) / 10);
                background = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                obstructables.add(background); // 5

                tempRect1 = new Rect(Constants.SCREENWIDTH * 5, -((Constants.SCREENHEIGHT * 20) / 10), Constants.SCREENWIDTH * 6 + 2, (Constants.SCREENHEIGHT * 13) / 10);
                background = new Obstructable(tempRect1, tempRect1, tempBmp, false);
                obstructables.add(background); // 6

                // Floor
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.transparent);
                tempRect1 = new Rect(-Constants.SCREENWIDTH, Constants.SCREENHEIGHT + 10, Constants.SCREENWIDTH * 5, Constants.SCREENHEIGHT + 15);
                obstructables.add(new Obstructable(tempRect1, tempRect1, tempBmp, false)); // 7
                // Left Bounds (Cobblestone Pillar)
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.cobblestone_pillar, scaleDown2);
                tempRect1 = new Rect(-100, Constants.SCREENHEIGHT / 2, 0, Constants.SCREENHEIGHT);
                tempRect2 = new Rect(-100, Constants.SCREENHEIGHT / 2, 0, Constants.SCREENHEIGHT + 20);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false)); // 8
                // Castle Tower (Middle)
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.cobblestone_pillar, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 250) / 100 - 50, Constants.SCREENHEIGHT / 2, (Constants.SCREENWIDTH * 250) / 100 + 50, Constants.SCREENHEIGHT);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 250) / 100 - 50, Constants.SCREENHEIGHT / 2, (Constants.SCREENWIDTH * 250) / 100 + 50, Constants.SCREENHEIGHT + 20);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false)); // 9
                // Platform 1
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_1, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 70) / 100, (Constants.SCREENHEIGHT * 80) / 100, (Constants.SCREENWIDTH * 110) / 100, Constants.SCREENHEIGHT);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 78) / 100, (Constants.SCREENHEIGHT * 80) / 100 + 20, (Constants.SCREENWIDTH * 106) / 100, (Constants.SCREENHEIGHT * 80) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 2
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_2, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 100) / 100, (Constants.SCREENHEIGHT * 60) / 100, (Constants.SCREENWIDTH * 150) / 100, (Constants.SCREENHEIGHT * 85) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 105) / 100, (Constants.SCREENHEIGHT * 60) / 100 + 20, (Constants.SCREENWIDTH * 145) / 100, (Constants.SCREENHEIGHT * 60) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 3
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_3, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 150) / 100, (Constants.SCREENHEIGHT * 80) / 100, (Constants.SCREENWIDTH * 170) / 100, (Constants.SCREENHEIGHT * 90) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 152) / 100, (Constants.SCREENHEIGHT * 80) / 100 + 20, (Constants.SCREENWIDTH * 169) / 100, (Constants.SCREENHEIGHT * 80) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 4
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_1, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 170) / 100, (Constants.SCREENHEIGHT * 60) / 100, (Constants.SCREENWIDTH * 190) / 100, (Constants.SCREENHEIGHT * 70) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 172) / 100, (Constants.SCREENHEIGHT * 60) / 100 + 20, (Constants.SCREENWIDTH * 189) / 100, (Constants.SCREENHEIGHT * 60) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 5
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_2, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 150) / 100, (Constants.SCREENHEIGHT * 40) / 100, (Constants.SCREENWIDTH * 170) / 100, (Constants.SCREENHEIGHT * 50) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 155) / 100, (Constants.SCREENHEIGHT * 40) / 100 + 20, (Constants.SCREENWIDTH * 165) / 100, (Constants.SCREENHEIGHT * 40) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 6
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_3, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 170) / 100, (Constants.SCREENHEIGHT * 20) / 100, (Constants.SCREENWIDTH * 190) / 100, (Constants.SCREENHEIGHT * 30) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 175) / 100, (Constants.SCREENHEIGHT * 20) / 100 + 20, (Constants.SCREENWIDTH * 185) / 100, (Constants.SCREENHEIGHT * 20) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 7
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_2, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 150) / 100, 0, (Constants.SCREENWIDTH * 190) / 100, (Constants.SCREENHEIGHT * 20) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 155) / 100, 20, (Constants.SCREENWIDTH * 185) / 100, 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 8
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_3, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 190) / 100, 0, (Constants.SCREENWIDTH * 230) / 100, (Constants.SCREENHEIGHT * 20) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 195) / 100, 20, (Constants.SCREENWIDTH * 225) / 100, 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 9
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_1, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 210) / 100, (Constants.SCREENHEIGHT * 20) / 100, (Constants.SCREENWIDTH * 250) / 100, (Constants.SCREENHEIGHT * 40) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 215) / 100, (Constants.SCREENHEIGHT * 20) / 100 + 20, (Constants.SCREENWIDTH * 245) / 100, (Constants.SCREENHEIGHT * 20) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 10
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_2, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 240) / 100, (Constants.SCREENHEIGHT * 40) / 100, (Constants.SCREENWIDTH * 280) / 100, (Constants.SCREENHEIGHT * 60) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 245) / 100, (Constants.SCREENHEIGHT * 40) / 100 + 20, (Constants.SCREENWIDTH * 275) / 100, (Constants.SCREENHEIGHT * 40) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 11
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_1, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 260) / 100, (Constants.SCREENHEIGHT * 60) / 100, (Constants.SCREENWIDTH * 300) / 100, (Constants.SCREENHEIGHT * 80) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 265) / 100, (Constants.SCREENHEIGHT * 60) / 100 + 20, (Constants.SCREENWIDTH * 295) / 100, (Constants.SCREENHEIGHT * 60) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 12
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_3, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 310) / 100, (Constants.SCREENHEIGHT * 80) / 100, (Constants.SCREENWIDTH * 350) / 100, (Constants.SCREENHEIGHT * 100) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 315) / 100, (Constants.SCREENHEIGHT * 80) / 100 + 20, (Constants.SCREENWIDTH * 345) / 100, (Constants.SCREENHEIGHT * 80) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 13
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_2, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 340) / 100, (Constants.SCREENHEIGHT * 60) / 100, (Constants.SCREENWIDTH * 360) / 100, (Constants.SCREENHEIGHT * 70) / 100);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 345) / 100, (Constants.SCREENHEIGHT * 60) / 100 + 20, (Constants.SCREENWIDTH * 355) / 100, (Constants.SCREENHEIGHT * 60) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));
                // Platform 14
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.platform_type_1, scaleDown2);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 400) / 100, (Constants.SCREENHEIGHT * 80) / 100, (Constants.SCREENWIDTH * 440) / 100, Constants.SCREENHEIGHT);
                tempRect2 = new Rect((Constants.SCREENWIDTH * 405) / 100, (Constants.SCREENHEIGHT * 80) / 100 + 20, (Constants.SCREENWIDTH * 435) / 100, (Constants.SCREENHEIGHT * 80) / 100 + 21);
                obstructables.add(new Obstructable(tempRect1, tempRect2, tempBmp, false));

                // Adding torches
                tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.red_torch_sprites, noScale);
                tempRect1 = new Rect((Constants.SCREENWIDTH * 10) / 100, (Constants.SCREENHEIGHT * 7) / 10, (Constants.SCREENWIDTH * 15) / 100, Constants.SCREENHEIGHT);
                Obstructable tempObs = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                tempAnimation = new Animation(tempBmp, tempBmp, 4);
                tempAnimation.skipFrame = 3;
                tempAnimation.framesToSkip = 3;
                tempObs.animation = new Animation(tempAnimation);
                obstructables.add(tempObs); // 8

                tempRect1 = new Rect((Constants.SCREENWIDTH * 60) / 100, (Constants.SCREENHEIGHT * 7) / 10, (Constants.SCREENWIDTH * 65) / 100, Constants.SCREENHEIGHT);
                tempObs = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                tempObs.animation = new Animation(tempAnimation);
                obstructables.add(tempObs); // 9

                tempRect1 = new Rect((Constants.SCREENWIDTH * 110) / 100, (Constants.SCREENHEIGHT * 7) / 10, (Constants.SCREENWIDTH * 115) / 100, Constants.SCREENHEIGHT);
                tempObs = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                tempObs.animation = new Animation(tempAnimation);
                obstructables.add(tempObs); // 10

                tempRect1 = new Rect((Constants.SCREENWIDTH * 160) / 100, (Constants.SCREENHEIGHT * 7) / 10, (Constants.SCREENWIDTH * 165) / 100, Constants.SCREENHEIGHT);
                tempObs = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                tempObs.animation = new Animation(tempAnimation);
                obstructables.add(tempObs); // 11

                tempRect1 = new Rect((Constants.SCREENWIDTH * 210) / 100, (Constants.SCREENHEIGHT * 7) / 10, (Constants.SCREENWIDTH * 215) / 100, Constants.SCREENHEIGHT);
                tempObs = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                tempObs.animation = new Animation(tempAnimation);
                obstructables.add(tempObs); // 12

                tempRect1 = new Rect((Constants.SCREENWIDTH * 260) / 100, (Constants.SCREENHEIGHT * 7) / 10, (Constants.SCREENWIDTH * 265) / 100, Constants.SCREENHEIGHT);
                tempObs = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                tempObs.animation = new Animation(tempAnimation);
                obstructables.add(tempObs); // 13

                tempRect1 = new Rect((Constants.SCREENWIDTH * 310) / 100, (Constants.SCREENHEIGHT * 7) / 10, (Constants.SCREENWIDTH * 315) / 100, Constants.SCREENHEIGHT);
                tempObs = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                tempObs.animation = new Animation(tempAnimation);
                obstructables.add(tempObs); // 14

                tempRect1 = new Rect((Constants.SCREENWIDTH * 360) / 100, (Constants.SCREENHEIGHT * 7) / 10, (Constants.SCREENWIDTH * 365) / 100, Constants.SCREENHEIGHT);
                tempObs = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                tempObs.animation = new Animation(tempAnimation);
                obstructables.add(tempObs); // 15

                tempRect1 = new Rect((Constants.SCREENWIDTH * 410) / 100, (Constants.SCREENHEIGHT * 7) / 10, (Constants.SCREENWIDTH * 415) / 100, Constants.SCREENHEIGHT);
                tempObs = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                tempObs.animation = new Animation(tempAnimation);
                obstructables.add(tempObs); // 16

                tempRect1 = new Rect((Constants.SCREENWIDTH * 460) / 100, (Constants.SCREENHEIGHT * 7) / 10, (Constants.SCREENWIDTH * 465) / 100, Constants.SCREENHEIGHT);
                tempObs = new Obstructable(tempRect1, tempRect1, tempBmp, true);
                tempObs.animation = new Animation(tempAnimation);
                obstructables.add(tempObs); // 17

                System.out.println("Other Thread ended!");
            }
        });

        ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.execute(heroThread);
        pool.execute(enemyThread);
        pool.execute(otherThread);
        pool.shutdown();
        
        try {
            boolean initFinished = pool.awaitTermination(1, TimeUnit.MINUTES);
            while(!initFinished) {
                wait(100);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println("All threads finished!");

        // New Checkpoint
        checkpointHero = new Hero(hero);
        checkpointObs = new ArrayList<>(obstructables.size());
        for (int i = 0; i < obstructables.size(); i++)
            checkpointObs.add(new Obstructable(obstructables.get(i)));
        checkpointEnemies = new ArrayList<>(enemies.size());
        for (int i = 0; i < enemies.size(); i++)
            checkpointEnemies.add(new Enemy(enemies.get(i)));
        // Create a Gesture Detector to aid in handling touch events
        gestureDetector = new GestureDetector(context, this);
        setFocusable(true);
    }

    public void update() {
        if(hero.health > 0) {
            hero.update(primaryZone, obstructables, enemies); // Update the Hero
            for (int i = 0; i < elements.size(); i++) // Update all HUD Elements
                elements.get(i).update(hero);
            for (int i = 0; i < enemies.size(); i++) { // Update all Enemies
                if(enemies.get(i).health > 0) {
                    enemies.get(i).update(obstructables, hero);
                    if (Rect.intersects(enemies.get(i).visualHitbox, screen)) {
                        enemies.get(i).draw = true;
                    } else
                        enemies.get(i).draw = false;
                } else {
                    if(enemies.size() == 0) {
                        Intent intent = new Intent(getContext(), TitleActivity.class);
                        getContext().startActivity(intent);
                    } else if(!enemies.get(i).boss && !enemies.get(i).isDying)
                        enemies.remove(i);
                    else
                        enemies.get(i).update(obstructables, hero);
                }
            }
            for (int i = 0; i < obstructables.size(); i++) { // Update all Obstructables
                obstructables.get(i).update();
                if (Rect.intersects(obstructables.get(i).visualHitbox, screen)) {
                    obstructables.get(i).draw = true;
                } else
                    obstructables.get(i).draw = false;
            }
        } else {
            if(!hero.isDying)
                hero.update(primaryZone, obstructables, enemies); // Update the Hero
            else {
                // Hero died, go back to checkpoint
                enemies.clear();
                obstructables.clear();
                hero = new Hero(checkpointHero);
                for (int i = 0; i < checkpointEnemies.size(); i++)
                    enemies.add(new Enemy(checkpointEnemies.get(i)));
                for (int i = 0; i < checkpointObs.size(); i++)
                    obstructables.add(new Obstructable(checkpointObs.get(i)));
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (int i = 0; i < obstructables.size(); i++) { // Draw all Obstructables in range
            if (obstructables.get(i).draw) {
                obstructables.get(i).draw(canvas);
            }
        }
        for (int i = 0; i < enemies.size(); i++) { // Draw all Enemies in range
            if (enemies.get(i).draw) {
                enemies.get(i).draw(canvas);
            }
        }
        for (int i = elements.size() - 1; i >= 0; i--) {// Draw all HUD Elements
            elements.get(i).draw(canvas);
        }
        hero.draw(canvas); // Draw the Hero
    }

    public int GetZonePressed(int x, int y) {
        if (fastMoveZoneR.contains(x, y)) {
            return 1;
        } else if (slowMoveZoneR.contains(x, y)) {
            return 2;
        } else if (slowMoveZoneL.contains(x, y)) {
            return 3;
        } else if (fastMoveZoneL.contains(x, y)) {
            return 4;
        } else if (menuZone.contains(x, y)) {
            return 5;
        } else if (actionZone.contains(x, y)) {
            return 6;
        } else
            return 0;
    }

    // Overridden SurfaceView methods

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true); // Starts the thread
        gameThread.setPriority(10);
        gameThread.start();
        System.out.println("[MARC] GameThread Priority = " + gameThread.getPriority());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        gameThread.setRunning(false);
    }

    // Overridden Gesture Detector methods

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        primaryZone = GetZonePressed((int) e.getX(), (int) e.getY());
        if(!gameThread.isRunning() && primaryZone == 5 && gameThread.pauseTimer == 25) {
            gameThread.setRunning(true);
            Constants.sounds.play(Constants.soundIndex[9], 1, 1, 1, 0, 1);
        } else if(primaryZone == 5 && gameThread.pauseTimer == 0) {
            System.out.println("Pause button press detected!");
            if(gameThread.isRunning()) {
                Constants.sounds.play(Constants.soundIndex[9], 1, 1, 1, 0, 1);
                gameThread.setRunning(false);
                return true;
            }
        } else if (e.getPointerCount() <= 2 && primaryZone != 6 && primaryZone != 5 && gameThread.isRunning()) {
            switch (e.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN: {
                    secondaryTouchX = (int) e.getX(1);
                    secondaryTouchY = (int) e.getY(1);
                }
                case MotionEvent.ACTION_POINTER_UP: {
                    int deltaX = abs((int) (secondaryTouchX - e.getX(1)));
                    int deltaY = abs((int) (secondaryTouchY - e.getY(1)));
                    if (deltaX == 0 && deltaY == 0) {
                        break;
                    } else if (deltaY > deltaX) { // Swipe is vertical
                        if (secondaryTouchY > e.getY(1)) {
                            System.out.println("Swipe up detected!");
                            if (hero.onGround) {
                                hero.startJump = true;
                                hero.isWalking = false;
                                hero.isRunning = false;
                            }
                        } else {
                            System.out.println("Swipe down detected!");
                            if (hero.onGround) {
                                hero.onGround = false;
                                hero.isFalling = true;
                                hero.phaseThrough = true;
                                hero.isWalking = false;
                                hero.isRunning = false;
                            }
                        }
                    } else { // Swipe must be horizontal
                        if (secondaryTouchX > e.getX(1)) {
                            System.out.println("Swipe left detected!");
                            if (hero.isFacingLeft) {
                                if((deltaX > (Constants.SCREENWIDTH / 3)) && !hero.isDashing && !hero.isAttacking && !hero.isLanding && hero.onGround && !hero.startJump) {
                                    hero.xVelocity = -Constants.HERODASHDISTANCE;
                                    hero.isDashing = true;
                                    if (!hero.ConsumeStamina(100))
                                        hero.ConsumeReserve();
                                    Constants.sounds.play(Constants.soundIndex[2], 1, 1, 1, 0, 1);
                                } else
                                    hero.RegisterAttack(1);
                            } else {
                                hero.RegisterAttack(2);
                            }
                        } else {
                            System.out.println("Swipe right detected!");
                            if (hero.isFacingLeft) {
                                hero.RegisterAttack(2);
                            } else {
                                if((deltaX > (Constants.SCREENWIDTH / 3)) && !hero.isDashing && !hero.isAttacking && !hero.isLanding && hero.onGround && !hero.startJump) {
                                    hero.xVelocity = Constants.HERODASHDISTANCE;
                                    hero.isDashing = true;
                                    if (!hero.ConsumeStamina(100))
                                        hero.ConsumeReserve();
                                    Constants.sounds.play(Constants.soundIndex[2], 1, 1, 1, 0, 1);
                                } else
                                    hero.RegisterAttack(1);
                            }
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    primaryZone = 0; // No movement
                    break;
                }
            }
        } else {
            if(gameThread.isRunning()) {
                return gestureDetector.onTouchEvent(e);
            } else {
                System.out.println("Returning nothing when paused!");
            }
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // If there is a larger change in X, the swipe must be horizontal.
        // If there is a larger change in Y, the swipe must be vertical.
        if(e1 == null || e2 == null) {
            System.out.println("Null event detected!");
            return true;
        }
        int deltaX = abs((int)(e1.getX() - e2.getX()));
        int deltaY = abs((int)(e1.getY() - e2.getY()));
        if(deltaY > deltaX) {
            if(e1.getY() > e2.getY()) { // Swipe Up
                if(hero.onGround) {
                    hero.startJump = true;
                    hero.isWalking = false;
                    hero.isRunning = false;
                }
            } else { // Swipe Down
                if(hero.onGround) {
                    hero.onGround = false;
                    hero.isFalling = true;
                    hero.phaseThrough = true;
                    hero.isWalking = false;
                    hero.isRunning = false;
                }
            }
        } else { // Swipe must be horizontal
            if (e1.getX() > e2.getX()) { // Swipe Left
                if (hero.isFacingLeft) {
                    if((deltaX > (Constants.SCREENWIDTH / 3)) && !hero.isDashing && !hero.isAttacking && !hero.isLanding && hero.onGround && !hero.startJump) {
                        hero.xVelocity = -Constants.HERODASHDISTANCE;
                        hero.isDashing = true;
                        if (!hero.ConsumeStamina(100))
                            hero.ConsumeReserve();
                        Constants.sounds.play(Constants.soundIndex[2], 1, 1, 1, 0, 1);
                    } else
                        hero.RegisterAttack(1);
                } else {
                    hero.RegisterAttack(2);
                }
            } else { // Swipe Right
                if (hero.isFacingLeft) {
                    hero.RegisterAttack(2);
                } else {
                    if((deltaX > (Constants.SCREENWIDTH / 3)) && !hero.isDashing && !hero.isAttacking && !hero.isLanding && hero.onGround && !hero.startJump) {
                        hero.xVelocity = Constants.HERODASHDISTANCE;
                        hero.isDashing = true;
                        if (!hero.ConsumeStamina(100))
                            hero.ConsumeReserve();
                        Constants.sounds.play(Constants.soundIndex[2], 1, 1, 1, 0, 1);
                    } else
                        hero.RegisterAttack(1);
                }
            }
        }
        return true;
    }
}
