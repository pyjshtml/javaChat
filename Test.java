import sources.*;
import java.util.*;
class Test{
  protected static Map<String, String> env = System.getenv();
  public static final String dbLoc = env.get("dbLocation");
  public static Database db = new Database(dbLoc);
  public static void main(String[] args) {
    System.out.println(env);
    System.out.println(db.read());
  }
}
