package com.simone.progetto;
import com.simone.progetto.utils.MyLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReceiverProgettoApplication {

	public static void main(String[] args) {
		MyLogger.getInstance().info(ReceiverProgettoApplication.class.getName() + " - " + Constants.UUID,"Program started !");
		SpringApplication.run(ReceiverProgettoApplication.class, args);
	}


}
