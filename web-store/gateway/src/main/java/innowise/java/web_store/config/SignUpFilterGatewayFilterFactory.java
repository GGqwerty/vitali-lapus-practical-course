package innowise.java.web_store.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class SignUpFilterGatewayFilterFactory
        extends AbstractGatewayFilterFactory<SignUpFilterGatewayFilterFactory.Config> {

    private final SignUpFilter signUpGatewayFilter;

    public SignUpFilterGatewayFilterFactory(SignUpFilter signUpGatewayFilter) {
        super(Config.class);
        this.signUpGatewayFilter = signUpGatewayFilter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return signUpGatewayFilter;
    }

    public static class Config {

    }
}
