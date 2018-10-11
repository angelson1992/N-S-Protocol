
import javafx.util.Pair;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server extends Thread {
  private ServerSocket serverSocket;
  private TreeMap<Integer,Integer> clients = new TreeMap<>();
  private DiffieHellman_Key_Exchange keyExchange;

  public final String PUBLIC_KEY_COMMAND = "PublicKey";
  public final String DISTRIBUTE_KEYS_COMMAND = "DistributeKeys";

  public Server(int globallyAgreedPrimeIntegerVar, int globallyAgreedAlphaVar, int port) throws IOException {
    serverSocket = new ServerSocket(port);
    serverSocket.setSoTimeout(10000);
    keyExchange = new DiffieHellman_Key_Exchange(globallyAgreedPrimeIntegerVar, globallyAgreedAlphaVar);
  }

  public void run() {
    while(true) {
      try {
        System.out.println("Server is waiting for client on port " +
          serverSocket.getLocalPort() + "...");

        Socket server = serverSocket.accept();

        System.out.println("Server just connected to " + server.getRemoteSocketAddress());
        DataInputStream in = new DataInputStream(server.getInputStream());
        ObjectInputStream ois = new ObjectInputStream(server.getInputStream());

        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());

        Pair<String,Object> request = (Pair<String,Object>) ois.readObject();

        sleep(1000);
        if(request.getKey().compareTo(PUBLIC_KEY_COMMAND) == 0){

          Pair<String, Integer> requestValue = (Pair<String, Integer>) request.getValue();

          System.out.println("Server received a public key: " + requestValue.getValue());

          int sessionKey = keyExchange.computeSessionKey( requestValue.getValue());
          String name = requestValue.getKey();
          System.out.println("Server has calculated session key with " + name + ": " + sessionKey);
          clients.put(name.hashCode()%255, sessionKey);

          System.out.println("Server is sending out server public key: " + keyExchange.getPublicKey());
          out.writeInt(keyExchange.getPublicKey());

          oos.flush();

        }else if(request.getKey().compareTo(DISTRIBUTE_KEYS_COMMAND) == 0){

          int initiatorNameHash = (int) request.getValue();
          int recieverNameHash = ois.readInt();
          int noice1 = ois.readInt()%255;
          int key1 = clients.get(initiatorNameHash);
          int key2 = clients.get(recieverNameHash);
          int keyS = (key1 + key2) % 255;
          System.out.println("KDC is sending Ks=" + keyS + ", IDb=" + recieverNameHash + ", N1=" + noice1 + " that is encrypted and Ks, IDa=" + initiatorNameHash + " double encrypted.");

          Toy_DES_CounterVersion key1Encrypter = new Toy_DES_CounterVersion(Integer.toString(key1, 2));
          Toy_DES_CounterVersion key2Encrypter = new Toy_DES_CounterVersion(Integer.toString(key2, 2));
          int[] key2Cypher = key2Encrypter.encryptAndDecrypt(new int[]{keyS, initiatorNameHash, noice1}, true);
          int[] key1Cypher = key1Encrypter.encryptAndDecrypt(new int[]{keyS, recieverNameHash, noice1, key2Cypher[0], key2Cypher[1], key2Cypher[2]}, true);

           for(int i = 0; i < key1Cypher.length; i++) {
             oos.writeInt(key1Cypher[i]);
             oos.flush();
           }

        }


        server.close();

      } catch (SocketTimeoutException s) {
        System.out.println("Socket timed out!");
        break;
      } catch (IOException e) {
        e.printStackTrace();
        break;
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String [] args) {
    int port = 2018;
    try {
      Thread t = new Server(353, 3, port);
      t.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
