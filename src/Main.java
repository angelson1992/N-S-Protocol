import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

import static java.lang.Thread.sleep;

public class Main {

  public static void main(String[] args) throws IOException, InterruptedException {

    DiffieHellman_Key_Exchange KeAlice = new DiffieHellman_Key_Exchange(353, 3);

    DiffieHellman_Key_Exchange KeBob = new DiffieHellman_Key_Exchange(353, 3);

    int alicePublicKey = KeAlice.getPublicKey();
    int bobPublicKey = KeBob.getPublicKey();

    System.out.println("Alice says that session key is " + KeAlice.computeSessionKey(bobPublicKey) + " and Bob says the session key is " + KeBob.computeSessionKey(alicePublicKey));

    Toy_DES_CounterVersion counterDES = new Toy_DES_CounterVersion(Integer.toString((int)System.currentTimeMillis()%255, 2));
    int[] test = {1, 2, 231};
    int[] cypher = counterDES.encryptAndDecrypt(test, true);
    System.out.println(Arrays.toString(cypher));
    System.out.println(Arrays.toString(counterDES.encryptAndDecrypt(cypher, false)));

    Server keyDistCntr = new Server(353, 3, 2018);
    Client alice = new Client(353, 3, 2018, 18881, "Alice", true);
    sleep(500);
    Client bob = new Client(353, 3, 2018, 18881, "Bob", false);

    Thread server = keyDistCntr;
    server.start();

    sleep(1000);

    Thread aliT = alice;
    aliT.start();

    sleep(1000);

    Thread bobT = bob;
    bobT.start();

  }

}
