import sources.*;
import java.util.*;
class Test{
  public static void main(String[] args) {
    Database db = new Database("./data.txt");
    // Map<String, String> data = new HashMap<>();
    // data.put("age","14");
    // data.put("Occupation","Student");
    // db.insert(data);
    Map<String, String> myQuery = new HashMap<>();
    myQuery.put("id","1");
    System.out.println(db.find(myQuery));
  }
}
