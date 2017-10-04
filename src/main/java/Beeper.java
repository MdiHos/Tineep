import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.time.LocalTime;

class Beeper implements Runnable {

    private static final String HALF_BEEP_FILE_NAME = "half_beep.wav";
    private static final String FULL_BEEP_FILE_NAME = "full_beep.wav";
    private static final int CLIP_NUMBER_ZERO = 0;
    private static final int CLIP_NUMBER_ONE = 1;
    private Clip[] clips = new Clip[2];
    private State state;

    Beeper(State inf) {
        state = inf;
        initializeSounds();
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 👇 This is a design pattern and is called double-checked locking
                if (state.isMuted()) {
                    synchronized (Beeper.class) {
                        while (state.isMuted()) {
                            Beeper.class.wait();
                        }
                    }
                }
                if (LocalTime.now().getSecond() == 0) {
                    if (state.isHalfBeeping() && LocalTime.now().getMinute() == 30) {
                        play(CLIP_NUMBER_ZERO);
                    } else if (LocalTime.now().getMinute() == 0) {
                        play(CLIP_NUMBER_ONE);
                    }
                }
                Thread.sleep(800);
            }
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, "An error occurred in the beeper "
                    + "thread of the program.\nTry to restart the application and " +
                    "see if that helps.", "Error", JOptionPane
                    .ERROR_MESSAGE, Main.errIcon);
            System.exit(1);
        }
    }

    private void initializeSounds() {
        try {
            AudioInputStream audioIn1 = AudioSystem.getAudioInputStream
                    (ClassLoader.getSystemClassLoader().getResource
                            (HALF_BEEP_FILE_NAME));
            AudioInputStream audioIn2 = AudioSystem.getAudioInputStream
                    (ClassLoader.getSystemClassLoader().getResource
                            (FULL_BEEP_FILE_NAME));
            clips[0] = AudioSystem.getClip();
            clips[0].open(audioIn1);
            clips[1] = AudioSystem.getClip();
            clips[1].open(audioIn2);
        } catch (Exception e) {
            int chosenOption = JOptionPane.showConfirmDialog(null, "Error while "
                    + "initializing sounds. Press OK to continue or Cancel to " +
                    "exit the program", "Error", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.ERROR_MESSAGE, Main.errIcon);
            if (chosenOption == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Please note that the beeping " +
                        "" + "" + "" + "function is not working due to the file(s)" +
                        "" + "" + " " + "being corrupted.\nTry to fix the problem " +
                        "and " + "then " + "restart the program.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                System.exit(1);
            }
        }
    }

    private void play(int clipNumber) throws InterruptedException {
        clips[clipNumber].setFramePosition(0);
        clips[clipNumber].start();
        Thread.sleep(1000);
    }
}
