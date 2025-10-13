package innowise.java.core;

import innowise.java.core.Factory.PartType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Faction implements Runnable {

    @Getter
    private final List<PartType> parts = new ArrayList<>();

    private static final int MAX_PART_GET = 5;

    private final Clock clock;

    private final Random r;

    private final int daysDuration;

    private final Factory factory;

    public Faction(Clock clock, int seed, int daysDuration, Factory factory) {
        this.clock = clock;
        r = new Random(seed);
        this.daysDuration = daysDuration;
        this.factory = factory;
    }

    @Override
    public void run() {
        for (int i = 0; i < daysDuration; i++) {
            clock.waitForStage(Clock.DayStage.NIGHT);
            List<PartType> taken = factory.sellParts(r.nextInt(0, MAX_PART_GET + 1));
            parts.addAll(taken);
            clock.waitForStage(Clock.DayStage.DAY);
        }
    }

    public int countRobots() {
        Map<PartType, Long> map = parts.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
        long heads = map.getOrDefault(PartType.HEAD, 0L);
        long torsos = map.getOrDefault(PartType.TORSO, 0L);
        long hands = map.getOrDefault(PartType.HAND, 0L) / 2;
        long feet = map.getOrDefault(PartType.FOOT, 0L) / 2;
        return (int) Math.min(Math.min(heads, torsos), Math.min(hands, feet));
    }
}
