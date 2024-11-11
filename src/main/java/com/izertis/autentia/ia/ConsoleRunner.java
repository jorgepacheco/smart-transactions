package com.izertis.autentia.ia;

import com.izertis.autentia.ia.config.BankAgent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public final class ConsoleRunner implements CommandLineRunner {

    private final BankAgent bankAgent;

    public ConsoleRunner(BankAgent bankAgent) {
        this.bankAgent = bankAgent;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Bank Assistant! Type 'exit' to quit.");

        while (true) {
            System.out.print("User: ");
            String userMessage = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userMessage)) {
                break;
            }

            String agentMessage = bankAgent.chat(userMessage);
            System.out.println("Bank Agent: " + agentMessage);
            System.out.println("****************************************************");
        }

        scanner.close();
    }
}
