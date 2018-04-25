package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class HUDElement implements GameObject{
    Bitmap mainRegionBmp;
    Bitmap secondaryRegionBmp;
    Rect mainRegion;
    Rect secondaryRegion;
    int tag; // 1 = health, 2 = stamina, 3 = reserve

    HUDElement(Rect mainRegion, int tag) {
        this.mainRegion = mainRegion;
        this.secondaryRegion = mainRegion;
        this.tag = tag;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(mainRegionBmp, new Rect(0, 0, mainRegionBmp.getWidth(), mainRegionBmp.getHeight()), mainRegion, null);
        canvas.drawBitmap(secondaryRegionBmp, new Rect(0, 0, secondaryRegionBmp.getWidth(), secondaryRegionBmp.getHeight()), secondaryRegion, null);
    }

    @Override
    public void update() {

    }

    public void update(Hero hero) {
        switch(tag) {
            case 1:
                mainRegion = new Rect(secondaryRegion.left, secondaryRegion.top, secondaryRegion.left + (int)(((secondaryRegion.right - secondaryRegion.left) * hero.health) / 100),
                        secondaryRegion.bottom);
                break;
            case 2:
                mainRegion = new Rect(secondaryRegion.left, secondaryRegion.top, secondaryRegion.left + (int)(((secondaryRegion.right - secondaryRegion.left) * hero.reserve) / 50),
                        secondaryRegion.bottom);
                break;
            case 3:
                mainRegion = new Rect(secondaryRegion.left, secondaryRegion.top, secondaryRegion.left + (int)(((secondaryRegion.right - secondaryRegion.left) * hero.stamina) / 100),
                        secondaryRegion.bottom);
                break;
            default:
                break;
        }
        this.update();
    }
}
