// File Name Client.java
import javafx.util.Pair;

import java.net.*;
import java.io.*;

public class Client extends Thread {

  private DiffieHellman_Key_Exchange keyExchange;
  private int port;

  public final String PUBLIC_KEY_COMMAND = "PublicKey";
  public final String DISTRIBUTE_KEYS_COMMAND = "DistributeKeys";
  private int sessionKey;
  private String name;

  public Client(int globallyAgreedPrimeIntegerVar, int globallyAgreedAlphaVar, int port, String name) {
    keyExchange = new DiffieHellman_Key_Exchange(globallyAgreedPrimeIntegerVar, globallyAgreedAlphaVar);
    this.port = port;
    this.name = name;
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


      int serverPublicKey = in.readInt();
      System.out.println(name + ": Server's public key is " + serverPublicKey);

      sessionKey = keyExchange.computeSessionKey(serverPublicKey);
      System.out.println(name + " has calculated session key: " + sessionKey);





      client.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}