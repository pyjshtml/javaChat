/*For reading files*/
import java.io.*;
/*For server*/
import java.net.*;
import java.io.*;
import sources.*;
/*General*/
import java.math.*;
import java.util.*;
import java.lang.*;
import java.util.regex.*;
class Main{
  protected static Map<String, String> env = System.getenv();
  public static final int port = 80;
  public static final String host = env.get("HOST");
  public static final String dbLoc = env.get("dbLocation");
  public static Database db = new Database(dbLoc);
  public static String read(String filename) {
    String content = "";
    try {
      File myObj = new File(filename);
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        content += data+"\r\n";
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    return content;
  }
  public static void write(String filename, String value){
    try{
      FileWriter author = new FileWriter(filename);
      author.write(value+"\n");
      author.close();
    } catch (Exception e){
      System.out.println("File writing error");
      e.printStackTrace();
    }
  }
  /*TEMP*/
  public static boolean checkToken(String token){
    return true;
  }
  /*TEMP*/
  public static boolean contains(String string, String pattern){
    Pattern p = Pattern.compile(pattern);
    return p.matcher(string).find();
  }
  public static void send(int statusCode, Socket socket){
    try{
      OutputStream output = socket.getOutputStream();
      if(statusCode == 404){
        String[] responseHeaders = {
          "HTTP/1.1 404 Path Not Found",
          "Content-Type:text/html; charset=utf-8"
        };
        String httpHead = String.join("\r\n",responseHeaders);
        output.write(String.join("\r\n\r\n",httpHead,read("./sites/404.html")).getBytes("utf-8"));
        socket.close();
      } else {
        String[] responseHeaders = {
          "HTTP/1.1 400 Bad Request",
          "Content-Type:text/html; charset=utf-8"
        };
        output.write(String.join("\r\n",responseHeaders).concat("\r\nBad Request").getBytes("utf-8"));
        socket.close();
      }
    } catch(Exception e){
      System.out.println(e);
    }
  }
  public static void main(String[] args) throws Exception {
    ServerSocket server = new ServerSocket(port,50,InetAddress.getByName(host));
    System.out.println(String.format("Listening on port %d",port));
    for(;;){
      try{
        Socket socket = server.accept();
        System.out.println("Incoming request");
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        ArrayList<String> headers = new ArrayList<>();
        String headerString = "";
        while((line = in.readLine()).length() != 0){
          headers.add(line);
          headerString += line+"\r\n";
        }
        int index = headerString.indexOf(" ");
        String method = headerString.substring(0,index);
        String route = headerString.substring(index+1,headerString.indexOf(" ",index+1));
        String[] subroutes = route.split("(\\?|#)");
        String path = subroutes[0];
        String reqBody = "";
        while(in.ready()){
          reqBody += Character.toString(((char) in.read()));
        }
        Map<String, String> body = new HashMap<>();
        if(reqBody.length() > 0){
          reqBody = URLDecoder.decode(reqBody,"utf-8");
          for(String pair : reqBody.split("&")){
            body.put(pair.split("=")[0],pair.split("=")[1]);
          }
        }
        String reqQuery = "none";
        Map<String, String> query = new HashMap<>();
        if(subroutes.length > 1){
          reqQuery = route.split("(\\?|#)")[1];
          for(String pair : reqQuery.split("&")){
            query.put(pair.split("=")[0],pair.split("=")[1]);
          }
        }
        /*Define Cookies*/
        Map<String, String> cookies = new HashMap<>();
        for(int i = 0; i < headers.size(); i++){
          if(headers.get(i).indexOf("Cookie:")==0){
            for(String cookie : headers.get(i).substring(8).split("; ")){
              cookies.put(cookie.split("=")[0],cookie.split("=")[1]);
            }
          }
        }
        OutputStream output = socket.getOutputStream();
        String[] template = {
          "HTTP/1.1 200 OK",
          "Content-Type: text/html; charset=utf-8",
          "Date".concat(new Date().toString())
        };
        ArrayList<String> responseHeaders = new ArrayList<>(Arrays.asList(template));
        if(method.equals("GET")){
          if(contains(path,"/static")){
            responseHeaders.clear();
            String[] cHead = {
              "HTTP/1.1 200 OK",
              "Vary: Accept",
              "Connection:keep-alive",
              "Date: " + new Date().toString()
            };
            responseHeaders.addAll(Arrays.asList(cHead));
            String httpHead = String.join("\r\n",responseHeaders);
            output.write(String.join("\r\n\r\n",httpHead,read( String.format("./public%s",path.substring(7)) )).getBytes("utf-8"));
            socket.close();
          } else if(path.equals("/")){
            if(cookies.containsKey("access_token") && checkToken(cookies.get("access_token"))){
              String httpHead = String.join("\r\n",responseHeaders);
              String httpBody = read("./sites/index.html");
              output.write(String.join("\r\n\r\n",httpHead,httpBody).getBytes("utf-8"));
              socket.close();
            } else {
              responseHeaders.clear();
              String[] cHead = {
                "HTTP/1.1 302 Found",
                "Location: /login?goto=/"
              };
              responseHeaders.addAll(Arrays.asList(cHead));
              output.write(String.join("\r\n",responseHeaders).concat("\r\n\r\n").getBytes("utf-8"));
              socket.close();
            }
          } else if (path.equals("/aboutus")){
            String httpHead = String.join("\r\n",responseHeaders);
            output.write(String.join("\r\n\r\n",httpHead,read("./sites/aboutus.html")).getBytes("utf-8"));
            socket.close();
          } else if (path.equals("/debug")){
            output.write(String.join("\r\n\r\n",String.join("\r\n",responseHeaders),read("./Main.java")).getBytes("utf-8"));
            socket.close();
          } else if (path.equals("/register")){
            output.write(String.join("\r\n",responseHeaders).concat("\r\n\r\n").concat(read("./sites/register.html")).getBytes("utf-8"));
            socket.close();
          } else if (path.equals("/login")){
            output.write(String.join("\r\n",responseHeaders).concat("\r\n\r\n").concat(read("./sites/login.html")).getBytes("utf-8"));
            socket.close();
          } else if (contains(path,"/completed")){
            output.write(String.join("\r\n",responseHeaders).concat("\r\n\r\n").concat("Data received").getBytes("utf-8"));
            socket.close();
          } else {
            send(404,socket);
          }
        } else if (method.equals("POST")){
          System.out.println("POST request");
          if(contains(path, "/login")){
            // System.out.println(Security.hash(body.get("password")));
            File database = new File(dbLoc);
            if(database.exists()){
              //
              String[] cHead = {
                "HTTP/1.1 302 Found",
                "Location: /completed",
                "Vary: Accept"
              };
              responseHeaders.clear();
              responseHeaders.addAll(Arrays.asList(cHead));
              if(!query.isEmpty()){
                if(query.containsKey("goto")){
                  responseHeaders.set(1,"Location: ".concat(query.get("goto")));
                }
              }
              System.out.println(String.join("\r\n",responseHeaders));
              output.write(String.join("\r\n",responseHeaders).concat("\r\n\r\n").getBytes("utf-8"));
              socket.close();
            } else {
              responseHeaders.clear();
              String[] cHead = {
                "HTTP/1.1 500 Internal Server Error"
              };
              responseHeaders.addAll(Arrays.asList(cHead));
              output.write(String.join("\r\n",responseHeaders).concat("\r\n\r\nBeedo Beedo Beedo").getBytes("utf-8"));
              socket.close();
            }
            // Map<String, String> obj = new HashMap<>();
            // db.find(obj);
          } else {
            send(404,socket);
          }
        }
      } catch(Exception e) {
        e.printStackTrace();
        try {
          Socket socket = server.accept();
          socket.getOutputStream().write("HTTP/1.1 500 Internal Server Error\r\nContent-Type:text/html; charset=utf-8\r\nConnection:close\r\n\r\nHello,World!".getBytes("utf-8"));
          socket.close();
        } catch(Exception e2){
          System.out.println("Kill me");
          e.printStackTrace();
        }
      }
    }
  }
}
// Bearer%20eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
