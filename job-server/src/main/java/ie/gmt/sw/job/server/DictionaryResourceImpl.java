package ie.gmt.sw.job.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Singleton;
import javax.ws.rs.Path;

import ie.gmt.sw.job.server.DictionaryResource;
import ie.gmt.sw.job.server.RmiClientThread;
import requests.Request;
import requests.RequestType;
import requests.Requestable;

@Path("/dictionary")
@Singleton
public class DictionaryResourceImpl implements DictionaryResource {

    private static final int NUMBER_OF_THREADS = 10;
    private int counter = 0;
    private BlockingQueue<Requestable> inQueue = new LinkedBlockingQueue<Requestable>();
    private Map<Integer, Map<String, Object>> outQueue = new ConcurrentHashMap<Integer, Map<String, Object>>();
    private ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public DictionaryResourceImpl() {
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            Runnable worker = new RmiClientThread(inQueue, outQueue);
            executor.execute(worker);
        }
    }

    @Override
    public Map<String, Object> getLookupRequest(String query) throws InterruptedException {
        Requestable<String> request = new Request<String>(RequestType.Lookup, query, counter);
        inQueue.put(request);

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("message", String.format("Searching for '%s'...", query));
        response.put("number", counter);

        counter++;

        return response;
    }

    @Override
    public Map<String, Object> postAddRequest(String word, String definition) throws InterruptedException {
        Map<String, String> data = new HashMap<String, String>();
        data.put(word, definition);
        Requestable<Map<String, String>> request = new Request<Map<String, String>>(RequestType.Add, data, counter);
        inQueue.put(request);

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("message", String.format("Adding '%s'...", word));
        response.put("number", counter);

        counter++;

        return response;
    }

    @Override
    public Map<String, Object> deleteRemoveRequest(String word) throws InterruptedException {
        Requestable<String> request = new Request<String>(RequestType.Remove, word, counter);
        inQueue.put(request);

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("message", String.format("Removing '%s'...", word));
        response.put("number", counter);

        counter++;

        return response;
    }

    @Override
    public Map<String, Object> getPollRequest(int number) {
        Map<String, Object> details = outQueue.remove(number);

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("ready", details != null);
        response.put("details", details);

        return response;
    }
}
