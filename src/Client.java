// File Name Client.java
import javafx.util.Pair;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Client extends Thread {

  private DiffieHellman_Key_Exchange keyExchange;
  private int port;

  public final String PUBLIC_KEY_COMMAND = "PublicKey";
  public final String DISTRIBUTE_KEYS_COMMAND = "DistributeKeys";
  private int sessionKey;
  private String name;
  private boolean isInitiator;

  public Client(int globallyAgreedPrimeIntegerVar, int globallyAgreedAlphaVar, int port, String name, boolean isInitiator) {
    keyExchange = new DiffieHellman_Key_Exchange(globallyAgreedPrimeIntegerVar, globallyAgreedAlphaVar);
    this.port = port;
    this.name = name;
    this.isInitiator = isInitiator;
  }

  public void run() {
    String serverName = "localhost";

    try {
      System.out.println(name + ": Connecting to " + serverName + " on port " + port);
      Socket client = new Socket(serverName, port);

      System.out.println(name + ": Just connected to " + client.getRemoteSocketAddress());
      OutputStream outToServer = client.getOutputStream();
      DataOutputStream out = new DataOutputStream(outToServer);
      ObjectOutputStream oos = new ObjectOutputStream(outToServer);

      InputStream inFromServer = client.getInputStream();
      DataInputStream in = new DataInputStream(inFromServer);
      ObjectInputStream ois = new ObjectInputStream(inFromServer);

      System.out.println(name + ": Sending out public key " + keyExchange.getPublicKey());
      oos.writeObject(new Pair<String,Pair<String, Integer>>(PUBLIC_KEY_COMMAND, new Pair<>(name, keyExchange.getPublicKey())));
      oos.flush();

      int serverPublicKey = in.readInt();
      System.out.println(name + ": Server's public key is " + serverPublicKey);

      sessionKey = keyExchange.computeSessionKey(serverPublicKey);
      System.out.println(name + " has calculated session key: " + sessionKey);



      if(isInitiator){

        int nameHash = name.hashCode()%255;
        String partnerName = "bob";
        int partnerNameHash =  partnerName.hashCode()%255;
        int nonce1 = (int) System.currentTimeMillis();
        Pair<String, Integer> arr = new Pair<>(DISTRIBUTE_KEYS_COMMAND, nameHash);
        oos.writeObject(arr);
        oos.flush();

        System.out.println(name + " aka " + nameHash + " is requesting keys distributed for itself and " + partnerName + " aka " + partnerNameHash + " at time " + nonce1);



      }

      client.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}