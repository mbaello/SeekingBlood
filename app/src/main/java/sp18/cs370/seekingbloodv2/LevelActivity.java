package sp18.cs370.seekingbloodv2;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class LevelActivity extends Activity {

    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the width and height of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Constants.SCREENWIDTH = displayMetrics.widthPixels;
        Constants.SCREENHEIGHT = displayMetrics.heightPixels;

        // Create a Soundpool containing BGM and sound effects
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attrs = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            Constants.sounds = new SoundPool.Builder().setMaxStreams(20).setAudioAttributes(attrs).build();
        } else {
            Constants.sounds = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
        }
        // Load the sounds into soundIndex to be referred to later by sounds
        Constants.soundIndex = new int[20];
        Constants.soundIndex[0] = Constants.sounds.load(this, R.raw.footstep, 1);
        Constants.soundIndex[1] = Constants.sounds.load(this, R.raw.sword_stab, 1);
        Constants.soundIndex[2] = Constants.sounds.load(this, R.raw.dash, 1);
        Constants.soundIndex[3] = Constants.sounds.load(this, R.raw.phasad_was_hit, 1);
        Constants.soundIndex[4] = Constants.sounds.load(this, R.raw.phasad_was_killed, 1);
        Constants.soundIndex[5] = Constants.sounds.load(this, R.raw.hero_was_hit, 1);
        Constants.soundIndex[6] = Constants.sounds.load(this, R.raw.hero_was_hit_by_stomp, 1);
        Constants.soundIndex[7] = Constants.sounds.load(this, R.raw.scahtur_was_hit, 1);
        Constants.soundIndex[8] = Constants.sounds.load(this, R.raw.scahtur_was_killed, 1);
        Constants.soundIndex[9] = Constants.sounds.load(this, R.raw.pause, 1);
        Constants.soundIndex[10] = Constants.sounds.load(this, R.raw.boss_bgm, 1);
        Constants.soundIndex[11] = Constants.sounds.load(this, R.raw.scahture_stomps, 1);
        /**
         * Play sound effects using the following format:
         *      Constants.sounds.play(Constants.soundIndex[i], 1, 1, 1, 0, 1.0);, where:
         *      1st param (i) = the index of the particular sound
         *      2nd param = left-volume range (0.0 to 1.0)
         *      3rd param = right-volume range (0.0 to 1.0)
         *      4th param = priority (0.0 to 1.0)
         *      5th param = loop (0 will not loop, but -1 will loop)
         *      6th param = playback speed (0.5 to 2.0)
         */
        // Allow the user to control volume levels
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        System.out.println("Initializing game...");
        game = new Game(this);
        System.out.println("Game has loaded!");

        // Set the content view
        setContentView(game);
    }
}
