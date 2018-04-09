package sp18.cs370.seekingbloodv2;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Obstructable{
    private Rect hitbox;

    public Obstructable(Rect hitbox) {
        this.hitbox = hitbox;
    }

    public int Collided(Hero hero) { // Checks to see if an Obstructable collides with the Hero (Will be Entity later)
        if(this.hitbox.contains(hero.getHitbox())) {
            System.out.println("Entity collision detected!");
            return 1;
        }
        return 0;
    }
/*
    @Override
    public void draw(Canvas canvas) {
        // Obstructables will be static for now...
    }

    @Override
    public void update() {
        // Obstructables will be static for now...
    }
*/
}
