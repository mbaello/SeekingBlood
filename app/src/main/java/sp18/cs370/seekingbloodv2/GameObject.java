package sp18.cs370.seekingbloodv2;

import android.graphics.Canvas;

// Anything that can be considered a game object must implement this interface.
// The idea is that if it's a game object that is drawn to the screen, it must also
// implement the functions below.
public interface GameObject {
    public void draw(Canvas canvas);
    public void update();
}
