package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Obstructable implements GameObject {
    private Bitmap bmp;
    private Rect hitbox;
    boolean isNotPhysical;

    public Obstructable(Rect hitbox, Bitmap bmp) {
        this.hitbox = hitbox;
        this.bmp = bmp;
        this.isNotPhysical = false;
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rect hitbox) {
        this.hitbox = hitbox;
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
