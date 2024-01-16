// VotingManager.java

package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VotingManager {
    private Map<String, Map<String, Boolean>> wordVotes = new HashMap<>();
    private Map<String, Boolean> currentVotes = new HashMap<>();

    public boolean requestVote(String node, String word, String operation) {
        // Solicită un vot de la fiecare nod în mod real
        boolean vote = requestRealVoteFromNode(node, word, operation);

        // Stochează votul în map-ul corespunzător cuvântului
        wordVotes.putIfAbsent(word, new HashMap<>());
        wordVotes.get(word).put(node, vote);

        // Verifică dacă majoritatea nodurilor au fost de acord
        return processVotes(word);
    }

    private boolean requestRealVoteFromNode(String node, String word, String operation) {
        // Solicită un vot de la nodul specific și întoarce rezultatul
        System.out.print("Node " + node + ": Do you agree with the operation? (yes/no): ");

        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine().toLowerCase();

        boolean vote = response.equals("yes");
        currentVotes.put(node, vote);

        return vote;
    }

    private boolean processVotes(String word) {
        // Calculează dacă majoritatea nodurilor au fost de acord
        Map<String, Boolean> votesForWord = wordVotes.get(word);
        if (votesForWord == null) {
            return false; // Nu există voturi pentru cuvântul specific
        }

        long agreedVotes = votesForWord.entrySet().stream().filter(Map.Entry::getValue).count();
        long totalVotes = votesForWord.size();

        return agreedVotes > totalVotes / 2;
    }

    public void resetVotes(String word) {
        // Resetează voturile pentru cuvântul specific după ce s-a luat o decizie
        wordVotes.remove(word);
        currentVotes.clear();
    }

    public Map<String, Boolean> getVotesForWord(String word) {
        // Returnează voturile pentru cuvântul specific
        return wordVotes.getOrDefault(word, new HashMap<>());
    }

    public Map<String, Boolean> getCurrentVotes() {
        // Returnează voturile curente (pentru vizualizare)
        return currentVotes;
    }
}
