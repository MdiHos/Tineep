import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class Animator implements Runnable {

    private static final int SLEEP_INTERVAL = 40;
    private static final int CLOCK_LENGTH = 8;
    private static final String PAD = "   ";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern
            ("HH:mm:ss");
    private static StringBuilder time = new StringBuilder();
    private static StringBuilder day = new StringBuilder(PAD);

    @Override
    public void run() {
        try {
            while (true) {
                bringTime();
                setDay();
                bringDay();
                Thread.sleep(850);
            }
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, "An error occurred in the " +
                    "animator thread of the program.\nTry to restart the " +
                    "application and see if that helps.", "Error", JOptionPane
                    .ERROR_MESSAGE, Main.errIcon);
            System.exit(1);
        }
    }

    private static void bringTime() throws InterruptedException {
        for (int i = CLOCK_LENGTH - 1; i >= 0; i--) {
            setTime(i, true);
            if (i == 7) {
                MainFrame.setTimeLabel(PAD + day + " " + time);
            } else if (i == 6) {
                MainFrame.setTimeLabel(PAD + day + time);
            } else if (i < 6 && i > 2) {
                MainFrame.setTimeLabel(PAD + day.substring(0, i - 3) + time);
            } else {
                MainFrame.setTimeLabel(PAD.substring(0, i) + time);
            }
            Thread.sleep(SLEEP_INTERVAL);
        }
        // continue to show the clock for 5 seconds (while updating it meanwhile!)
        for (int i = 0; i < 10; i++) {
            setTime(8, false);
            MainFrame.setTimeLabel(time.toString());
            Thread.sleep(500);
        }
    }

/*
    private static void bringTime2() throws InterruptedException {
        for (int count = 0; count < CLOCK_LENGTH; count++) {
            StringBuilder spaces = new StringBuilder();
            setTime(count, false);
            if (count < 4) {
                for (int i = 0; i < 3 - count; i++) {
                    spaces.append(" ");
                }
                MainFrame.setTimeLabel(time + spaces.toString() + day);
            } else if (count > 3 && count < 7) {
                MainFrame.setTimeLabel(time + day.substring(count - 3, 3));
            } else {
                MainFrame.setTimeLabel(time.toString());
            }
            Thread.sleep(SLEEP_INTERVAL);
        }
        for (int i = 0; i < 10; i++) {
            setTime(8, false);
            MainFrame.setTimeLabel(time.toString());
            Thread.sleep(500);
        }
    }
*/

    private static void setTime(final int head, boolean fromEnd) {
        if (fromEnd) {
            time.replace(0, time.length(), now().substring(head));
        } else {
            time.replace(0, time.length(), now().substring(0, head));
        }
    }

    private static String now() {
        return LocalTime.now().format(FORMATTER);
    }

    private static void bringDay() throws InterruptedException {
        for (int count = CLOCK_LENGTH; count >= 0; count--) {
            setTime(count, false);
            if (count < 6 && count > 2) {
                MainFrame.setTimeLabel(time + day.substring(count - 3, 3));
            } else if (count < 3) {
                StringBuilder spaces = new StringBuilder();
                for (int i = 3; i > count; i--) {
                    spaces.append(" ");
                }
                MainFrame.setTimeLabel(time.toString() + spaces + day);
            } else {
                MainFrame.setTimeLabel(time.toString());
            }
            Thread.sleep(SLEEP_INTERVAL);
        }
    }

    private static void setDay() {
        String today = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle
                .SHORT, Locale.getDefault());
        day.replace(0, day.length(), today);
    }
}
