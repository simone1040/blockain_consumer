package com.simone.progetto;
import com.simone.progetto.utils.Configuration;
import com.simone.progetto.utils.MyLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReceiverProgettoApplication {

	public static void main(String[] args) {
		Configuration.Startup();
		MyLogger.getInstance().info(ReceiverProgettoApplication.class.getName() + " - " + Configuration.UUID,"Program started !");
		SpringApplication.run(ReceiverProgettoApplication.class, args);
	}
}
