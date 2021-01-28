package com.simone.progetto.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class ReceiverConfiguration {
    public static final String UUID = java.util.UUID.randomUUID().toString();
    public static final String CONFIGURATION_PATH = "receiver.conf";
    public static Integer MIN_NUMBER_OF_STEPS;
    public static Integer MAX_NUMBER_OF_STEPS;
    public static Integer MS_TIME_COMPUTE;
    public static Integer MIN_NONCE;
    public static Integer MAX_NONCE;
    public static String IP_ADDRESS_SERVER_RABBIT;
    public static String GENESIS_HASH = IntStream.range(0,20).mapToObj(i -> "F").collect(Collectors.joining(""));

    public static void Startup(){
        Properties configFile = new Properties();
        try{
            InputStream file = new ClassPathResource(CONFIGURATION_PATH).getInputStream();
            configFile.load(file);
            ReceiverConfiguration.MIN_NUMBER_OF_STEPS = Integer.parseInt(configFile.getProperty("MIN_NUMBER_OF_STEPS").trim());
            ReceiverConfiguration.MAX_NUMBER_OF_STEPS = Integer.parseInt(configFile.getProperty("MAX_NUMBER_OF_STEPS").trim());
            ReceiverConfiguration.MS_TIME_COMPUTE = Integer.parseInt(configFile.getProperty("MS_TIME_COMPUTE").trim());
            ReceiverConfiguration.MIN_NONCE = Integer.parseInt(configFile.getProperty("MIN_NONCE").trim());
            ReceiverConfiguration.MAX_NONCE = Integer.parseInt(configFile.getProperty("MAX_NONCE").trim());
            ReceiverConfiguration.IP_ADDRESS_SERVER_RABBIT = configFile.getProperty("IP_ADDRESS").trim();
        }
        catch (Exception ex){
            log.info("{" + ReceiverConfiguration.UUID + "} Exception in read configuration --> " + ex.toString());
            System.exit(1);
        }
    }
}
