package sp18.cs370.seekingbloodv2;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private static final int MAX_FPS = 50;
    private double FPS;
    private SurfaceHolder surfaceHolder;
    private Game game;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private boolean running;
    public static Canvas canvas;

    public GameThread(SurfaceHolder surfaceHolder, Game game) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.game = game;
    }

    @Override
    public void run() {
        long startTime;
        long timeDelay = 20; // Delay
        long waitTime;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 20; // Delay

        while(running) {
            startTime = System.nanoTime(); // Gets the system time in nano seconds
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    // Code in here is executed approximately 50 times per second (i.e. 50 FPS)
                    game.update();      // Updates the game internally/logically
                    game.draw(canvas);  // Updates the screen
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {e.printStackTrace();};
                }
            }
            timeDelay = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeDelay;
            try {
                if(waitTime > 0) { // Checks to see if the operations were completed fast enough
                    sleep(waitTime); // If they were, cause the thread to sleep to throttle FPS
                }
            } catch(Exception e) {e.printStackTrace();}
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            /*
            if(frameCount == MAX_FPS) {
                FPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println("FPS = " + FPS);
            }
            */
        }
    }
}
