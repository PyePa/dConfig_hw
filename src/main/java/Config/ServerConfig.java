package Config;

import org.aeonbits.owner.Config;


@Config.Sources("classpath:config.properties")
public interface ServerConfig extends Config {
    @Key("login")
    String login();
    @Key("password")
    String password();
}
