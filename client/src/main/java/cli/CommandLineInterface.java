package cli;

import ie.gmit.sw.client.DictionaryClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CommandLineInterface {
    private boolean authenticated = false;
    private List<String> nodes;  // Lista de noduri în sistem
    private DictionaryClient client;
    private Scanner scanner;

    public CommandLineInterface() {
        client = new DictionaryClient();
        scanner = new Scanner(System.in);
        nodes = new ArrayList<>();

        // La prima rulare, inițializați lista de noduri
        initializeNodes();
    }

    public void authenticateUser() {
        // Implementați autentificarea utilizatorului
        // Actualizați authenticated în funcție de rezultatul autentificării
    }

    public void initializeNodes() {
        try {
            // Adăugați adresa IP a calculatorului curent în lista de noduri
            String localIp = InetAddress.getLocalHost().getHostAddress();
            nodes.add(localIp);
            System.out.println("Adresa IP locală a fost adăugată: " + localIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        // Autentificarea utilizatorului la prima rulare
        authenticateUser();

        while (true) {
            if (!authenticated) {
                // Dacă utilizatorul nu este autentificat, solicită autentificarea
                authenticateUser();
            }

            System.out.println("Choose an option:");
            System.out.println("1. Lookup");
            System.out.println("2. Add");
            System.out.println("3. Remove");
            System.out.println("4. Edit");
            System.out.println("5. Manage Nodes");
            System.out.println("6. Exit");

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
                    requestPermissionAndExecuteOperation(addWord, addDefinition, "add");
                    break;
                case 3:
                    System.out.print("Enter word to remove: ");
                    String removeWord = scanner.nextLine();
                    requestPermissionAndExecuteOperation(removeWord, "", "remove");
                    break;
                case 4:
                    System.out.print("Enter word to edit: ");
                    // Implementează funcționalitatea de editare
                    break;
                case 5:
                    manageNodes();
                    break;
                case 6:
                    client.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void requestPermissionAndExecuteOperation(String word, String definition, String operation) {
        System.out.println("Requesting permission from nodes...");

        // Solicită permisiunea de la fiecare nod
        int permissionCount = 0;
        for (String node : nodes) {
            System.out.print("Node " + node + ": ");
            if (requestPermissionFromNode(node)) {
                permissionCount++;
            }
        }

        // Verifică majoritatea voturilor
        if (permissionCount > nodes.size() / 2) {
            System.out.println("Majoritatea nodurilor au acordat permisiunea. Executând operația...");
            // Execută operația
            executeOperation(word, definition, operation);
        } else {
            System.out.println("Operația a fost respinsă de majoritatea nodurilor. Anulând...");
        }
    }

    private boolean requestPermissionFromNode(String node) {
        // Simulează solicitarea de permisiune de la nod
        System.out.print("Do you agree? (yes/no): ");
        String response = scanner.nextLine();
        return response.equalsIgnoreCase("yes");
    }

    private void executeOperation(String word, String definition, String operation) {
        // Implementează execuția operației
        if (operation.equals("add")) {
            displayResponse1(client.add(word, definition));
        } else if (operation.equals("remove")) {
            displayResponse(client.remove(word));
        } else {
            // Implementează execuția operației de editare
        }
    }

    private void manageNodes() {
        System.out.println("Manage Nodes:");
        System.out.println("1. Add Node");
        System.out.println("2. Remove Node");
        System.out.println("3. Back");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consumă newline

        switch (choice) {
            case 1:
                addNode();
                break;
            case 2:
                removeNode();
                break;
            case 3:
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void addNode() {
        try {
            // Adaugă adresa IP a calculatorului curent în lista de noduri
            String localIp = InetAddress.getLocalHost().getHostAddress();
            nodes.add(localIp);
            System.out.println("Node added: " + localIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void removeNode() {
        // Implementează funcționalitatea de eliminare a unui nod
        // Poți permite utilizatorului să introducă adresa IP sau să afișezi lista de noduri și să aleagă din ea
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
