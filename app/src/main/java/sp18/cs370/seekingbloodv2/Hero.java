package sp18.cs370.seekingbloodv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Hero implements GameObject {
    private Bitmap bmp;
    private Rect hitbox;
    private Sprite sprite;
    private boolean canUpdate;
    private boolean onGround;
    private boolean startJump;
    private boolean finishedJump;
    private float stamina;
    private float reserve;
    private int xVelocity;
    private int yVelocity;

    public Hero(Rect hitbox) {
        System.out.println("Constructing Hero!");
        this.hitbox = hitbox;
        this.startJump = false;
        this.onGround = true;
        this.xVelocity = 0;
        this.yVelocity = Constants.HEROJUMPVELOCITY;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rect hitbox) {
        this.hitbox = hitbox;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isStartJump() {
        return startJump;
    }

    public void setStartJump(boolean startJump) {
        this.startJump = startJump;
    }

    // Overridden GameObject methods

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), hitbox, null);
    }

    @Override
    // Any horizontal movement will change the left and right bounds of the hitbox.
    // Any vertical movement will change the top and bottom bounds of the hitbox.
    public void update() {
        if(startJump) {
            startJump = false;
            hitbox = new Rect(hitbox.left, hitbox.top + yVelocity, hitbox.right, hitbox.bottom + yVelocity);
            yVelocity += Constants.GRAVITY;
            setOnGround(false);
            System.out.println("Starting jump! Hero top = " + hitbox.top + "; Hero bottom = " + hitbox.bottom);
        } else if(!onGround) {
            // Hard coding touching the floor
            if((hitbox.bottom + yVelocity) > Constants.SCREENHEIGHT) {
                int bottom = Constants.SCREENHEIGHT;
                hitbox = new Rect(hitbox.left, bottom - Constants.HEROHEIGHT, hitbox.right, bottom);
                setOnGround(true);
                yVelocity = Constants.HEROJUMPVELOCITY;
                System.out.println("Touched Ground! Hero top = " + hitbox.top + "; Hero bottom = " + hitbox.bottom);
            } else {
                hitbox = new Rect(hitbox.left, hitbox.top + yVelocity, hitbox.right, hitbox.bottom + yVelocity);
                yVelocity += Constants.GRAVITY;
                System.out.println("yVelocity = " + yVelocity);
                System.out.println("Airborne! Hero top = " + hitbox.top + "; Hero bottom = " + hitbox.bottom);
            }
        }
    }

    public void update(int zone) {
        this.update();
        if(zone == 1) // Sprint Right
            hitbox = new Rect(hitbox.left + Constants.HEROFASTMOVE, hitbox.top, hitbox.right + Constants.HEROFASTMOVE, hitbox.bottom);
        else if(zone == 2) // Walk Right
            hitbox = new Rect(hitbox.left + Constants.HEROSLOWMOVE, hitbox.top, hitbox.right + Constants.HEROSLOWMOVE, hitbox.bottom);
        else if(zone == 3) // Walk Left
            hitbox = new Rect(hitbox.left - Constants.HEROSLOWMOVE, hitbox.top, hitbox.right - Constants.HEROSLOWMOVE, hitbox.bottom);
        else if(zone == 4) // Sprint Left
            hitbox = new Rect(hitbox.left - Constants.HEROFASTMOVE, hitbox.top, hitbox.right - Constants.HEROFASTMOVE, hitbox.bottom);
    }
}
