package com.simone.progetto.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class MyLogger {

    private static MyLogger instance = null;

    private MyLogger() {
        super();
        PropertyConfigurator.configure("/etc/receiverLog4j.properties");
    }

    public static MyLogger getInstance(){
        if(instance  == null){
            instance  = new MyLogger();
        }
        return instance;
    }

    public void info(String myclass, String msg) {
        Logger.getLogger(myclass).info(msg);
    }

    public void error(String myclass, String msg, Exception ce) {
        Logger.getLogger(myclass).error(msg, ce);
    }

    public void warning(String myclass, String msg) {
        Logger.getLogger(myclass).warn(msg);
    }

    public void debug(String myclass, String msg) {
        Logger.getLogger(myclass).debug(msg);
    }
}