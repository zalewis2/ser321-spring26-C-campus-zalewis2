import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Leader {

    static List<workerHelper> workerGroup = new CopyOnWriteArrayList<>();
    static ConcurrentHashMap<String,Integer> responseMap = new ConcurrentHashMap<>();

    public static void main(String args[]) {
        try {
            if (args.length != 1) {
                System.out.println("Usage: gradle runLeader -Pport=9000");
                System.exit(0);
            }
            int port = -1;
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.out.println("Port must be an integer");
                System.exit(2);
            }
            ExecutorService threadPool = Executors.newCachedThreadPool();

            ServerSocket serverSock = new ServerSocket(port);
            System.out.println("Leader ready and waiting for at least 3 workers");

            new Thread(() -> {
                while (true) {
                    try{
                        Socket workerSock = serverSock.accept();
                        System.out.println("Worker connected");
                        workerHelper helper = new workerHelper(workerSock, workerGroup, responseMap);
                        workerGroup.add(helper);
                        threadPool.execute(helper);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
            BufferedReader Br = new BufferedReader(new InputStreamReader(System.in));


            while(workerGroup.size() < 3){
                Thread.sleep(500);
            } // ^^^^^ need 3 or more for consensus

            System.out.println("Ready for to go with " + workerGroup.size() + " workers\n");


            while(true){
                System.out.print("Enter simple arithmetic problem (add OR subtract OR multiply)(example: 3 + 4): ");
                String task = Br.readLine();
                if(task.equals("quit")) { break; }
                responseMap.clear(); // <<<<< just to make sure no responses are left over

                List<workerHelper> consWorkers= new ArrayList<>(workerGroup);
                System.out.println("Now giving problem to all workers for a consensus.");
                for(workerHelper helper : consWorkers){
                    helper.messageHelper(messagesLeader.buildTask(task));
                }


                int timeout = 10; // only giving 10 seconds. proficiency testing :)
                int waitTime = 0;
                while(responseMap.size() < workerGroup.size() && waitTime < timeout)
                {
                    Thread.sleep(1000);
                    waitTime ++;
                    System.out.println("Waiting for " + workerGroup.size() + " workers");
                }

                System.out.println("Answers are in");
                for(Map.Entry<String,Integer> entry : responseMap.entrySet()){
                    System.out.println("Received from" + entry.getKey() + ": " + entry.getValue());
                }
                // need to build consensus logic
            }

            System.out.println("Leader has finished... Shutting down...");
            serverSock.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class workerHelper implements Runnable {
    private Socket workerSock;
    private String workerName;
    private ConcurrentHashMap<String,Integer> responseMap;
    private List<workerHelper> workerGroup;
    private BufferedReader in;
    private PrintWriter out;

    public workerHelper(Socket workerSock, List<workerHelper> workerGroup, ConcurrentHashMap<String,Integer> responseMap)
    {
        this.workerSock = workerSock;
        this.workerName = workerSock.getInetAddress() + ":" + workerSock.getPort(); // bug1 fix here
        this.workerGroup = workerGroup;
        this.responseMap = responseMap;
    }



    public void messageHelper(String message) {
        out.println(message);
    }

    public void run() {
        System.out.println("Worker " + workerName + " is now started");   // bug1: workerName is printing port

        try {
            out = new PrintWriter(workerSock.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(workerSock.getInputStream()));
            String incoming; // incoming json


            while((incoming = in.readLine()) != null){
                JSONObject obj = messagesLeader.parseResponse(incoming);
                String type = obj.getString("type");


                if(type.equals("REGISTER")){
                    workerName = obj.getString("name");
                    System.out.println(workerName + " connected from localhost:" + workerSock.getPort());
                } else if(type.equals("response"))
                {
                    int answer = obj.getInt("answer");
                    System.out.println("Answer from " + workerName + " " + answer); // watch print to see if correct
                    responseMap.put(workerName, answer);
                }
            }


        } catch (IOException e) {
            System.out.println("Cannot connect properly to " + workerName);
        }
        finally {
            workerGroup.remove(this);
            try {
                workerSock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



class messagesLeader{
    public static String buildTask(String problem) {
        JSONObject res = new JSONObject();
        res.put("type", "task");
        res.put("problem", problem);
        return res.toString();
    }

    public static String buildConsensus(int result, JSONObject votes) {
        JSONObject res = new JSONObject();
        res.put("type", "consensus");
        res.put("result", result);
        res.put("votes", votes);
        return res.toString();
    }

    public static String buildFailedConsensus(JSONObject votes) {
        JSONObject res = new JSONObject();
        res.put("type", "failedConsensus");
        res.put("votes", votes);
        return res.toString();
    }
    public static JSONObject parseResponse(String response) {
        return new JSONObject(response);
    }
}