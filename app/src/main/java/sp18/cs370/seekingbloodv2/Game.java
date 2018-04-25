package sp18.cs370.seekingbloodv2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class Game extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener {
    private GameThread gameThread;
    private GestureDetector gestureDetector;
    private Hero hero;
    private Rect fastMoveZoneR;
    private Rect slowMoveZoneR;
    private Rect slowMoveZoneL;
    private Rect fastMoveZoneL;
    private Rect actionZone;
    private int primaryZone;
    private int secondaryTouchX;
    private int secondaryTouchY;
    Bitmap tempBmp;
    HUDElement tempElement;
    public ArrayList<Obstructable> obstructables;
    public ArrayList<Enemy> enemies;
    public ArrayList<HUDElement> elements;

    public Game(Context context) {
        super(context);
        getHolder().addCallback(this);

        // Force Bitmap Factory to not scale Bitmaps
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Create the Hero - Has animations for: walking, running, beginning a jump, airborne,
        // landing, and attacking.
        hero = new Hero(new Rect((Constants.SCREENWIDTH * 2) / 5, ((Constants.SCREENHEIGHT * 6) / 10),
                Constants.SCREENWIDTH / 2, (Constants.SCREENHEIGHT * 8) / 10));
        hero.entityHeight = ((Constants.SCREENHEIGHT * 8) / 10) - ((Constants.SCREENHEIGHT * 6) / 10); // Store the Hero's height
        // Load the Walking Sprite Sheet
        tempBmp = ResizeBMP(BitmapFactory.decodeResource(getResources(), R.drawable.default_walk_sprites, options));
        Constants.HEROWALKSPRITEWIDTH = tempBmp.getWidth() / 68;
        Constants.HEROWALKSPRITEHEIGHT = tempBmp.getHeight();
        System.out.println("WALK SPRITE WIDTH = " + tempBmp.getWidth());
        System.out.println("WALK SPRITE HEIGHT = " + tempBmp.getHeight());
        hero.setLeftBmp(FlipBMP(tempBmp));
        hero.setRightBmp(tempBmp);
        // Load the Running Sprite Sheet
        tempBmp = ResizeBMP(BitmapFactory.decodeResource(getResources(), R.drawable.default_run_sprites, options));
        Constants.HERORUNSPRITEWIDTH = tempBmp.getWidth() / 55;
        Constants.HERORUNSPRITEHEIGHT = tempBmp.getHeight();
        hero.leftRunBmp = (FlipBMP(tempBmp));
        hero.rightRunBmp = tempBmp;
        // Load the Jump-Start Sprite Sheet
        tempBmp = ResizeBMP(BitmapFactory.decodeResource(getResources(), R.drawable.default_jump_start_sprites, options));
        Constants.HEROJUMPSPRITEWIDTH = tempBmp.getWidth() / 48;
        Constants.HEROJUMPSPRITEHEIGHT = tempBmp.getHeight();
        hero.leftJumpBmp = (FlipBMP(tempBmp));
        hero.rightJumpBmp = tempBmp;
        // Load the Airborne Sprite Sheet
        tempBmp = ResizeBMP(BitmapFactory.decodeResource(getResources(), R.drawable.default_airborne_sprites, options));
        Constants.HEROAIRBORNESPRITEWIDTH = tempBmp.getWidth() / 61;
        Constants.HEROAIRBORNESPRITEHEIGHT = tempBmp.getHeight();
        hero.leftAirborneBmp = (FlipBMP(tempBmp));
        hero.rightAirborneBmp = tempBmp;
        // Load the Landing Sprite Sheet
        tempBmp = ResizeBMP(BitmapFactory.decodeResource(getResources(), R.drawable.default_landing_sprites, options));
        Constants.HEROLANDSPRITEWIDTH = tempBmp.getWidth() / 45;
        Constants.HEROLANDSPRITEHEIGHT = tempBmp.getHeight();
        hero.leftLandingBmp = (FlipBMP(tempBmp));
        hero.rightLandingBmp = tempBmp;
        // Load the Forward Attack Sprite Sheet
        tempBmp = ResizeBMP(BitmapFactory.decodeResource(getResources(), R.drawable.default_forward_attack_sprites, options));
        Constants.HEROFORATKSPRITEWIDTH = tempBmp.getWidth() / 102;
        Constants.HEROFORATKSPRITEHEIGHT = tempBmp.getHeight();
        hero.leftForwardAttackBmp = (FlipBMP(tempBmp));
        hero.rightForwardAttackBmp = tempBmp;
        hero.sample = ResizeBMP(BitmapFactory.decodeResource(getResources(), R.drawable.test_platform_asset, options));

        // Define the HUD Elements (health, stamina, reserve, menu)
        elements = new ArrayList<>();
        tempElement = new HUDElement(new Rect((Constants.SCREENWIDTH * 8) / 10, (Constants.SCREENHEIGHT * 9) / 10 - 40, Constants.SCREENWIDTH,
                (Constants.SCREENHEIGHT * 9) / 10 - 20), 1);
        tempElement.mainRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.health);
        tempElement.secondaryRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.empty_resource);
        elements.add(tempElement);
        tempElement = new HUDElement(new Rect((Constants.SCREENWIDTH * 8) / 10, (Constants.SCREENHEIGHT * 9) / 10 + 10,(Constants.SCREENWIDTH * 9) / 10,
                (Constants.SCREENHEIGHT * 9) / 10 + 30), 2);
        tempElement.mainRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.reserve);
        tempElement.secondaryRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.empty_resource);
        elements.add(tempElement);
        tempElement = new HUDElement(new Rect((Constants.SCREENWIDTH * 8) / 10, (Constants.SCREENHEIGHT * 9) / 10 + 60, Constants.SCREENWIDTH,
                (Constants.SCREENHEIGHT * 9) / 10 + 80), 3);
        tempElement.mainRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.stamina);
        tempElement.secondaryRegionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.empty_resource);
        elements.add(tempElement);

        // Define the region boundaries and define a Gesture Detector
        fastMoveZoneR = new Rect(0, 0, Constants.SCREENWIDTH / 6, Constants.SCREENHEIGHT / 4);
        slowMoveZoneR = new Rect(0, Constants.SCREENHEIGHT / 4, Constants.SCREENWIDTH / 6, Constants.SCREENHEIGHT / 2);
        slowMoveZoneL = new Rect(0, Constants.SCREENHEIGHT / 2, Constants.SCREENWIDTH / 6, (3 * Constants.SCREENHEIGHT) / 4);
        fastMoveZoneL = new Rect(0, (3 * Constants.SCREENHEIGHT) / 4, Constants.SCREENWIDTH / 6, Constants.SCREENHEIGHT);
        actionZone = new Rect(Constants.SCREENWIDTH / 6, 0, Constants.SCREENWIDTH, Constants.SCREENHEIGHT);

        // Define the Obstructable (platforms, walls, and background)
        obstructables = new ArrayList<>();
        tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.background_level_1);
        Obstructable background = new Obstructable(new Rect(-((Constants.SCREENWIDTH * 4) / 10), -((Constants.SCREENHEIGHT * 3) / 10),
                (Constants.SCREENWIDTH * 24) / 10, (Constants.SCREENHEIGHT * 13) / 10), tempBmp); // Background Image (Nonphysical Obstructable)
        background.isNotPhysical = true;
        obstructables.add(background);
        tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.test_platform_asset);
        obstructables.add(new Obstructable(new Rect(0, Constants.SCREENHEIGHT - 4, Constants.SCREENWIDTH * 2, Constants.SCREENHEIGHT),
                tempBmp)); // Floor
        obstructables.add(new Obstructable(new Rect(0, -Constants.SCREENHEIGHT, 1, Constants.SCREENHEIGHT), tempBmp)); // Left Bounds
        obstructables.add(new Obstructable(new Rect(Constants.SCREENWIDTH * 2 - 1, -Constants.SCREENHEIGHT, Constants.SCREENWIDTH * 2, Constants.SCREENHEIGHT),
                tempBmp)); // Right Bounds
        obstructables.add(new Obstructable(new Rect(0, (Constants.SCREENHEIGHT * 90) / 100 - 1,
                        (Constants.SCREENWIDTH * 10) / 100, (Constants.SCREENHEIGHT * 90) / 100), tempBmp)); // Platform (0 to 10 wide, 90 bottom)
        obstructables.add(new Obstructable(new Rect((Constants.SCREENWIDTH * 20) / 100, (Constants.SCREENHEIGHT * 80) / 100 - 1,
                (Constants.SCREENWIDTH * 30) / 100, (Constants.SCREENHEIGHT * 80) / 100), tempBmp)); // Platform (20 to 30 wide, 70 bottom)
        obstructables.add(new Obstructable(new Rect((Constants.SCREENWIDTH * 40) / 100, (Constants.SCREENHEIGHT * 90) / 100 - 1,
                (Constants.SCREENWIDTH * 50) / 100, (Constants.SCREENHEIGHT * 90) / 100), tempBmp)); // Platform (40 to 50 wide, 85 bottom)
        obstructables.add(new Obstructable(new Rect((Constants.SCREENWIDTH * 80) / 100, (Constants.SCREENHEIGHT * 85) / 100 - 1,
                (Constants.SCREENWIDTH * 90) / 100, (Constants.SCREENHEIGHT * 85) / 100), tempBmp)); // Platform (80 to 90 wide, 85 bottom)
        obstructables.add(new Obstructable(new Rect((Constants.SCREENWIDTH * 80) / 100, (Constants.SCREENHEIGHT * 70) / 100 - 1,
                (Constants.SCREENWIDTH * 90) / 100, (Constants.SCREENHEIGHT * 70) / 100), tempBmp)); // Platform (80 to 90 wide, 70 bottom)
        obstructables.add(new Obstructable(new Rect((Constants.SCREENWIDTH * 80) / 100, (Constants.SCREENHEIGHT * 55) / 100 - 1,
                (Constants.SCREENWIDTH * 90) / 100, (Constants.SCREENHEIGHT * 55) / 100), tempBmp)); // Platform (80 to 90 wide, 55 bottom)

        // Define the Enemies
        enemies = new ArrayList<>();
        tempBmp = BitmapFactory.decodeResource(getResources(), R.drawable.test_entity);
        Enemy tempEnemy = new Enemy(new Rect((Constants.SCREENWIDTH * 8) / 10, (Constants.SCREENHEIGHT * 6) / 10,
                (Constants.SCREENWIDTH * 9) / 10, (Constants.SCREENHEIGHT * 8) / 10));
        tempEnemy.setRightBmp(tempBmp);
        tempEnemy.setLeftBmp(tempBmp);
        tempEnemy.entityHeight = ((Constants.SCREENHEIGHT * 8) / 10) - ((Constants.SCREENHEIGHT * 6) / 10);
        tempEnemy.bleedBmp = BitmapFactory.decodeResource(getResources(), R.drawable.blood_effect);
        enemies.add(tempEnemy);
        tempEnemy = new Enemy(new Rect((Constants.SCREENWIDTH * 11) / 10, (Constants.SCREENHEIGHT * 6) / 10,
                (Constants.SCREENWIDTH * 12) / 10, (Constants.SCREENHEIGHT * 8) / 10));
        tempEnemy.setRightBmp(tempBmp);
        tempEnemy.setLeftBmp(tempBmp);
        tempEnemy.entityHeight = ((Constants.SCREENHEIGHT * 8) / 10) - ((Constants.SCREENHEIGHT * 6) / 10);
        tempEnemy.bleedBmp = BitmapFactory.decodeResource(getResources(), R.drawable.blood_effect);
        enemies.add(tempEnemy);
        Constants.BLEEDSPRITEWIDTH = tempEnemy.bleedBmp.getWidth() / 6;
        Constants.BLEEDSPRITEHEIGHT = tempEnemy.bleedBmp.getHeight();
        System.out.println("Declared all!");
        // Create the Gesture Detector which aids in handling swipes
        gestureDetector = new GestureDetector(context, this);

        // Create a thread for this SurfaceView (Game)
        gameThread = new GameThread(getHolder(), this);

        // View can receive focus (i.e. receive touch events?)
        setFocusable(true);
    }

    public void update() {
        hero.update(primaryZone, obstructables, enemies); // Update the Hero
        for(int i = 0; i < elements.size(); i++) // Update all HUD Elements
            elements.get(i).update(hero);
        for(int i = 0; i < enemies.size(); i++) // Update all Enemies
            enemies.get(i).update(obstructables, hero);
        for(int i = 0; i < obstructables.size(); i++) // Update all Obstructables
            obstructables.get(i).update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for(int i = 0; i < obstructables.size(); i++) // Draw all Obstructables
            obstructables.get(i).draw(canvas);
        for(int i = 0; i < enemies.size(); i++) // Draw all Enemies
            enemies.get(i).draw(canvas);
        for(int i = 0; i < elements.size(); i++) // Draw all HUD Elements
            elements.get(i).draw(canvas);
        hero.draw(canvas);
    }

    public int GetZonePressed(int x, int y) {
        if(fastMoveZoneR.contains(x, y)) {
            return 1;
        } else if(slowMoveZoneR.contains(x, y)) {
            return 2;
        } else if(slowMoveZoneL.contains(x, y)) {
            return 3;
        } else if(fastMoveZoneL.contains(x, y)) {
            return 4;
        } else if(actionZone.contains(x, y)) {
            return 5;
        } else
            return 0;
    }

    Bitmap FlipBMP(Bitmap bmp) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, bmp.getWidth() / 2f, bmp.getHeight() / 2f);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    Bitmap ResizeBMP(Bitmap bmp) {
        /*
        Matrix matrix = new Matrix();
        matrix.postScale(2f, 2f);
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        System.out.println("Current BMP Width = " + width + ", BMP Height = " + height);
        bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, false);
        System.out.println("New BMP Width = " + bmp.getWidth() + ", BMP Height = " + bmp.getHeight());
        */
        return bmp;
    }

    // Overridden SurfaceView methods

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true); // Starts the thread
        gameThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        while(true) {
            try {
                gameThread.setRunning(false);
                gameThread.join();
            } catch(Exception e) {e.printStackTrace();}
        }
    }

    // Overridden Gesture Detector methods

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        primaryZone = GetZonePressed((int)e.getX(), (int)e.getY());
        if (e.getPointerCount() <= 2 && primaryZone != 5) {
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
                            if (hero.isOnGround())
                                hero.setStartJump(true);
                        } else {
                            System.out.println("Swipe down detected!");
                            if (hero.isOnGround()) {
                                hero.onGround = false;
                                hero.isFalling = true;
                                hero.phaseThrough = true;
                            }
                        }
                    } else { // Swipe must be horizontal
                        if( secondaryTouchX > e.getX(1)) {
                            System.out.println("Swipe left detected!");
                            if(hero.isFacingLeft) {
                                hero.MeleeAttack(1);
                            } else {
                                hero.MeleeAttack(2);
                            }
                        } else {
                            System.out.println("Swipe right detected!");
                            if(hero.isFacingLeft) {
                                hero.MeleeAttack(2);
                            } else {
                                hero.MeleeAttack(1);
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
        } else
            return gestureDetector.onTouchEvent(e);
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
        if(deltaY > deltaX) { // Swipe is vertical
            if(e1.getY() > e2.getY()) {
                System.out.println("Swipe up detected!");
                if(hero.isOnGround())
                    hero.setStartJump(true);
            } else {
                System.out.println("Swipe down detected!");
                if(hero.isOnGround()) {
                    hero.onGround = false;
                    hero.isFalling = true;
                    hero.phaseThrough = true;
                }
            }
        } else { // Swipe must be horizontal
            if(e1.getX() > e2.getX()) {
                System.out.println("Swipe left detected!");
                if(hero.isFacingLeft) {
                    hero.MeleeAttack(1);
                } else {
                    hero.MeleeAttack(2);
                }
            } else {
                System.out.println("Swipe right detected!");
                if(hero.isFacingLeft) {
                    hero.MeleeAttack(2);
                } else {
                    hero.MeleeAttack(1);
                }
            }
        }
        return true;
    }
}
