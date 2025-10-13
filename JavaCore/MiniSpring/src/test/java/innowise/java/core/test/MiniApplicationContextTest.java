package innowise.java.core.test;

import innowise.java.core.MiniApplicationContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MiniApplicationContextTest {

    private static MiniApplicationContext context;

    @BeforeAll
    public static void setup() {
        context = new MiniApplicationContext("innowise.java.core.test");
    }

    @Test
    public void testIntService() {
        IntService intService = context.getBean(IntService.class);
        assertNotNull(intService, "IntService must be created");
        int value = intService.getInt();
        assertEquals(42, value, "IntService.getInt() must return 42");
    }

    @Test
    public void testRandomRepositoryPrototype() {
        RandomRepository r1 = context.getBean(RandomRepository.class);
        RandomRepository r2 = context.getBean(RandomRepository.class);

        assertNotNull(r1, "RandomRepository r1 must be created");
        assertNotNull(r2, "RandomRepository r2 must be created");

        assertNotEquals(r1, r2, "r1 and r2 must be different (prototype)");

        Random r = new Random(42);
        int intValue = r.nextInt();

        assertEquals(intValue, r1.load(), "Check r1 afterPropertiesSet()");
        assertEquals(intValue, r2.load(), "Check r2 afterPropertiesSet()");
    }

    @Test
    public void testRandomServiceDependencies() {
        RandomService randomService = context.getBean(RandomService.class);
        assertNotNull(randomService, "RandomService must be created");
        assertNotNull(randomService.getR1(), "r1 must be created");
        assertNotNull(randomService.getR2(), "r2 must be created");

        assertNotEquals(randomService.getR1(), randomService.getR2(), "r1 and r2 must be different");

        RandomRepository newR = context.getBean(RandomRepository.class);
        assertNotEquals(randomService.getR1(), newR);
        assertNotEquals(randomService.getR2(), newR);
    }
}
