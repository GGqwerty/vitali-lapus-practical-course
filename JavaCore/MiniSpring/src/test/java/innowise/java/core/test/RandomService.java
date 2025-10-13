package innowise.java.core.test;

import innowise.java.core.annotations.Autowired;
import innowise.java.core.annotations.Component;
import lombok.Getter;

@Component
public class RandomService {
    @Autowired
    @Getter
    private RandomRepository r1;

    @Autowired
    @Getter
    private RandomRepository r2;
}
