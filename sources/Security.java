package sources;
import java.math.*;
import java.security.*;
import java.nio.charset.StandardCharsets;
public class Security{
  public static String hash(String input){
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
}
