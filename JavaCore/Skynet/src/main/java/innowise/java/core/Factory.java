package innowise.java.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Factory implements Runnable {

    @Getter
    private final List<PartType> parts = Collections.synchronizedList(new ArrayList<>());

    private final int MAX_PART_PRODUCE = 10;

    private final Clock clock;

    private final Random r;

    private final int daysDuration;

    public Factory(Clock clock, int seed, int daysDuration) {
        this.clock = clock;
        r = new Random(seed);
        this.daysDuration = daysDuration;
    }

    @Override
    public void run() {
        for (int i = 0; i < daysDuration; i++) {
            clock.waitForStage(Clock.DayStage.DAY);
            int partsCount = r.nextInt(0, MAX_PART_PRODUCE);
            for (int j = 0; j < partsCount; j++) {
                parts.add(getRandomPart());
            }
            clock.waitForStage(Clock.DayStage.NIGHT);
        }
    }

    public synchronized List<PartType> sellParts(int count) {
        List<PartType> result = new ArrayList<>(count);
        synchronized (parts) {
            int i = 0;
            while (i < count && !parts.isEmpty()) {
                result.add(parts.remove(r.nextInt(0, parts.size())));
            }
        }
        return result;
    }

    private PartType getRandomPart() {
        return PartType.values()[r.nextInt(0, PartType.values().length)];
    }

    public enum PartType {
        HEAD, TORSO, HAND, FOOT
    }
}
