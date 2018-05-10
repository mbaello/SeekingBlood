package sp18.cs370.seekingbloodv2;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameThread extends Thread {
    static final int MAX_FPS = 50;
    double FPS;
    SurfaceHolder surfaceHolder;
    Game game;
    boolean loading;
    boolean running;
    static Canvas canvas;
    int pauseTimer = 0;
    // ExecutorService pool = Executors.newFixedThreadPool(1);

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public GameThread(SurfaceHolder surfaceHolder, Game game) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.game = game;
        this.loading = false; // Needs to be true for splash
    }

    @Override
    public void run() {
        long drawStartTime;
        long drawWaitTime;
        long drawTimeDelay;
        long drawTargetTime = 20; // Delay
        int frameCount = 0;
        long totalTime = 0;
        /*
        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long updateStartTime;
                long updateWaitTime;
                long updateTimeDelay;
                long updateTargetTime = 20; // Delay
                while(true) {
                    updateStartTime = System.nanoTime(); // Gets the system time in nano seconds
                    game.update();      // Updates the game internally/logically
                    updateTimeDelay = (System.nanoTime() - updateStartTime) / 1000000;
                    updateWaitTime = updateTargetTime - updateTimeDelay;
                    try {
                        if(updateWaitTime > 0) { // Checks to see if the operations were completed fast enough
                            sleep(updateWaitTime); // If they were, cause the thread to sleep to throttle FPS
                        }
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        });

        if(!updateThread.isAlive()) {
            System.out.println("Running UpdateThread!");
            pool.execute(updateThread);
        }
        */
        while(true) {
            drawStartTime = System.nanoTime(); // Gets the system time in nano seconds
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    // Code in here is executed approximately 50 times per second (i.e. 50 FPS)
                    if(!loading) {
                        game.update();      // Updates the game internally/logically
                        game.draw(canvas);  // Updates the screen
                        if (pauseTimer > 0)
                            pauseTimer--;
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {e.printStackTrace();}
                }
            }
            drawTimeDelay = (System.nanoTime() - drawStartTime) / 1000000;
            drawWaitTime = drawTargetTime - drawTimeDelay;
            if(!running) {
                synchronized (this) {
                    while(!running)
                        try {
                            wait(20);
                            if(pauseTimer < 25)
                                pauseTimer++;
                            else
                                pauseTimer = 25;
                        } catch (Exception e) {e.printStackTrace();}
                }
                pauseTimer = 25;
            }
            try {
                if(drawWaitTime > 0) { // Checks to see if the operations were completed fast enough
                    sleep(drawWaitTime); // If they were, cause the thread to sleep to throttle FPS
                }
            } catch(Exception e) {e.printStackTrace();}
            totalTime += System.nanoTime() - drawStartTime;
            frameCount++;
            if(frameCount == MAX_FPS) {
                FPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println("FPS = " + FPS);
            }
        }
    }
}
