package innowise.java.core.test;

import innowise.java.core.annotations.Component;

@Component
public class IntRepository {
    public int loadInt() {
        return 42;
    }
}
