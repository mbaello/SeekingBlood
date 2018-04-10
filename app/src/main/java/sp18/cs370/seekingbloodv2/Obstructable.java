package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Obstructable implements GameObject {
    private Bitmap bmp;
    private Rect hitbox;
    private boolean horizontal;

    public Obstructable(Rect hitbox, Bitmap bmp, boolean horizontal) {
        this.hitbox = hitbox;
        this.bmp = bmp;
        this.horizontal = horizontal;
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public int getHitboxTop() {
        return hitbox.top;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    // Overridden GameObject methods

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), hitbox, null);
    }

    @Override
    public void update() {

    }

}
