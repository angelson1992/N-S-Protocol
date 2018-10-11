import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.*;

class Toy_DES {

  private boolean verboseTest = false;
  private BitSet key = new BitSet();
  private BitSet subKey1;
  private BitSet subKey2;
  private int[][] sBox1 = {{1, 0, 3, 2},
    {3, 2, 1, 0},
    {0, 2, 1, 3},
    {3, 1, 3, 2}};
  private int[][] sBox2 = {{0, 1, 2, 3},
    {2, 0, 1, 3},
    {3, 0, 1, 0},
    {2, 1, 0, 3}};

  Toy_DES(String keyParam){

    setKey(keyParam);

    //Sets up the subkeys
    keySchedule();

  }

  public void setKey(String keyParam){

    //Inputs the string representation of the binary key into the BitSet key variable
    for(int i = 0; i < keyParam.length(); i++){
      if(keyParam.charAt(i) == '1') {key.set(i, true);} else {key.set(i, false);}
    }

  }

  private BitSet permutation(BitSet inputBits, List<Integer> permutationTable){

    BitSet answer = new BitSet(permutationTable.size());

//        for(int i = 0; i < permutationTable.size(); i++){
//
//            //Set the permutationTable[ith] number of the answer bitset to the inputBit[ith] value
//            //Because permutationTables count from 1 instead of from 0, permutationTable has a -1 to work with
//            answer.set(permutationTable.get(i)-1, inputBits.get(i));
//
//        }

    for(int i = 0; i < permutationTable.size(); i++){

      answer.set(i, inputBits.get(permutationTable.get(i)-1));

    }

    if(verboseTest){
      System.out.println("Permutation has begun.\n" +
        "We are permuting the input " + inputBits + "\n" +
        "by using the table " + permutationTable + "\n" +
        "and the result is " + answer + "\n" +
        "Permutation has ended.\n\n");
    }

    return answer;

  }

//    public BitSet expansionPermutation(BitSet inputBits, List<Integer> permutationTable){
//
//        BitSet answer = new BitSet();
//
//        for(int i = 0; i < permutationTable.size(); i++){
//
//            answer.set(i, inputBits.get(permutationTable.get(i)));
//
//        }
//
//        if(verboseTest){
//            System.out.println("Expansion Permutation has begun.\n" +
//              "We are Expansion permuting the input " + inputBits + "\n" +
//              "by using the table " + permutationTable + "\n" +
//              "and the result is " + answer + "\n" +
//              "Expansion Permutation has ended.\n\n");
//        }
//
//        return answer;
//
//    }

  //This is just a utility function to get integers from binary
  public int BitsToInt(BitSet input, int size){

    String binaryString = "";

    for(int i = 0; i < size; i++){

      if(input.get(i)){
        binaryString = binaryString + "1";
      }else{
        binaryString = binaryString + "0";
      }

    }

    if(verboseTest){
      System.out.println("A BitSet is being converted to a decimal integer.\n" +
        "We are converting " + input + "\n" +
        "The BitSet was converted to the string " + binaryString + "\n" +
        "And the integer " + Integer.parseInt(binaryString, 2) + "\n\n");
    }

    return Integer.parseInt(binaryString, 2);

  }

  public String TwoDMatrixPrint(int[][] matrix){

    String toBeprinted = "";
    for(int i = 0; i < matrix.length; i++){
      for(int j = 0; j < matrix[i].length; j++){
        toBeprinted = toBeprinted + matrix[i][j] + ", ";
      }
      toBeprinted = toBeprinted + "\n";
    }

    return toBeprinted;
  }

  //This method simulates the leftward bitshift on BitSets
  public BitSet leftShiftRotation(BitSet inputBits, int size){

    BitSet answer = new BitSet(inputBits.size());

    //Set the first bit of the input into the last bit of the answer
    answer.set(size-1, inputBits.get(0));

    for(int i = 1; i < size; i++){

      //Set all bits of the input into the bit directly to it's left in the answer
      //except the first bit
      answer.set(i-1, inputBits.get(i));

    }

    if(verboseTest){
      System.out.println("LeftShiftRotation has been called.\n" +
        "The input being shifted is " + inputBits + "\n" +
        "The value gotten is " + answer + "\n\n");
    }

    return answer;

  }

