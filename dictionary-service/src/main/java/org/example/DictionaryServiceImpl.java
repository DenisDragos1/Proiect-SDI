package org.example;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryServiceImpl extends UnicastRemoteObject implements DictionaryService {
    private static final long serialVersionUID = 1L;
    private Map<String, Boolean> votes = new ConcurrentHashMap<>();
    private Set<String> activeOperations = new HashSet<>();
    private Map<String, String> dictionary = new HashMap<>();

    public DictionaryServiceImpl(Map<String, String> dictionary) throws RemoteException {
        super();
        this.dictionary = dictionary;
    }

    @Override
    public String lookup(String s) throws RemoteException {

        sleep();
        s = s.toUpperCase();
        String definition = dictionary.get(s);
        if (definition == null) {
            definition = "String not found";
        }
        return definition;
    }

    @Override
    public Map<String, Object> add(String word, String def,String clientIp) throws RemoteException {
        sleep();
        word = word.toUpperCase();
        Map<String, Object> response = new HashMap<>();
        if (dictionary.containsKey(word)) {
            response.put("message", String.format("Modified the definition for the word '%s' in the dictionary.", word));
        } else {
            response.put("message", String.format("Successfully added '%s' to the dictionary.", word));
        }
        dictionary.put(word, def);
        return response;
    }

    @Override
    public Map<String, Object> edit(String word, String newDefinition,String clientIp) throws RemoteException {
        sleep();
        word = word.toUpperCase();
        Map<String, Object> response = new HashMap<>();
        if (dictionary.containsKey(word)) {
            dictionary.put(word, newDefinition);
            response.put("message", String.format("Successfully edited the definition for the word '%s' in the dictionary.", word));
        } else {
            response.put("message", String.format("Could not find the word '%s' in the dictionary for editing.", word));
        }
        return response;
    }

    @Override
    public String remove(String word,String clientIp) throws RemoteException {
        sleep();
        word = word.toUpperCase();
        String definition = dictionary.remove(word);
        if (definition != null) {
            return String.format("Successfully removed '%s' from the dictionary.", word);
        } else {
            return String.format("Could not find the word '%s' in the dictionary.", word);
        }
    }

//    @Override
//    public boolean requestVote(String clientIp, String operation) throws RemoteException {
//        // Simulează logica de votare
//        System.out.println("Received vote request from " + clientIp + " for operation: " + operation);
//        // Returnează true dacă este de acord, altfel false
//        // Poți adăuga o logica mai avansată aici
//        return true; // Modifică logica în funcție de cerințele tale.
//    }
@Override
public boolean requestVote(String clientIp, String operationId) throws RemoteException {
    // sincronizare adaugată aici...
    synchronized (this) {
        if (activeOperations.contains(operationId)) {
            return false;
        }

        System.out.println("Received vote request from " + clientIp + " for operation: " + operationId);
        boolean vote = true;
        votes.put(clientIp + ":" + operationId, vote);
        return vote;
    }
}

    @Override
    public Map<String, Boolean> getVotes() throws RemoteException {
        // sincronizare adaugată aici...
        synchronized (this) {
            return new HashMap<>(votes);
        }
    }

    @Override
    public void startOperation(String operationId) throws RemoteException {
        // sincronizare adaugată aici...
        synchronized (this) {
            activeOperations.add(operationId);
        }
    }

    @Override
    public void endOperation(String operationId) throws RemoteException {
        // sincronizare adaugată aici...
        synchronized (this) {
            activeOperations.remove(operationId);
        }
    }


    @Override
    public boolean isReady() throws RemoteException {
        // Implementează logica pentru a verifica dacă serviciul este pregătit
        return true; // Modifică logica în funcție de cerințele tale.
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
