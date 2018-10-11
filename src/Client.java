// File Name Client.java
import javafx.util.Pair;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Client extends Thread {

  private DiffieHellman_Key_Exchange keyExchange;
  private int port;
  private int clientToClientPort;
  private int clientToClientKey;

  public final String PUBLIC_KEY_COMMAND = "PublicKey";
  public final String DISTRIBUTE_KEYS_COMMAND = "DistributeKeys";
  private int sessionKey;
  private String name;
  private boolean isInitiator;

  public Client(int globallyAgreedPrimeIntegerVar, int globallyAgreedAlphaVar, int port, int clientToClientPort, String name, boolean isInitiator) {
    keyExchange = new DiffieHellman_Key_Exchange(globallyAgreedPrimeIntegerVar, globallyAgreedAlphaVar);
    this.port = port;
    this.name = name;
    this.isInitiator = isInitiator;
    this.clientToClientPort = clientToClientPort;
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

      client.close();

      if(isInitiator){

        System.out.println(name + ": Connecting to " + serverName + " on port " + port);
        client = new Socket(serverName, port);

        System.out.println(name + ": Just connected to " + client.getRemoteSocketAddress());
        outToServer = client.getOutputStream();
        out = new DataOutputStream(outToServer);
        oos = new ObjectOutputStream(outToServer);

        inFromServer = client.getInputStream();
        in = new DataInputStream(inFromServer);
        ois = new ObjectInputStream(inFromServer);



        int nameHash = name.hashCode()%255;
        String partnerName = "Bob";
        int partnerNameHash =  partnerName.hashCode()%255;
        int nonce1 = (int) System.currentTimeMillis();
        Pair<String, Integer> arr = new Pair<>(DISTRIBUTE_KEYS_COMMAND, nameHash);
        oos.writeObject(arr);
        oos.writeInt(partnerNameHash);
        oos.writeInt(nonce1);
        oos.flush();
        System.out.println(name + " aka " + nameHash + " is requesting keys distributed for itself and " + partnerName + " aka " + partnerNameHash + " at time " + nonce1);

        int[] buffer = new int[6];
        for(int i = 0; i < buffer.length; i++){
          buffer[i] = ois.readInt();
        }

        Toy_DES_CounterVersion decrypter = new Toy_DES_CounterVersion(Integer.toString(sessionKey, 2));
        int[] results = decrypter.encryptAndDecrypt(buffer, false);
        clientToClientKey = results[0];
        System.out.println(name + " received keyS=" + results[0] + " IDb=" + results[1] + " and N1=" + results[2]);

        Socket clientToClientSocket = new Socket(serverName, clientToClientPort);
        System.out.println(name + ": Just connected to " + clientToClientSocket.getRemoteSocketAddress());
        OutputStream outToClientToClient = clientToClientSocket.getOutputStream();
        DataOutputStream outClientToClient = new DataOutputStream(outToClientToClient);
        ObjectOutputStream oosClientToClient = new ObjectOutputStream(outToClientToClient);

        InputStream inFromClientToClient = clientToClientSocket.getInputStream();
        DataInputStream inClientToClient = new DataInputStream(inFromClientToClient);
        ObjectInputStream oisClientToClient = new ObjectInputStream(inFromClientToClient);

        System.out.println(name + " sent the still encrypted portion.");
        oosClientToClient.writeInt(results[3]);
        oosClientToClient.writeInt(results[4]);
        oosClientToClient.writeInt(results[5]);
        oosClientToClient.flush();


        Toy_DES_CounterVersion clientToClientDES = new Toy_DES_CounterVersion(Integer.toString(clientToClientKey, 2));
        int encryptedTestInt = oisClientToClient.readInt();
        int testResult = clientToClientDES.encryptAndDecrypt(encryptedTestInt, false);
        int testCypherResponse = clientToClientDES.encryptAndDecrypt(testResult+1, true);
        oosClientToClient.writeInt(testCypherResponse);
        oosClientToClient.flush();
        System.out.println(name + " recieved " + testResult + " and is sent " + (testResult+1));

      }else{
        ServerSocket clientToClientServerSocket = new ServerSocket(clientToClientPort);
        clientToClientServerSocket.setSoTimeout(10000);

        System.out.println(name + " is waiting for client on port " +
          clientToClientServerSocket.getLocalPort() + "...");

        Socket clientToClientServer = clientToClientServerSocket.accept();

        System.out.println(name + " just connected to " + clientToClientServer.getRemoteSocketAddress());
        in = new DataInputStream(clientToClientServer.getInputStream());
        ois = new ObjectInputStream(clientToClientServer.getInputStream());

        out = new DataOutputStream(clientToClientServer.getOutputStream());
        oos = new ObjectOutputStream(clientToClientServer.getOutputStream());


        int[] clientToClientBuffer = new int[3];
        for(int i = 0; i < clientToClientBuffer.length; i++){
          clientToClientBuffer[i] = ois.readInt();
        }

        Toy_DES_CounterVersion decrypter = new Toy_DES_CounterVersion(Integer.toString(sessionKey, 2));
        int[] clientToClientResults = decrypter.encryptAndDecrypt(clientToClientBuffer, false);
        clientToClientKey = clientToClientResults[0];
        System.out.println(name + " received keyS=" + clientToClientResults[0] + " IDa=" + clientToClientResults[1] + " and N1=" + clientToClientResults[2]);

        Toy_DES_CounterVersion clientToClientDES = new Toy_DES_CounterVersion(Integer.toString(clientToClientKey, 2));
        int nonce2 = 111;
        oos.writeInt(clientToClientDES.encryptAndDecrypt(nonce2, true));
        oos.flush();
        System.out.println(name + "sent " + nonce2 + " as a test for the clientToClient key");

        int testResponseCypher = ois.readInt();
        int testResponse = clientToClientDES.encryptAndDecrypt(testResponseCypher, false);
        System.out.println(name + " recieved " + testResponse + " as a test response");

      }

      client.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}