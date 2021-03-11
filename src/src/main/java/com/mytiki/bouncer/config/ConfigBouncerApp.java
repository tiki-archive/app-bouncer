package com.mytiki.bouncer.config;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

public class ConfigBouncerApp {

    @PostConstruct
    void starter(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
