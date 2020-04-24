package sources;
import java.math.*;
import java.security.*;
import java.nio.charset.StandardCharsets;
public class Security{
  public static final String salt = "cQwMKtTyEr";
  public static String hash(String input){
    String res = "Error";
    try{
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      StringBuilder sb = new StringBuilder(new BigInteger(1,md.digest(salt.concat(input).getBytes("utf-8"))).toString(16));
      while (sb.length() < 32){
        sb.insert(0, '0');
      }
      res = sb.toString();
    } catch(Exception e){
      e.printStackTrace();
    }
    return res;
  }
  public static String hash(String input, String salt){
    String res = "Error";
    try{
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      StringBuilder sb = new StringBuilder(new BigInteger(1,md.digest(input.getBytes("utf-8"))).toString(16));
      while (sb.length() < 32){
        sb.insert(0, '0');
      }
      res = sb.toString();
    } catch(Exception e){
      e.printStackTrace();
    }
    return res;
  }
  public static boolean compare(String unhashed, String hash){
    return hash(unhashed).equals(hash);
  }
  public static boolean compare(String unhashed, String hash, String salt){
    return hash(unhashed, salt).equals(hash);
  }
}
