
import mysteryservice.MysteryService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.*;
import java.io.*;
import java.util.Scanner;


/**
 */
class SockClient {
  static Socket sock = null;
  static String host = "localhost";
  static int port = 8888;
  static OutputStream out;
  // Using and Object Stream here and a Data Stream as return. Could both be the same type I just wanted
  // to show the difference. Do not change these types.
  static ObjectOutputStream os;
  static DataInputStream in;
  public static void main (String args[]) {

    if (args.length != 2) {
      System.out.println("Expected arguments: <host(String)> <port(int)>");
      System.exit(1);
    }

    try {
      host = args[0];
      port = Integer.parseInt(args[1]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port|sleepDelay] must be an integer");
      System.exit(2);
    }

    try {
      connect(host, port); // connecting to server
      System.out.println("Client connected to server.");
      boolean requesting = true;
      while (requesting) {
        System.out.println("What would you like to do: 1 - echo, 2 - add, " +
                "3 - string concatenation," + "\n 4 - calculatemany " +
                "5 - playlist, 6 - analyzer (0 to quit)");
        Scanner scanner = new Scanner(System.in);
        int choice = Integer.parseInt(scanner.nextLine());
        // You can assume the user put in a correct input, you do not need to handle errors here
        // You can assume the user inputs a String when asked and an int when asked.
        // So you do not have to handle user input checking
        JSONObject json = new JSONObject(); // request object
        switch(choice) {
          case 0:
            System.out.println("Choose quit. Thank you for using our services. Goodbye!");
            requesting = false;
            break;
          case 1:
            System.out.println("Choose echo, which String do you want to send?");
            String message = scanner.nextLine();
            json.put("type", "echo");
            json.put("data", message);
            break;
          case 2:
            System.out.println("Choose add, enter first number:");
            String num1 = scanner.nextLine();
            json.put("type", "add");
            json.put("num1", num1);

            System.out.println("Enter second number:");
            String num2 = scanner.nextLine();
            json.put("num2", num2);
            break;
          case 3:
            System.out.println("Choose string concatenation, enter first string:");
            String str1 = scanner.nextLine();
            System.out.println("Enter second string:");
            String str2 = scanner.nextLine();
            json.put("type", "stringconcatenation");
            json.put("string1", str1);              // "str1" to "string1" ? fix? yesbug (3)
            json.put("string2", str2);
            break;
          case 4:
          System.out.println("Choose an operation, add, multiply or average:");
          String operation = scanner.nextLine();

            if (!operation.equals("add") && !operation.equals("multiply")
                                                              && !operation.equals("average")) {
              System.out.println("Invalid operation. Valid operations are add, multiply, average");
              break;
          }

            System.out.println("How many numbers?");
            int count = Integer.parseInt(scanner.nextLine());

            JSONArray numList = new JSONArray();
            for (int i = 0; i < count; i++) {
              System.out.println("Enter number " + (i+1) + ":");
              numList.put(Integer.parseInt(scanner.nextLine()));

            }

            json.put("type", "calculatemany");
            json.put("operation", operation);
            json.put("numList", numList);
            break;

          case 5:
            System.out.println("Choose a playlist action, add, remove, list or clear");
            String action = scanner.nextLine();

            if(!action.equals("add") && !action.equals("remove")
                        && !action.equals("list") && !action.equals("clear")) {
              System.out.println("Invalid action. Choose from: add, remove, list, clear");
              break;

            }


              json.put("type", "playlist");
              json.put("action", action);

              if(action.equals("add")){
                System.out.println("Enter the song title name:");
                String songTitle = scanner.nextLine();
                System.out.println("Enter the song artist name:");
                String songArtist = scanner.nextLine();
                json.put("song", songTitle);
                json.put("artist", songArtist);
              }

              if(action.equals("remove")){
                System.out.println("Enter the song title name:");
                String songTitle = scanner.nextLine();
                json.put("song", songTitle);
              }

              // no list needed

              // no clear needed

              break;

          case 6:
            System.out.println("Choose analyzer action, wordcount, charchount, search");
            json.put("type", "analyzer");
            json.put("action", "search");
            json.put("text", "hello five");
            json.put("find", "Five"); //checking if goes to lowercase

            // TODO: implement currency (4) or playlist (5) for Part C
        }
        if(!requesting) {
          continue;
        }

        // write the whole message
        os.writeObject(json.toString());
        // make sure it wrote and doesn't get cached in a buffer
        os.flush();

        // handle the response
        // - not doing anything other than printing some things, make this better
        // !! you will most likely need to parse the response for the other 2 services!
        String i = (String) in.readUTF();
        JSONObject res = new JSONObject(i);
        System.out.println("Got response: " + res);
        if (res.getBoolean("ok")){
          if (res.getString("type").equals("echo")) {
            System.out.println(res.getString("echo"));

            // calculatemany operations here
          } else if (res.getString("type").equals("calculatemany")) {

            if (res.getString("operation").equals("add")) {
              System.out.println("The sum is: "+ (res.getInt("sum")));

            } else if (res.getString("operation").equals("multiply")) {
              System.out.println("The product is: "+ (res.getInt("product")));

            } else if (res.getString("operation").equals("average")) {
              System.out.println("The average is: "+ (res.getInt("average")));
            }

            // playlist actions here
          } else if (res.getString("type").equals("playlist")) {

            if (res.getString("action").equals("add")) {
              System.out.println("\n" + res.getString("message"));
            }
            else if (res.getString("action").equals("remove")) {
              System.out.println("\n" + res.getString("message"));
            }
            else if (res.getString("action").equals("list")) {
              JSONArray songs = res.getJSONArray("songs");
              for(int j = 0; j < songs.length(); j++){
                JSONObject song = songs.getJSONObject(j);
                System.out.println("\n" + song.getString("song") + " - " + song.getString("artist"));
              }
            }
            else if (res.getString("action").equals("clear")) {
              System.out.println("\nPlaylist cleared");
            }

          } else if (res.getString("type").equals("analyzer")) {
            System.out.println("Analyzer");
          }

          else  {
            System.out.println(res.getString("result")); // int to string, bug fix? YESBUG (4)
          }
        } else {
          System.out.println("\n" + res.getString("message"));
        }
      }
      // want to keep requesting services so don't close connection
      //overandout();


    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void overandout() throws IOException {
    //closing things, could
    in.close();
    os.close();
    sock.close(); // close socked after sending
  }

  public static void connect(String host, int port) throws IOException {
    // open the connection
    sock = new Socket(host, port); // connect to host and socket on port 8888

    // get output channel
    out = sock.getOutputStream();

    // create an object output writer (Java only)
    os = new ObjectOutputStream(out);

    in = new DataInputStream(sock.getInputStream());
  }
}