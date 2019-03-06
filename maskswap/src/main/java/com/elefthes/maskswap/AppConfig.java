package com.elefthes.maskswap;

import com.elefthes.maskswap.controller.AuthenticateEmail;
import com.elefthes.maskswap.controller.Conversion;
import com.elefthes.maskswap.controller.Login;
import com.elefthes.maskswap.controller.Registration;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

//@ApplicationPath("a")
/*public class AppConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<>();
        s.add(Login.class);
        s.add(Registration.class);
        s.add(AuthenticateEmail.class);
        s.add(Conversion.class);
        return s;
    }
}*/
@ApplicationPath("a")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        super(MultiPartFeature.class);
        packages("com.elefthes.maskswap.controller");
    }
}
