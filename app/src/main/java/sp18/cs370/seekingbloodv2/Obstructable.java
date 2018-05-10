package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Obstructable implements GameObject {
    Animation animation;
    Bitmap bmp;
    Rect visualHitbox;
    Rect physicalHitbox;
    boolean isNotPhysical;
    boolean draw;

    Obstructable(Rect visualHitbox, Rect physicalHitbox, Bitmap bmp, boolean isNotPhysical) {
        this.visualHitbox = visualHitbox;
        this.physicalHitbox = physicalHitbox;
        this.bmp = bmp;
        this.isNotPhysical = isNotPhysical;
        this.draw = false;
    }

    Obstructable(Obstructable obstructable) {
        if(obstructable.animation != null)
            this.animation = new Animation(obstructable.animation);
        this.visualHitbox = obstructable.visualHitbox;
        this.physicalHitbox = obstructable.physicalHitbox;
        this.bmp = Bitmap.createBitmap(obstructable.bmp);
        this.isNotPhysical = obstructable.isNotPhysical;
        this.draw = obstructable.draw;
    }

    // Overridden GameObject methods

    @Override
    public void draw(Canvas canvas) {
        if(animation == null)
            canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), visualHitbox, null);
        else
            animation.play(visualHitbox, canvas);
    }

    @Override
    public void update() {
        if(animation != null)
            animation.update();
    }

}
