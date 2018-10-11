import java.util.Arrays;
import java.util.BitSet;

/**
 * Created by John on 10/10/2018.
 */
public class Toy_DES_CounterVersion extends Toy_DES {

  private String firstKeyString;

  public Toy_DES_CounterVersion(String keyParam) {
    super(keyParam);
    firstKeyString = keyParam;
  }

  public int[] encryptAndDecrypt(int[] input, boolean isEncryptionMode){

    int answer[] = new int[input.length];
    int keyCounter = Integer.parseInt(firstKeyString, 2);

    if(isEncryptionMode) {

      for (int i = 0; i < input.length; i++) {

        setKey(Integer.toString(keyCounter + i, 2));
        keySchedule();

        answer[i] = encryptAndDecrypt(input[i], true);

      }

    }else{

      for (int i = 0; i < input.length; i++) {

        setKey(Integer.toString(keyCounter + i, 2));
        keySchedule();

        answer[i] = encryptAndDecrypt(input[i], false);

      }

    }

    return answer;

  }

}
