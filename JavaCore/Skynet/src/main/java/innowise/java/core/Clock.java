package innowise.java.core;

import lombok.Getter;


@Getter
public class Clock {

    private DayStage now;

    public Clock(DayStage now) {
        this.now = now;
    }

    public synchronized void nextStage() {
        now = (now == DayStage.DAY) ? DayStage.NIGHT : DayStage.DAY;
        notifyAll();
    }

    public synchronized void waitForStage(DayStage stage) {
        while (now != stage) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public enum DayStage {
        DAY, NIGHT
    }
}
