package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Animation {
    Bitmap leftBmp;
    Bitmap rightBmp;
    Rect source;
    boolean active;
    boolean facingLeft;
    int loop;
    int frame;
    int framesToSkip;
    int skipFrame;      // Will be equal to framesToSkip initially
    int maxFrame; // Should be "1 - TOTAL_FRAMES"
    int frameWidth;
    int frameHeight;

    Animation(Bitmap leftBmp, Bitmap rightBmp, int totalFrames) {
        this.leftBmp = Bitmap.createBitmap(leftBmp);
        this.rightBmp = Bitmap.createBitmap(rightBmp);
        this.active = false;
        this.facingLeft = false;
        this.loop = 0;
        this.frame = 0;
        this.framesToSkip = 0;
        this.skipFrame = 0;
        this.maxFrame = totalFrames - 1;
        this.frameWidth = rightBmp.getWidth();
        this.frameHeight = rightBmp.getHeight() / totalFrames;
        this.source = new Rect(0, 0, frameWidth - 1, frameHeight - 1);
    }

    Animation(Animation animation) {
        this.leftBmp = Bitmap.createBitmap(animation.leftBmp);
        this.rightBmp = Bitmap.createBitmap(animation.rightBmp);
        this.active = animation.active;
        this.facingLeft = animation.facingLeft;
        this.loop = animation.loop;
        this.frame = animation.frame;
        this.framesToSkip = animation.framesToSkip;
        this.skipFrame = animation.skipFrame;
        this.maxFrame = animation.maxFrame;
        this.frameWidth = animation.frameWidth;
        this.frameHeight = animation.frameHeight;
        this.source = animation.source;
    }

    public void update() {
        if(!active)
            active = true;
        if(framesToSkip == 0)
            frame++;
        else {
            if(skipFrame == framesToSkip) {
                skipFrame = 0;
                frame++;
            } else
                skipFrame++;
        }
        if(frame > maxFrame) {
            frame = 0;
        }
        // Need to make inactive and reset frames
    }

    public void update(Hero hero) {
        if(hero.isFacingLeft)
            facingLeft = true;
        else
            facingLeft = false;
        this.update();
    }

    public void update(Enemy enemy) {
        if(enemy.isFacingLeft)
            facingLeft = true;
        else
            facingLeft = false;
        this.update();
    }

    public void play(Rect dest, Canvas canvas) {
        // System.out.println("[ANIMATION] Frame = " + frame + ", Max Frame = " + maxFrame);
        Rect tempSource = new Rect(source.left, source.top + (frame * frameHeight), source.right, source.bottom + (frame * frameHeight));
        if(facingLeft)
            canvas.drawBitmap(leftBmp, tempSource, dest, null);
        else
            canvas.drawBitmap(rightBmp, tempSource, dest, null);
    }

    public void reset() {
        if(active) {
            active = false;
            frame = 0;
        }
    }

}
