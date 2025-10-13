package innowise.java.core;

public class Emulator {
    public static void main(String[] args) throws InterruptedException {
        Clock clock = new Clock(Clock.DayStage.DAY);

        Factory factory = new Factory(clock, 42, 100);

        Faction world = new Faction(clock, 42, 100, factory);

        Faction wednesday = new Faction(clock, 42, 100, factory);

        Thread clockThread = new Thread(() -> {
            for (int i = 0; i < 200; i++) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (clock) {
                    clock.nextStage();
                }
            }
        });
        Thread factoryThread = new Thread(factory);
        Thread worldThread = new Thread(world);
        Thread wednesdayThread = new Thread(wednesday);

        clockThread.start();
        factoryThread.start();
        worldThread.start();
        wednesdayThread.start();

        factoryThread.join();
        worldThread.join();
        wednesdayThread.join();
        clockThread.join();

        System.out.println("Осталось деталей на фабрике: " + factory.getParts());
        System.out.println("Роботов World:" + world.countRobots());
        System.out.println("Роботов Wednesday:" + wednesday.countRobots());
        if(world.countRobots()>wednesday.countRobots()) {
            System.out.println("World win");
        } else {
            System.out.println("Wednesday win");
        }
    }
}
