package innowise.java.core.test;

import innowise.java.core.annotations.Component;
import innowise.java.core.annotations.Scope;
import innowise.java.core.interfaces.InitializingBean;

import java.util.Random;

@Component()
@Scope("prototype")
public class RandomRepository implements InitializingBean {

    Random r;

    @Override
    public void afterPropertiesSet() {
        r = new Random(42);
    }

    public int load() {
        return r.nextInt();
    }
}
