import javax.swing.*;
import java.io.*;

public class Main {

    private static final String STATE_FILE_NAME = "prop.dat";
    static final ImageIcon errIcon = new ImageIcon(Main.getImage("err_icon.png"));

    public static void main(String[] args) {
        try {
            Thread.currentThread().setName("Main");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            State state = getInitialState();
            new MainFrame(state);
            Thread beeperThread = new Thread(new Beeper(state), "Beeper");
            beeperThread.setPriority(Thread.MAX_PRIORITY);
            beeperThread.start();
            new Thread(new Animator(), "Animator").start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error in the application...\nTry " +
                    "" + "" + "to restart the program and see if that helps.",
                    "Error", JOptionPane.ERROR_MESSAGE, errIcon);
            System.exit(1);
        }
    }

    private static State getInitialState() {
        State state;
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream
                (STATE_FILE_NAME))) {
            state = (State) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Cannot read initialization state " +
                    "from the file '" + STATE_FILE_NAME + "'"
                    + ".\nTineep " + "will " + "load the default settings.",
                    "Error", JOptionPane.ERROR_MESSAGE, Main.errIcon);
            state = new State();
        }
        return state;
    }

     static byte[] getImage(String path) {
        InputStream inputStream = Main.class.getResourceAsStream(path);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int readBytesCount;
        byte[] data = new byte[16384];
        try {
            while ((readBytesCount = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, readBytesCount);
            }
            buffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }
}
