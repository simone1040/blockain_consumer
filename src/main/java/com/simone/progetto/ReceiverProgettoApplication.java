package com.simone.progetto;
import com.simone.progetto.utils.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReceiverProgettoApplication {


	public static void main(String[] args) {
		Configuration.Startup();
		SpringApplication.run(ReceiverProgettoApplication.class, args);
	}
}