  //A simple function to append two BitSets together.
  public BitSet append(BitSet first, int firstSize, BitSet second, int secondSize){

    //This effectively copies the first part
    BitSet answer = (BitSet) first.clone();

    //This adds the second part to the copy of the first part
    for(int i = firstSize; i < firstSize+secondSize; i++){

      answer.set(i, second.get(i-firstSize));

    }

    if(verboseTest){

      System.out.println("The append function has been called for BitSets.\n" +
        "The values being appended are " + first + " and " + second + "\n" +
        "The result is " + answer + "\n\n");

    }

    return answer;

  }

  public List<BitSet> keySchedule(){

    if(verboseTest){System.out.println("The key scheduler was been called.\n");}

    List<BitSet> answer = new ArrayList<>();

    //P10 Permutation table representation
    Integer[] TenBitPermutation = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    List<Integer> TenBitPerm = Arrays.asList(TenBitPermutation);

    //The permutation step that gets us the P10 block in key schedule on the slides
    BitSet P10Block = permutation(key, TenBitPerm);

    //Splitting P10 block in half to work on
    BitSet leftHalf = P10Block.get(0, 5);
    BitSet rightHalf = P10Block.get(5, 10);

    //Doing the leftshift on the halfs of the P10 block
    leftHalf = leftShiftRotation(leftHalf, 5);
    rightHalf = leftShiftRotation(rightHalf, 5);

    //Putting the modified halfs back together
    BitSet toBePermuted = append(leftHalf, 5, rightHalf, 5);

    //P8 Permutation table representation
    Integer[] TenToEightBitPermutation = {6, 3, 7, 4, 8, 5, 10, 9};
    List<Integer> TenEightPerm = Arrays.asList(TenToEightBitPermutation);

    //Use of reduction permutation to get the first subkey
    subKey1 = permutation(toBePermuted, TenEightPerm);
    answer.add(subKey1);

    //Doing the leftshifts on the halfs again for the second subkey
    leftHalf = leftShiftRotation(leftHalf, 5);
    rightHalf = leftShiftRotation(rightHalf, 5);

    //Putting the remodified halfs back together
    BitSet toBePermuted2ElectricBoogaloo = append(leftHalf, 5, rightHalf, 5);

    //Use of reduction permutation to get the second subkey
    subKey2 = permutation(toBePermuted2ElectricBoogaloo, TenEightPerm);
    answer.add(subKey2);

    if(verboseTest){
      System.out.println("We worked on the key " + key + "\n" +
        "And we got the subkeys " + answer + "\n" +
        "The key scheduler has complete.\n\n");
    }

    return answer;
  }

  //Just some abstraction put around the xor function for the sake of style consistency and testing
  public BitSet XOR(BitSet left, BitSet right){

    BitSet answer = (BitSet) left.clone();
    answer.xor(right);

    if(verboseTest){
      System.out.println("XOR has been called.\n" +
        "The inputs are " + left + " and " + right + "\n" +
        "And the answer is " + answer + "\n\n");
    }

    return answer;

  }

  private BitSet sBoxFunction(BitSet input, int[][] matrix){

    String row = "";
    String col = "";

    //Grabbing the row and col values as strings of the 1s and 0s values
    if (input.get(0) == true) {row = row + "1";} else {row = row + "0";}
    if (input.get(3) == true) {row = row + "1";} else {row = row + "0";}
    if (input.get(1) == true) {col = col + "1";} else {col = col + "0";}
    if (input.get(2) == true) {col = col + "1";} else {col = col + "0";}

    //Converting the strings of 1s and 0s to integers
    int rowVal = Integer.parseInt(row, 2);
    int colVal = Integer.parseInt(col, 2);

    //doing the sBox function and converting the result to a string for convenience
    int sBoxValue = matrix[rowVal][colVal];
    String sBoxString = Integer.toBinaryString(sBoxValue);

    //Converting the convenient string into a more convenient BitSet
    BitSet answer = new BitSet();
    for(int i = 0; i < sBoxString.length(); i++){
      if(sBoxString.charAt(i) == '1'){
        answer.set(i, true);
      }else{
        answer.set(i, false);
      }
    }

    if(verboseTest){
      System.out.println("The sBoxFunction as been called.\n" +
        "on the input " + input + " and the matrix of \n" + TwoDMatrixPrint(matrix) + "\n" +
        "the value picked was " + sBoxValue + " aka " + sBoxString + "\n" +
        "and the answer is " + answer + "\n\n");
    }

    return answer;

  }

