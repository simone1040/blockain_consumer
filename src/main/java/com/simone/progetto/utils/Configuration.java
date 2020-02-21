package com.simone.progetto.utils;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Configuration {
    public static final String UUID = java.util.UUID.randomUUID().toString();
    public static final String CONFIGURATION_PATH = "./receiver.conf";
    public static Integer MIN_NUMBER_OF_STEPS;
    public static Integer MAX_NUMBER_OF_STEPS;
    public static Integer MS_TIME_COMPUTE;
    public static Integer MIN_NONCE;
    public static Integer MAX_NONCE;
    public static String GENESIS_HASH = IntStream.range(0,20).mapToObj(i -> "F").collect(Collectors.joining(""));

    public static void Startup(){
        Properties configFile = new Properties();
        try{
            FileInputStream aoao = new FileInputStream(CONFIGURATION_PATH);
            configFile.load(aoao);
            Configuration.MIN_NUMBER_OF_STEPS = Integer.parseInt(configFile.getProperty("MIN_NUMBER_OF_STEPS").trim());
            Configuration.MAX_NUMBER_OF_STEPS = Integer.parseInt(configFile.getProperty("MAX_NUMBER_OF_STEPS").trim());
            Configuration.MS_TIME_COMPUTE = Integer.parseInt(configFile.getProperty("MS_TIME_COMPUTE").trim());
            Configuration.MIN_NONCE = Integer.parseInt(configFile.getProperty("MIN_NONCE").trim());
            Configuration.MAX_NONCE = Integer.parseInt(configFile.getProperty("MAX_NONCE").trim());
        }
        catch (Exception ex){
            MyLogger.getInstance().error(Configuration.class.getName(),
                    "Eccezione nella lettura della configurazione --> " + ex.toString(),ex);
            System.exit(1);
        }
    }
}
