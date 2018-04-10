package sp18.cs370.seekingbloodv2;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
    public ArrayList<Obstructable> obstructables;

    public Game(Context context) {
        super(context);
        getHolder().addCallback(this);
        // Create a thread for this SurfaceView (Game)
        gameThread = new GameThread(getHolder(), this);
        // Create our Hero (This will be consolidated later to load 1 BMP per Entity
        hero = new Hero(new Rect((Constants.SCREENWIDTH * 20) / 100, ((Constants.SCREENHEIGHT * 50) / 100), // 50 was 80 was 70
                (Constants.SCREENWIDTH * 30) / 100, (Constants.SCREENHEIGHT * 70) / 100)); // Hitbox MUST match the sprite!
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_stand1)); // Needs to be converted to sprite sheet!
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_walk1));
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_walk2));
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_walk3));
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_walk4));
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_walk5));
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_walk6));
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_walk7));
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_walk8));
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_walk9));
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_walk10));
        hero.addBmp(BitmapFactory.decodeResource(getResources(), R.drawable.test_jump1));
        Constants.HEROHEIGHT = Constants.SCREENHEIGHT - ((Constants.SCREENHEIGHT * 80) / 100); // Store the Hero's height
        // Define the region boundaries and define a Gesture Detector
        fastMoveZoneR = new Rect(0, 0, Constants.SCREENWIDTH / 6, Constants.SCREENHEIGHT / 4);
        slowMoveZoneR = new Rect(0, Constants.SCREENHEIGHT / 4, Constants.SCREENWIDTH / 6, Constants.SCREENHEIGHT / 2);
        slowMoveZoneL = new Rect(0, Constants.SCREENHEIGHT / 2, Constants.SCREENWIDTH / 6, (3 * Constants.SCREENHEIGHT) / 4);
        fastMoveZoneL = new Rect(0, (3 * Constants.SCREENHEIGHT) / 4, Constants.SCREENWIDTH / 6, Constants.SCREENHEIGHT);
        actionZone = new Rect(Constants.SCREENWIDTH / 6, 0, Constants.SCREENWIDTH, Constants.SCREENHEIGHT);
        // Define an Obstructable (platform/wall)
        obstructables = new ArrayList<>();
        Bitmap obsBmp = BitmapFactory.decodeResource(getResources(), R.drawable.test_platform_asset);
        obstructables.add(new Obstructable(new Rect(0, Constants.SCREENHEIGHT - 10, Constants.SCREENWIDTH, Constants.SCREENHEIGHT + 1000),
                obsBmp, true)); // Floor
        obstructables.add(new Obstructable(new Rect(Constants.SCREENWIDTH / 2, (Constants.SCREENHEIGHT * 3) / 4,
                (Constants.SCREENWIDTH / 2) + 500, ((Constants.SCREENHEIGHT * 3) / 4) + 10), obsBmp, true)); // Platform
        obstructables.add(new Obstructable(new Rect(250, Constants.SCREENHEIGHT / 2, 750,
                (Constants.SCREENHEIGHT / 2) + 10), obsBmp, true)); // Platform
        obstructables.add(new Obstructable(new Rect(-1000, 0, 10, Constants.SCREENHEIGHT), obsBmp, false)); // Left Bounds
        obstructables.add(new Obstructable(new Rect(Constants.SCREENWIDTH - 10, 0, Constants.SCREENWIDTH + 1000, Constants.SCREENHEIGHT),
                obsBmp, false)); // Right Bounds
        // Create the Gesture Detector which aids in handling swipes
        gestureDetector = new GestureDetector(context, this);
        // View can receive focus (i.e. receive touch events?)
        setFocusable(false);
    }

    public void update() {
        hero.update(primaryZone, obstructables); // Update the Hero
        for(int i = 0; i < obstructables.size(); i++) // Update all Obstructables
            obstructables.get(i).update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        hero.draw(canvas);
        for(int i = 0; i < obstructables.size(); i++) // Draw all obstructables
            obstructables.get(i).draw(canvas);
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
                case MotionEvent.ACTION_POINTER_DOWN:
                    secondaryTouchX = (int)e.getX(1);
                    secondaryTouchY = (int)e.getY(1);
                case MotionEvent.ACTION_POINTER_UP:
                    int deltaX = abs((int) (secondaryTouchX - e.getX(1)));
                    int deltaY = abs((int) (secondaryTouchY - e.getY(1)));
                    if (deltaY > deltaX) { // Swipe is vertical
                        System.out.println("Swipe up detected!");
                        if (secondaryTouchY > e.getY(1) && hero.isOnGround())
                                hero.setStartJump(true);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    primaryZone = 0; // No movement
                    break;
            }
        } else
            return gestureDetector.onTouchEvent(e);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        System.out.println("onDown called!");
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
        int deltaX = abs((int)(e1.getX() - e2.getX()));
        int deltaY = abs((int)(e1.getY() - e2.getY()));
        if(deltaY > deltaX) { // Swipe is vertical
            if(e1.getY() > e2.getY()) {
                System.out.println("Swipe up detected!");
                if(hero.isOnGround())
                    hero.setStartJump(true);
            }
            else
                System.out.println("Swipe down detected!");
        } else { // Swipe must be horizontal
            if(e1.getX() > e2.getX())
                System.out.println("Swipe left detected!");
            else
                System.out.println("Swipe right detected!");
        }
        return true;
    }
}
