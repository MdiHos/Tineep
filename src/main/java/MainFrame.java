import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;

class MainFrame extends JFrame {

    private static final int HOUR_1 = 1;
    private static final int HOUR_2 = 2;
    private static final int HOUR_4 = 4;
    private static final JLabel timeLabel = new JLabel();
    private JMenuItem[] menuItems = new JMenuItem[7];
    private JCheckBoxMenuItem halfBeepCheckBox;
    private TrayIcon trayIcon;
    private JMenu muteMenu;
    private State state;

    MainFrame(State inf) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setType(Type.UTILITY);
        state = inf;
        try {
            initComponents();
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(null, "Cannot initialize the " +
                    "application" + " components.", "Error", JOptionPane
                    .ERROR_MESSAGE, Main.errIcon);
            System.exit(1);
        }
        if (state.isMuted()) {
            startUnmuter();
        }
        makeHandlers();
        makeLayout();
        setFocusableWindowState(false);
        setBackground(new Color(0, 0, 0, 125));
        setVisible(state.isClockVisible());
    }

    private void initComponents() throws AWTException {
        timeLabel.setFont(new Font("Monofonto", Font.PLAIN, 16));
        timeLabel.setForeground(Color.WHITE);
        menuItems[0] = new JMenuItem("for 1 hour", new ImageIcon(Main.getImage("1h.png")));
        menuItems[1] = new JMenuItem("for 2 hours", new ImageIcon(Main.getImage("2h.png")));
        menuItems[2] = new JMenuItem("for 4 hours", new ImageIcon(Main.getImage("4h.png")));
        menuItems[3] = new JMenuItem("Unmute", new ImageIcon(Main.getImage("unmute.png")));
        menuItems[3].setVisible(state.isMuted());
        menuItems[4] = new JMenuItem((state.isClockVisible() ? "Visible" :
                "Hidden") + " at start");
        menuItems[4].setIcon(new ImageIcon(Main.getImage(state.isClockVisible() ? "clock" +
                ".png" : "clock_dis.png")));
        menuItems[5] = new JMenuItem("OK", new ImageIcon(Main.getImage("ok.png")));
        menuItems[6] = new JMenuItem("Exit", new ImageIcon(Main.getImage("exit.png")));
        muteMenu = new JMenu("Mute");
        muteMenu.add(menuItems[0]);
        muteMenu.add(menuItems[1]);
        muteMenu.add(menuItems[2]);
        halfBeepCheckBox = new JCheckBoxMenuItem("Half-beep (30s)");
        halfBeepCheckBox.setSelected(state.isHalfBeeping());
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(muteMenu);
        popupMenu.add(menuItems[3]);
        popupMenu.add(halfBeepCheckBox);
        popupMenu.add(menuItems[4]);
        popupMenu.add(menuItems[5]);
        popupMenu.add(menuItems[6]);
        SystemTray tray = SystemTray.getSystemTray();
        Image trayImage;
        trayImage = Toolkit.getDefaultToolkit().createImage(Main.getImage(state.isMuted() ?
                "mute_icon.png" : "tray_icon.gif"));
        trayIcon = new TrayIcon(trayImage, "Double click to " + (state
                .isClockVisible() ? "hide" : "show") + " the clock");
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.setLocation(e.getX(), e.getY());
                    popupMenu.setInvoker(popupMenu);
                    popupMenu.setVisible(true);
                }
            }
        });
        trayIcon.addActionListener(e -> {
            setVisible(!isVisible());
            trayIcon.setToolTip("Double click to " + (isVisible() ? "hide" :
                    "show") + " the " + "clock");
        });
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);
    }

    private void makeHandlers() {
        halfBeepCheckBox.addActionListener(e -> {
            state.setHalfBeeping();
            storeAppData();
        });
        menuItems[0].addActionListener(e -> mute(HOUR_1));
        menuItems[1].addActionListener(e -> mute(HOUR_2));
        menuItems[2].addActionListener(e -> mute(HOUR_4));
        menuItems[3].addActionListener(e -> unmute());
        menuItems[4].addActionListener(e -> {
            state.setClockVisibility();
            menuItems[4].setText((state.isClockVisible() ? "Visible" : "Hidden") +
                    " at start");
            menuItems[4].setIcon(new ImageIcon(Main.getImage(state.isClockVisible() ?
                    "clock" + ".png" : "clock_dis.png")));
            storeAppData();
        });
        menuItems[6].addActionListener(e -> System.exit(0));
    }

    private void storeAppData() {
        try (ObjectOutputStream stream = new ObjectOutputStream(new
                FileOutputStream("prop.dat"))) {
            stream.writeObject(state);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving the state of the " +
                    "program.\nYour settings may be reset to their default "
                    + "values in the next execution.", "Error", JOptionPane
                    .ERROR_MESSAGE, Main.errIcon);
        }
    }

    static void setTimeLabel(String time) {
        timeLabel.setText(" " + time);
    }

    private void mute(int duration) {
        state.setMuted(true);
        state.setMuteTime(LocalDateTime.now().plusHours(duration));
        storeAppData();
        startUnmuter();
    }

    private void startUnmuter() {
        muteMenu.setVisible(false);
        halfBeepCheckBox.setEnabled(false);
        String muteText = "muted till " + state.getMuteTime().toString().substring
                (11, 16);
        menuItems[3].setText("Unmute (" + muteText + ")");
        menuItems[3].setVisible(true);
        trayIcon.setImage(Toolkit.getDefaultToolkit().createImage(Main.getImage("mute_icon" +
                ".png")));
        new Thread(() -> {
            while (LocalDateTime.now().compareTo(state.getMuteTime()) < 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(null, "Error in the unmuter " +
                            "thread of the " + "program.\nUnmuting now...",
                            "Error", JOptionPane.ERROR_MESSAGE, Main.errIcon);
                    unmute();
                }
            }
            unmute();
        }).start();
    }

    private void unmute() {
        state.setMuted(false);
        storeAppData();
        menuItems[3].setVisible(false);
        muteMenu.setVisible(true);
        halfBeepCheckBox.setEnabled(true);
        trayIcon.setImage(Toolkit.getDefaultToolkit().createImage(Main.getImage("tray_icon" +
                ".gif")));
        synchronized (Beeper.class) {
            Beeper.class.notifyAll();
        }
    }

    private void makeLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(timeLabel, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(timeLabel, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE));
        pack();
    }
}