  private BitSet fFunctionBox(BitSet input, BitSet key){

    if(verboseTest){System.out.println("fFunctionBox has been called on input " + input + " and key " + key + ".");}

    //Expansion permutation representation
    Integer[] FourToEightBitPermutation = {4, 1, 2, 3, 2, 3, 4, 1};
    List<Integer> FourEightPerm = Arrays.asList(FourToEightBitPermutation);

    //Use of expansion permutation
    BitSet P4to8Block = permutation(input, FourEightPerm);

    //XOR P4to8 and a subkey
    BitSet toBeSplit = XOR(P4to8Block, key);

    //Splitting in half
    BitSet left = toBeSplit.get(0, 4);
    BitSet right = toBeSplit.get(4, 8);

    //Using the sBoxes
    BitSet sBoxResultsLeft = sBoxFunction(left, sBox1);
    BitSet sBoxResultsRight = sBoxFunction(right, sBox2);

    //Merging the sBox outputs back together
    BitSet sBoxResultsAll = append(sBoxResultsLeft, 2, sBoxResultsRight, 2);

    //Four bit permutation representation
    Integer[] FourBitPermutation = {2, 4, 3, 1};
    List<Integer> FourPerm = Arrays.asList(FourBitPermutation);

    BitSet answer = permutation(sBoxResultsAll, FourPerm);

    if(verboseTest){
      System.out.println("fBoxFunction input was " + input + "\n" +
        "And the given subkey was " + key + "\n" +
        "With a final result of " + answer + "\n\n");
    }

    return answer;

  }

  private List<BitSet> fiestelRound(BitSet left, BitSet right, BitSet subkey){

    if(verboseTest){System.out.println("A fiestel round has begun on left " + left + " and right " + right + " with subkey" + subkey + "\n");}

    ArrayList<BitSet> answer = new ArrayList();

    //Calling fFunction on the right half
    BitSet modifiedRight = fFunctionBox(right, subkey);

    //XORing the left half and the fFunctioned right half
    BitSet newLeft = XOR(left, modifiedRight);

    answer.add(0, newLeft);
    answer.add(1, right);

    if(verboseTest){System.out.println("A fiestel round has ended and the answer was " + answer + "\n\n");}

    return answer;

  }

