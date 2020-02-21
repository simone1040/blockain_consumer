package com.simone.progetto.bean;

import com.simone.progetto.Receiver;
import com.simone.progetto.syncroRequestCode;
import com.simone.progetto.SyncroQueue;
import com.simone.progetto.utils.InsertChainSemaphore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
    public InsertChainSemaphore InsertChainSemaphore() {
        return new InsertChainSemaphore();
    }
}
