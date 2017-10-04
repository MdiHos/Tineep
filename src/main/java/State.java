import java.io.Serializable;
import java.time.LocalDateTime;

class State implements Serializable {

    private LocalDateTime muteTime;
    private boolean clockVisible = true;
    private boolean halfBeeping = true;
    private boolean muted = false;

    State() {
        muteTime = LocalDateTime.now();
        muted = false;
        clockVisible = true;
        halfBeeping = true;
    }

    boolean isClockVisible() {
        return clockVisible;
    }

    void setClockVisibility() {
        clockVisible = !clockVisible;
    }

    LocalDateTime getMuteTime() {
        return muteTime;
    }

    void setMuteTime(LocalDateTime time) {
        muteTime = time;
    }

    synchronized boolean isHalfBeeping() {
        return halfBeeping;
    }

    synchronized void setHalfBeeping() {
        halfBeeping = !halfBeeping;
    }

    synchronized boolean isMuted() {
        return muted;
    }

    synchronized void setMuted(boolean state) {
        muted = state;
    }
}