  public int encryptAndDecrypt(int input, boolean isEncryptionMode){

    if(verboseTest && isEncryptionMode){System.out.println("Encryption has begun.");}
    if(verboseTest && !isEncryptionMode){System.out.println("Decryption has begun.");}

    //Tranforming the Byte input into a BitSet input for conveniences sake
    BitSet inputBitSet = new BitSet();
    String inputString = Integer.toBinaryString(input);

    for(int i = inputString.length(); i > 0; i--){
      if(inputString.charAt(i-1)=='1'){inputBitSet.set(inputString.length()-i, true);}else{inputBitSet.set(inputString.length()-i, false);}
    }

    //Initial permutation table representation
    Integer[] initialPermutation = {2, 6, 3, 1, 4, 8, 5, 7};
    List<Integer> initPerm = Arrays.asList(initialPermutation);

    //Doing initial permutation
    BitSet permutedInputBits = permutation(inputBitSet, initPerm);

    List<BitSet> roundTwoResults;
    if(isEncryptionMode) {
      //The first fiestelRound for encryption
      List<BitSet> roundOneResults = fiestelRound(permutedInputBits.get(0, 4), permutedInputBits.get(4, 8), subKey1);

      //The second fiestelRound with switch the switched sides for encryption
      roundTwoResults = fiestelRound(roundOneResults.get(1), roundOneResults.get(0), subKey2);
    }else{
      //The first fiestelRound for decryption
      List<BitSet> roundOneResults = fiestelRound(permutedInputBits.get(0, 4), permutedInputBits.get(4, 8), subKey2);

      //The second fiestelRound with switch the switched sides for decryption
      roundTwoResults = fiestelRound(roundOneResults.get(1), roundOneResults.get(0), subKey1);
    }

    //Combining the results together
    BitSet toBeInversePermuted = append(roundTwoResults.get(0), 4, roundTwoResults.get(1), 4);

    //Inverse initial permutation table representation
    Integer[] inverseInitialPermutation = {4, 1, 3, 5, 7, 2, 8, 6};
    List<Integer> inverInitPerm = Arrays.asList(inverseInitialPermutation);

    //Doing the inverse initial permutation
    BitSet answer = permutation(toBeInversePermuted, inverInitPerm);

    //Converting the answer BitSet to a string for convenience
    String answerString = "";
    for(int i = 8; i > 0; i--){
      if(answer.get(i-1) == true){
        answerString = answerString + "1";
      }
      if(answer.get(i-1) == false){
        answerString = answerString + "0";
      }
    }


    int byteAnswer = Integer.valueOf(answerString, 2);

    if(verboseTest && isEncryptionMode){
      System.out.println("Encryption was used on input " + input + "\n" +
        "and has given the output " + byteAnswer + " aka " + Integer.toBinaryString(byteAnswer) + "\n\n");
    }
    if(verboseTest && !isEncryptionMode){
      System.out.println("Decryption was used on input " + input + "\n" +
        "and has given the output " + byteAnswer + " aka " + Integer.toBinaryString(byteAnswer) + "\n\n");
    }

    return byteAnswer;
  }

}

//public class Main {
//
//  public static void main(String[] args) {
//
//    Toy_DES blockCypher = new Toy_DES("0101000100");
//    int cyphertext = blockCypher.encryptAndDecrypt(Byte.valueOf("001100",2), true);
//    blockCypher.encryptAndDecrypt(cyphertext, false);
//
//    if(false) {
//
//      BitSet test = new BitSet(8); test.set(0); test.set(3); test.set(6);
//
//      Integer[] table = {2, 6, 3, 1, 4, 8, 5, 7};
//      List<Integer> initialPermutation = Arrays.asList(table);
//
//      Integer[] inverseTable = {4, 1, 3, 5, 7, 2, 8, 6};
//      List<Integer> inversePermutation = Arrays.asList(inverseTable);
//
//      BitSet permuted = blockCypher.permutation(test, initialPermutation);
//      BitSet inversePermuted = blockCypher.permutation(permuted, inversePermutation);
//
//      System.out.println("The plaintext is " + test);
//      System.out.println("The initial permutation is " + permuted);
//      System.out.println("The permuted text inversed is " + inversePermuted);
//      System.out.println("The plaintext rotated left is " + blockCypher.leftShiftRotation(test, 8));
//      System.out.println("The permuted text rotated left is " + blockCypher.leftShiftRotation(permuted, 8));
//      System.out.println(Integer.parseInt("11010", 2));
//      System.out.println("Test is converted as " + blockCypher.BitsToInt(test, 8) + ".\n"
//        + "permuted is converted as " + blockCypher.BitsToInt(permuted, 8) + ".\n"
//        + "Rotated test is converted as " + blockCypher.BitsToInt(blockCypher.leftShiftRotation(test, 8), 8) + ".\n"
//        + "Rotated permuted is converted as " + blockCypher.BitsToInt(blockCypher.leftShiftRotation(permuted, 8), 8) + ".");
//      System.out.println("Test and permuted appended is " + blockCypher.append(test, 8, permuted, 8));
//      System.out.println("permuted and inversePermuted appended is " + blockCypher.append(permuted, 8, inversePermuted, 8));
//      System.out.println(Integer.toBinaryString(Byte.valueOf("1111111",2)));
//
//    }
//
//  }
//
//}