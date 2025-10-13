package innowise.java.core.test;

import innowise.java.core.annotations.Autowired;
import innowise.java.core.annotations.Component;

@Component
public class IntService {

    @Autowired
    private IntRepository rep;

    public int getInt() {
        return rep.loadInt();
    }
}
