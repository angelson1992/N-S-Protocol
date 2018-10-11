import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

import static java.lang.Thread.sleep;

public class Main {

  public static void main(String[] args) throws IOException, InterruptedException {

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
