package com.example.transfer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EntityScan(basePackages = "com.example.transfer_service")
public class TransferServiceApplication {

	public static void main(String[] args) {
		System.out.println("-------- Transfer Service Application Started --------");
		ConfigurableApplicationContext context = SpringApplication.run(TransferServiceApplication.class, args);
		UPITransferService upiTransferService = context.getBean(UPITransferService.class);
		// This method can be used to execute code after the application has started
		try {
			upiTransferService.initiateTransfer("1234567890", "0987654321", 100.00);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("-------- Transfer Service Application Started --------");
	}

}
