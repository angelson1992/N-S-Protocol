/**
 * Created by John on 10/10/2018.
 */
public class DiffieHellman_Key_Exchange {

  private int globallyAgreedPrimeInteger;
  private int globallyAgreedAlpha;

  private int secretKey;

  public int getPublicKey() {
    return publicKey;
  }

  private int publicKey;

  //initializing DES with a human random key
  private Toy_DES encrypter = new Toy_DES("1001010011");

  public DiffieHellman_Key_Exchange(int globallyAgreedPrimeIntegerVar, int globallyAgreedAlphaVar, int generalSeed) {

    globallyAgreedPrimeInteger = globallyAgreedPrimeIntegerVar;
    globallyAgreedAlpha = globallyAgreedAlphaVar;


    secretKey = encrypter.encryptAndDecrypt(generalSeed % 255, true) % globallyAgreedPrimeInteger;

    publicKey = (globallyAgreedAlpha ^ secretKey) % globallyAgreedPrimeInteger;

  }

  public int computeSessionKey(int partnerPublicKey){

    return (partnerPublicKey ^ secretKey) % globallyAgreedPrimeInteger;

  }

}
