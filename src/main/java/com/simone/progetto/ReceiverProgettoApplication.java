package com.simone.progetto;
import com.simone.progetto.utils.ReceiverConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReceiverProgettoApplication {


	public static void main(String[] args) {
		ReceiverConfiguration.Startup();
		SpringApplication.run(ReceiverProgettoApplication.class, args);
	}
}
