/**
 * Created by John on 10/10/2018.
 */
public class DiffieHellman_Key_Exchange {

  private int globallyAgreedPrimeInteger;
  private int globallyAgreedAlpha;
  private int secretKey;
  private int publicKey;

  //initializing DES with a human random key
  private Toy_DES encrypter = new Toy_DES(Integer.toString( (int)(System.currentTimeMillis() % 255), 2) );

  public DiffieHellman_Key_Exchange(int globallyAgreedPrimeIntegerVar, int globallyAgreedAlphaVar) {

    globallyAgreedPrimeInteger = globallyAgreedPrimeIntegerVar;
    globallyAgreedAlpha = globallyAgreedAlphaVar;


    secretKey = encrypter.encryptAndDecrypt((int)(System.currentTimeMillis() % 255), true) % globallyAgreedPrimeInteger;

    publicKey = (globallyAgreedAlpha ^ secretKey) % globallyAgreedPrimeInteger;

  }

  public int getPublicKey() {
    return publicKey;
  }

  public int computeSessionKey(int partnerPublicKey){

    return (partnerPublicKey ^ secretKey) % globallyAgreedPrimeInteger;

  }

}
