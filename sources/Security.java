package sources;
import java.math.*;
import java.security.*;
import java.util.*;
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
  public static String hash(String input, boolean useSalt){
    if(useSalt==true){
      return hash(input);
    } else {
      return hash(input,"");
    }
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
  public static String webKey(String input) throws Exception{
    MessageDigest md = MessageDigest.getInstance("SHA-1");
    byte[] ninp = md.digest(input.concat("258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("utf-8"));
    return Base64.getUrlEncoder().encodeToString(ninp).replaceAll("_","/").replaceAll("\\-","+");
  }
  public static boolean compare(String unhashed, String hash){
    return hash(unhashed).equals(hash);
  }
  public static boolean compare(String unhashed, String hash, String salt){
    return hash(unhashed, salt).equals(hash);
  }
}
