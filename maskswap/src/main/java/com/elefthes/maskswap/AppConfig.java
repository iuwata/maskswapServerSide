package com.elefthes.maskswap;

import com.elefthes.maskswap.controller.Login;
import com.elefthes.maskswap.controller.Registration;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("a")
public class AppConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<>();
        s.add(Login.class);
        s.add(Registration.class);
        return s;
    }
}
