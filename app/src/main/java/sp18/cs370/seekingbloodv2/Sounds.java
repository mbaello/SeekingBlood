package sp18.cs370.seekingbloodv2;

import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;

public class Sounds implements GameObject{
    SoundPool soundPool;
    int heroStab;
    int heroWalk;

    Sounds(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

    }

    Sounds(Sounds sounds) {
        this.soundPool = sounds.soundPool;
        this.heroStab = sounds.heroStab;
        this.heroWalk = sounds.heroWalk;
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void update() {

    }
}
