package com.example.partybauxserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The PartybauxserverApplication class compiles and runs the server code.
 */
@SpringBootApplication
public class PartybauxserverApplication {

	/**
	 * Compiles/runs the server, which creates the jar file needed to place the Spring application 
	 * onto the server
	 * @param args used for running the main method in Java application
	 */
	public static void main(String[] args) {
		SpringApplication.run(PartybauxserverApplication.class, args);
	}

}
