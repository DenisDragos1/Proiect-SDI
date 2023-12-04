package ie.gmit.sw.client;

import org.example.DictionaryService;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryClient {
    private Map<String, Boolean> votes = new ConcurrentHashMap<>();

    private DictionaryService service;
    private Scanner scanner;
    private String clientIp;

    public DictionaryClient(String serverIp, int port) {
        try {
            // Aici se face lookup pentru serviciul RMI
            service = (DictionaryService) Naming.lookup("rmi://" + serverIp + ":" + port + "/dictionaryService");
            scanner = new Scanner(System.in);
            clientIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String lookup(String query) {
        try {
            return service.lookup(query);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> add(String word, String definition) {
        try {
            return service.add(word, definition, clientIp);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> edit(String word, String newDefinition) {
        try {
            return service.edit(word, newDefinition, clientIp);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String remove(String word) {
        try {
            return service.remove(word, clientIp);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }



    public synchronized boolean requestVote(String clientIp, String operation) throws RemoteException {
        // Simulează logica de votare
        System.out.println("Received vote request from " + clientIp + " for operation: " + operation);

        // Stabilește votul în funcție de o logică simplă (poți adapta aceasta în funcție de cerințe)
        boolean vote = !operation.equals("add"); // Acceptă orice altă operație în afară de "add"

        // Stochează votul pentru clientIp
        votes.put(clientIp, vote);

        // Returnează votul
        return vote;
    }


    public synchronized Map<String, Boolean> getVotes() throws RemoteException {
        // Returnează o copie a voturilor
        return new HashMap<>(votes);
    }



    public boolean isReady() {
        try {
            return service.isReady();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        // Implementează logica de închidere a clientului aici (dacă este necesar)
        scanner.close();
    }
}
