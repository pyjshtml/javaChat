import sources.*;
import java.util.*;
class Test{
  public static void main(String[] args) {
    try{
      String hash = Security.webKey("dGhlIHNhbXBsZSBub25jZQ==");
      System.out.println(hash);
    } catch(Exception e){
      e.printStackTrace();
    }
  }
}
