package cli;

import ie.gmit.sw.client.DictionaryClient;

import java.util.Map;
import java.util.Scanner;

public class CommandLineInterface {

    private DictionaryClient client;
    private Scanner scanner;

    public CommandLineInterface() {
        client = new DictionaryClient();
        scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Lookup");
            System.out.println("2. Add");
            System.out.println("3. Remove");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumă newline

            switch (choice) {
                case 1:
                    System.out.print("Enter word to lookup: ");
                    String lookupQuery = scanner.nextLine();
                    displayResponse(client.lookup(lookupQuery));
                    break;
                case 2:
                    System.out.print("Enter word to add: ");
                    String addWord = scanner.nextLine();
                    System.out.print("Enter definition: ");
                    String addDefinition = scanner.nextLine();
                   displayResponse1(client.add(addWord, addDefinition));
                    break;
                case 3:
                    System.out.print("Enter word to remove: ");
                    String removeWord = scanner.nextLine();
                    displayResponse(client.remove(removeWord));
                    break;
                case 4:
                    client.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayResponse(String response) {
        System.out.println("Response: " + response);
    }
    private void displayResponse1(Map<String, Object> response) {
        String message = (String) response.get("message");
        System.out.println("Response: " + message);
    }

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.start();
    }
}
