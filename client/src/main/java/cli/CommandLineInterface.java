package cli;

import ie.gmit.sw.client.DictionaryClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommandLineInterface {
    private boolean authenticated = false;
    private List<String> nodes;  // Lista de noduri în sistem
    private DictionaryClient client;
    private Scanner scanner;
    private String currentServerIp = "192.168.1.2";  // Adresa IP a serverului curent


    public CommandLineInterface() {
        try {
            //  Adaugă adresa IP și portul serverului în constructorul DictionaryClient
            //client = new DictionaryClient("192.168.31.10", 1099);
            client = new DictionaryClient(currentServerIp, 1099);
            scanner = new Scanner(System.in);
            nodes = new ArrayList<>();

            // La prima rulare, inițializați lista de noduri
            //initializeNodes();
            // Adaugă adresa IP locală în lista de noduri
            try {
                String localIp = InetAddress.getLocalHost().getHostAddress();
                nodes.add(localIp);
                System.out.println("Local IP Address added: " + localIp);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Pornește un fir de execuție pentru afișarea continuă a listei de noduri
//        Thread nodeDisplayThread = new Thread(new NodeDisplayTask());
     //   nodeDisplayThread.start();
    }
    // Clasa Task pentru firul de execuție care afișează continuu lista de noduri
    private class NodeDisplayTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                displayNodes();
                try {
                    // Așteaptă 5 secunde înainte de a actualiza și afișa din nou lista
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void switchServer() {
        // Actualizează adresa IP a serverului curent (poate fi extins pentru a detecta serverul disponibil)
        currentServerIp = "192.168.1.17";
        System.out.println("Switching to server: " + currentServerIp);

        // Actualizează clientul cu noua adresă IP a serverului
        client.updateServer(currentServerIp);
    }


    private void displayNodes() {
        System.out.println("Current Nodes: " + nodes);
    }
    public void authenticateUser() {
        // Implementați autentificarea utilizatorului
        // Actualizați authenticated în funcție de rezultatul autentificării
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
            System.out.println("6. Adauga 1000 perechi chei valoare automat(fara confirmare)");
            System.out.println("7. Exit");

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
                    String editWord = scanner.nextLine();
                    System.out.print("Enter new definition: ");
                    String newDefinition = scanner.nextLine();
                    requestPermissionAndExecuteOperation(editWord, newDefinition, "edit");
                    break;
                case 5:
                    manageNodes();
                    break;
                case 6:
                    // Adaugă 1000 perechi cheie-valoare automat
                    for (int i = 0; i < 1000; i++) {
                        String key = "Key" + i;
                        String value = "Value" + i;
                        requestPermissionAndExecuteOperation(key, value, "add");
                    }
                    break;
                case 7:
                    client.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

//    private void requestPermissionAndExecuteOperation(String word, String definition, String operation) {
//        System.out.println("Requesting permission from nodes...");
//
//        // Solicită permisiunea de la fiecare nod
//        int permissionCount = 0;
//
//        // Pregătește un map pentru a stoca voturile de la noduri
//        Map<String, Boolean> votes = new HashMap<>();
//
//        for (String node : nodes) {
//            System.out.print("Node " + node + ": ");
//
//            // Afișează mesajul la nivelul CLI și permite utilizatorului să voteze
//            boolean vote = requestPermissionFromNode(node, operation);
//            votes.put(node, vote);
//
//            if (vote) {
//                permissionCount++;
//            }
//        }
//
//        // Verifică majoritatea voturilor
//        if (permissionCount > nodes.size() / 2) {
//            System.out.println("Majoritatea nodurilor au acordat permisiunea. Executând operația...");
//            // Execută operația
//            executeOperation(word, definition, operation);
//        } else {
//            System.out.println("Operația a fost respinsă de majoritatea nodurilor. Anulând...");
//        }
//
//        // Afișează voturile la nivelul CLI
//        displayVotes(votes);
//    }
private void requestPermissionAndExecuteOperation( String word, String definition, String operation) {
    System.out.println("Requesting permission from nodes...");

    try {
        // Solicită permisiunea de la fiecare nod
        int permissionCount = 0;
        Map<String, Boolean> votes = new HashMap<>();

        for (String node : nodes) {
            System.out.print("Node " + node + ": ");

            // Afișează mesajul la nivelul CLI și permite utilizatorului să voteze
            boolean vote = client.requestPermissionFromNode(node, operation);
            votes.put(node, vote);

            if (vote) {
                permissionCount++;
            }
        }

        // Verifică majoritatea voturilor
        if (permissionCount > nodes.size() / 2) {
            System.out.println("Majority of nodes have granted permission. Executing operation...");
            // Execută operația
            executeOperation(word, definition, operation);
        } else {
            System.out.println("Operation rejected by the majority of nodes. Cancelling...");
            // În cazul în care majoritatea nodurilor nu au acordat permisiunea, comută la alt server
            switchServer();
        }

        // Afișează voturile la nivelul CLI
        displayVotes(votes);
    } catch (Exception e) {
        e.printStackTrace();
        // În cazul în care apare o excepție, comută la alt server
        switchServer();
    }
}

    private void displayVotes(Map<String, Boolean> votes) {
        System.out.println("Votes received from nodes:");
        for (Map.Entry<String, Boolean> entry : votes.entrySet()) {
            System.out.println("Node " + entry.getKey() + ": " + (entry.getValue() ? "agreed" : "disagreed"));
        }
    }

    private boolean requestPermissionFromNode(String node, String operation) {
        // Simulează solicitarea de permisiune de la nod
        try {
            System.out.print("Do you agree? (yes/no): ");
            String response = scanner.nextLine();
            boolean vote = response.equalsIgnoreCase("yes");
            System.out.println("Vote from Node " + node + ": " + (vote ? "agreed" : "disagreed"));
            return vote;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




    //    private boolean requestPermissionFromNode(String node) {
//        // Simulează solicitarea de permisiune de la nod
//        try {
//            boolean vote = client.requestVote(node);
//            System.out.print("Do you agree? (yes/no): ");
//            String response = scanner.nextLine();
//            return vote && response.equalsIgnoreCase("yes");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//private boolean requestPermissionFromNode(String node) {
//    // Simulează solicitarea de permisiune de la nod
//    try {
//        boolean vote = client.requestVote(node, "operation");
//        System.out.println("Received vote from " + node + ": " + (vote ? "agreed" : "rejected"));
//        return vote;
//    } catch (Exception e) {
//        e.printStackTrace();
//        return false;
//    }
//}


    private void executeOperation(String word, String definition, String operation) {
        // Implementează execuția operației
        try {
            if (operation.equals("add")) {
                Map<String, Object> response = client.add(word, definition);
                displayResponse1(response);
            } else if (operation.equals("remove")) {
                displayResponse(client.remove(word));
            } else if (operation.equals("edit")) {
                Map<String, Object> response = client.edit(word, definition);
                displayResponse1(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
