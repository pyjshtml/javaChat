import sources.*;
import java.util.*;
class Test{
  public static void main(String[] args) {
    try{
      String token = "Bearer%20e3R5cD1qd3QsIGFsZz1SUzI1Nn0.e2VtYWlsPWphY2tAbG9jYWxob3N0fQ.2664a457998db80dbc14128a0a36cde0a61228bedaf167d99eb99b0ebc1ce8dd";
      boolean isValid = JWT.isValid("pwd",token);
      System.out.println(isValid);
    } catch(Exception e){
      e.printStackTrace();
    }
  }
}
