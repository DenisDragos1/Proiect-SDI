package ie.gmt.sw.job.server;

import org.example.DictionaryService;
import requests.RequestType;
import requests.Requestable;

import java.rmi.Naming;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class RmiClientThread implements Runnable {

    private BlockingQueue<Requestable> inQueue;
    private Map<Integer, Map<String, Object>> outQueue = new ConcurrentHashMap<>();
    private boolean keepRunning = true;

    public RmiClientThread(BlockingQueue<Requestable> inQueue, Map<Integer, Map<String, Object>> outQueue) {
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    public void run() {
        while (keepRunning) {
            try {
                Requestable request = inQueue.take();
                DictionaryService ds = (DictionaryService) Naming.lookup("rmi://127.0.0.1:1099/dictionaryService");

                Map<String, Object> details = new HashMap<>();

                if (request.getType().equals(RequestType.Lookup)) {
                    String result = ds.lookup((String) request.getData());
                    details.put("message", result);
                } else if (request.getType().equals(RequestType.Add)) {
                    Map<String, String> map = (Map<String, String>) request.getData();
                    Map<String, Object> resultMap = new HashMap<>();

                    for (String word : map.keySet()) {
                        Map<String, Object> result = ds.add(word, map.get(word));
                        resultMap.put(word, result);
                    }

                    details.put("message", resultMap);
                } else {
                    String result = ds.remove((String) request.getData());
                    details.put("message", result);
                }

                outQueue.put(request.getNumber(), details);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
