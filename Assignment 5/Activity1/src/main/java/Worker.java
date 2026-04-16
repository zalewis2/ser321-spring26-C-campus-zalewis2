import java.net.*;
import java.io.*;
import org.json.JSONObject;

public class Worker {
    public static void main(String args[]) {
        try {
            if (args.length != 3) {
                System.out.println("Usage: gradle runWorker -PworkerName=Worker1 -Phost=localhost -Pport=9000");
                System.exit(0);
            }
            String workerName = args[0];
            String host = args[1];
            int port = -1;
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                System.out.println("[Port] must be an integer");
                System.exit(2);
            }

            Socket leaderSock = new Socket(host, port);
            System.out.println(workerName + " connected to leader at " + host + ":" + port);

            PrintWriter out = new PrintWriter(leaderSock.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(leaderSock.getInputStream()));
            BufferedReader Br = new BufferedReader(new InputStreamReader(System.in));

            JSONObject rej = new JSONObject();
            rej.put("type", "REGISTER");
            rej.put("name", workerName);
            out.println(rej.toString());

            // thread sleep here so the connection stays open
//            System.out.println(workerName + " waiting...");
//            Thread.sleep(5000);

            String incoming;
            while((incoming = in.readLine()) != null){
                JSONObject obj = messages.parseResponse(incoming);
                String type = obj.getString("type");

                if(type.equals("task")){
                    String problem = obj.getString("problem");
                    System.out.println("\nTask received" + problem);
                    System.out.println("Enter your answer: ");
                    String answer = Br.readLine();

                    int ans = Integer.parseInt(answer);
                    System.out.println(messages.buildResponse(workerName, ans));
                    System.out.println("Result submitted to leader.");
                    System.out.println("Waiting for next task...");


                } else if (type.equals("consensus")) {
                    int result = obj.getInt("result");
                    int count = obj.getInt("count");
                    int total = obj.getInt("total");

                    JSONObject votes = obj.getJSONObject("votes");

                    System.out.println("\nConsensus announced: " + result + " (" + count
                            + "/" + total + " workers agreed)"); // i love formatting strings

                    if (votes.has(String.valueOf(result))) {
                        System.out.println("You voted with the majority!");
                    } else {
                        System.out.println("You voted with the minority.");
                    }

                } else if (type.equals("failedConsensus")) {
                    JSONObject voteCount = obj.getJSONObject("votes");
                    System.out.println("\nNo consensus reached.");
                    System.out.println("Vote distribution: " + voteCount);
                }


            }

            leaderSock.close();
            System.out.println(workerName + " disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class messages{

    public static String buildResponse(String workerName, int answer) {
        JSONObject res = new JSONObject();
        res.put("type", "response");
        res.put("answer", answer);
        res.put("workerName", workerName);
        return res.toString();
    }

    public static JSONObject parseResponse(String response) {
        return new JSONObject(response);
    }
}