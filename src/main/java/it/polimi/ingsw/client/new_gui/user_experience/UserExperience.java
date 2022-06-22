package it.polimi.ingsw.client.new_gui.user_experience;

import javafx.scene.CacheHint;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

public class UserExperience {
    public static void playSound(Media sound) {
        new MediaPlayer(sound).play();
    }

    public static void playSoundLoop(Media sound) {
        var mp = new MediaPlayer(sound);
        mp.setOnEndOfMedia(() -> mp.seek(Duration.ZERO));
        mp.play();
    }

    public static void doDownUpEffect(ImageView imgView, int offset, int msDelay) {
        var y = imgView.getY();
        imgView.setY(y + offset);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                imgView.setY(y);
            }
        }, msDelay);
    }

    public static void setImageViewBrightness(ImageView imageView, double brightness) {
        ColorAdjust bright = new ColorAdjust();
        bright.setBrightness(brightness);
        imageView.setEffect(bright);
        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);
    }
}
