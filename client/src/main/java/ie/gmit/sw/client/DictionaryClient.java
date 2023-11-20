package ie.gmit.sw.client;

import org.example.DictionaryService;

import java.rmi.Naming;
import java.util.HashMap;
import java.util.Map;

public class DictionaryClient {

    private DictionaryService service;


    private Map<String, Object> dictionary; //  pentru a defini dicționarul

    public DictionaryClient() {
        try {
            // Aici se face lookup pentru serviciul RMI
            service = (DictionaryService) Naming.lookup("rmi://127.0.0.1:1099/dictionaryService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String lookup(String query) {
        try {
            return service.lookup(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public String add(String word, String definition) {
//        try {
//            return service.add(word, definition);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
public Map<String, Object> add(String word, String definition) {
    try {
        return service.add(word, definition);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

//edit
public Map<String, Object> edit(String word, String newDefinition) {
    // Implementează logica pentru editarea unui cuvânt
    // De exemplu, poți verifica dacă cuvântul există și apoi să-l editezi

    Map<String, Object> response = new HashMap<>();
    if (dictionary.containsKey(word)) {
        dictionary.put(word, newDefinition);
        response.put("message", "Word edited successfully.");
    } else {
        response.put("message", "Word not found for editing.");
    }

    return response;
}


    public String remove(String word) {
        try {
            return service.remove(word);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        // Implementează logica de închidere a clientului aici (dacă este necesar)
    }
}
