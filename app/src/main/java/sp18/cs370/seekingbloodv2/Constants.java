package sp18.cs370.seekingbloodv2;

import android.media.SoundPool;

public class Constants {
    // Screen width and height for relative coordinate placement
    public static int SCREENWIDTH;
    public static int SCREENHEIGHT;
    // Constants related to movement
    public static int HEROSLOWMOVE = 2;
    public static int HEROFASTMOVE = 5;
    public static int HEROJUMPVELOCITY = -35;
    public static int ENEMYSLOWMOVE = 2;
    public static int ENEMYFASTMOVE = 4;
    public static int KNOCKBACKVELOCITY = 15;
    public static int GRAVITY = 2;
    public static int HERODASHDISTANCE = 150;
    // Global Soundpool available to all classes
    public static SoundPool sounds;
    public static int soundIndex[];
}
