package com.hardeymorlah.walletapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WalletapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletapiApplication.class, args);
	}

}
