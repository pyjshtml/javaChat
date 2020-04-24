package sources;
import sources.*;//security.*;
import java.math.*;
import java.security.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
public class JWT{
  public String token = "";
  public static String encode(String text) throws Exception{
    return Base64.getUrlEncoder().withoutPadding().encodeToString(text.getBytes("utf-8"));
  }
  public static String decode(String text) throws Exception{
    return new String(Base64.getUrlDecoder().decode(text),StandardCharsets.UTF_8);
  }
  public JWT(String secret, Map header, Map payload){
    try{
      String nHedd = encode(header.toString());
      String nPay = encode(payload.toString());
      String temp = nHedd.concat(".").concat(nPay).concat(secret);
      String sign = Security.hash(temp,false);
      token = nHedd.concat(".").concat(nPay).concat(".").concat(sign);
    } catch(Exception e){
      e.printStackTrace();
    }
  }
  public static ArrayList<String> verifyToken(String secret,String token) throws Exception{
    String processedToken = token.split("Bearer\\%20")[1];
    String[] components = processedToken.split("\\.");
    if(components.length == 3){
      if(components[2].equals(Security.hash(components[0].concat(".").concat(components[1]).concat(secret),false))){
        ArrayList<String> res = decodeToken(processedToken);
        return res;
      } else {
        System.out.println("Invalid token");
      }
    }
    return new ArrayList<String>();
  }
  public ArrayList<String> verifyToken(String secret) throws Exception{
    String[] components = token.split("\\.");
    if(components[2].equals(Security.hash(components[0].concat(".").concat(components[1]).concat(secret),false))){
      ArrayList<String> res = this.decodeToken();
      return res;
    } else {
      System.out.println("Invalid token");
    }
    return new ArrayList<String>();
  }
  public static ArrayList<String> decodeToken(String token) throws Exception{
    ArrayList<String> res = new ArrayList<>();
    String[] components = token.split("\\.");
    String header = decode(components[0]);
    String payload = decode(components[1]);
    res.add(header);
    res.add(payload);
    return res;
  }
  public ArrayList<String> decodeToken() throws Exception{
    ArrayList<String> res = new ArrayList<>();
    String[] components = token.split("\\.");
    String header = decode(components[0]);
    String payload = decode(components[1]);
    res.add(header);
    res.add(payload);
    return res;
  }
  public static boolean isValid(String secret, String token) throws Exception{
    return !verifyToken(secret, token).isEmpty();
  }
  public boolean isValid(String secret) throws Exception{
    return !this.verifyToken(secret).isEmpty();
  }
}
