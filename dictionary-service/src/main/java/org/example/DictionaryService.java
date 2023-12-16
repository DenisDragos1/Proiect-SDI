package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * An interface that exposes the public service methods that may be invoked by a remote object.
 * All remote methods must throw a RemoteException.
 * In RMI, a class that implements a remote interface is called a Remote Object.
 */
public interface DictionaryService extends Remote {

    /**
     * Find the definition of a string in the dictionary.
     * @param s the string to lookup in the dictionary.
     * @return either the dictionary definition of s or the text "String not found".
     * @throws RemoteException
     */
    public String lookup(String s) throws RemoteException;

    /**
     * Add the word and definition to the dictionary if it doesn't exist. If it exists, update the definition.
     * @param word to add to the dictionary.
     * @param definition of the word.
     * @return a message to show the user.
     * @throws RemoteException
     */
//    public String add(String word, String definition) throws RemoteException;
  //  public Map<String, Object> add(String word, String definition) throws RemoteException;
    public Map<String, Object> add(String word, String definition,String clientIp) throws RemoteException;

    /**
     * Remove the given word from the dictionary if it exists.
     * @param// word to remove from the dictionary.
     * @return a message to show the user.
     * @throws RemoteException
     */
    // Adaugă această metodă pentru a permite votarea.
    public boolean requestVote(String clientIp, String operation) throws RemoteException;


    // Adaugă această metodă pentru a verifica dacă un nod este pregătit.
    boolean isReady() throws RemoteException;


    public String remove(String word,String clientIp) throws RemoteException;

    //public Map<String, Object> edit(String word, String newDefinition);
    Map<String, Object> edit(String word, String newDefinition,String clientIp) throws RemoteException;

    //boolean requestVote(String clientIp, String operationId) throws RemoteException;
    Map<String, Boolean> getVotes() throws RemoteException;
    void startOperation(String operationId) throws RemoteException;
    void endOperation(String operationId) throws RemoteException;
}
