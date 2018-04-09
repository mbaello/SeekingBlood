package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;

public class Sprite {
    int x, y;
    int xSpeed, ySpeed;
    int height, width;
    Bitmap bmp;
    MainActivity activity;

    public Sprite(MainActivity activity, Bitmap bmp) {
        this.bmp = bmp;
        this.activity = activity;
        this.height = this.bmp.getHeight();
        this.width = this.bmp.getWidth();
        x = y = 0;
        xSpeed = 5;
        ySpeed = 0;
    }
}
