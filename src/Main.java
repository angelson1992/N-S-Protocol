public class Main {

  public static void main(String[] args) {

    DiffieHellman_Key_Exchange KeAlice = new DiffieHellman_Key_Exchange(353, 3, 200);

    DiffieHellman_Key_Exchange KeBob = new DiffieHellman_Key_Exchange(353, 3, 27);

    int alicePublicKey = KeAlice.getPublicKey();
    int bobPublicKey = KeBob.getPublicKey();

    System.out.println("Alice says that session key is " + KeAlice.computeSessionKey(bobPublicKey) + " and Bob says the session key is " + KeBob.computeSessionKey(alicePublicKey));

  }

}
