package com.simone.progetto.bean;

import com.simone.progetto.Receiver;
import com.simone.progetto.syncroRequestCode;
import com.simone.progetto.SyncroQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Semaphore;

@Configuration
public class BeanConfiguration {
    @Bean
    public Receiver receiver() {
        return new Receiver();
    }

    @Bean
    public SyncroQueue syncroQueue() {
        return new SyncroQueue();
    }

    @Bean
    public syncroRequestCode syncroRequestCode() {
        return new syncroRequestCode();
    }

    @Bean
    public Semaphore getSemaphore(){
        return new Semaphore(1);
    }
}
