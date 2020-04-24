/*For reading files*/
import java.io.File;
import java.io.FileNotFoundException;
/*For server*/
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.io.*;
/*General*/
import java.util.*;
import java.lang.*;
import java.util.regex.*;
class Main{
  protected static Map<String, String> env = System.getenv();
  public static final int port = 80;
  public static final String host = env.get("HOST");
  public static final String webroot = "C:\\Users\\Jack\\first_java-server\\sites\\";
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
    while(true){
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
        String body = "";
        while(in.ready()){
          body += Character.toString(((char) in.read()));
        }
        int index = headerString.indexOf(" ");
        String method = headerString.substring(0,index);
        String route = headerString.substring(index+1,headerString.indexOf(" ",index+1));
        String[] subroutes = route.split("(\\?|#)");
        String path = subroutes[0];
        String query = "none";
        if(subroutes.length > 1){
          query = route.split("(\\?|#)")[1];
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
        if(method.equals("GET")){
          if(contains(path,"/static")){
            String[] responseHeaders = {
              "HTTP/1.1 200 OK",
              // String.format("Content-Type:%s/%s",contains(path,"images") ? "image" : "text",path.substring(path.indexOf(".")+1,path.length())),
              "Connection:keep-alive",
              "Date: " + new Date().toString()
            };
            String httpHead = String.join("\r\n",responseHeaders);
            output.write(String.join("\r\n\r\n",httpHead,read( String.format("./public%s",path.substring(7)) )).getBytes("utf-8"));
            socket.close();
          } else if(path.equals("/")){
            if(cookies.containsKey("access_token") && checkToken(cookies.get("access_token"))){
              String[] responseHeaders = {
                "HTTP/1.1 200 OK",
                "Content-Type:text/html; charset=utf-8"
              };
              String httpHead = String.join("\r\n",responseHeaders);
              String httpBody = read("./sites/index.html");
              output.write(String.join("\r\n\r\n",httpHead,httpBody).getBytes("utf-8"));
              socket.close();
            } else {
              String[] responseHeaders = {
                "HTTP/1.1 302 Found",
                "Location: /login?goto=/"
              };
              output.write((String.join("\r\n",responseHeaders)+"\r\n\r\n").getBytes("utf-8"));
              socket.close();
            }
          } else if (path.equals("/aboutus")){
            String[] responseHeaders = {
              "HTTP/1.1 200 OK",
              "Content-Type:text/html; charset=utf-8"
            };
            String httpHead = String.join("\r\n",responseHeaders);
            output.write(String.join("\r\n\r\n",httpHead,read("./sites/aboutus.html")).getBytes("utf-8"));
            socket.close();
          } else if (path.equals("/debug")){
            String[] responseHeaders = {
              "HTTP/1.1 200 OK",
              "Content-Type:text/html; charset=utf-8"
            };
            String httpHead = String.join("\r\n",responseHeaders);
            output.write(String.join("\r\n\r\n",httpHead,read("./Main.java")).getBytes("utf-8"));
            socket.close();
          } else if (path.equals("/register")){
            String[] responseHeaders = {
              "HTTP/1.1 200 Found",
              "Content-Type: text/html; charset=utf-8"
            };
            output.write(String.join("\r\n",responseHeaders).concat("\r\n\r\n").concat(read("./sites/register.html")).getBytes("utf-8"));
            socket.close();
          } else if (path.equals("/login")){
            String[] responseHeaders = {
              "HTTP/1.1 200 OK",
              "Content-Type: text/html; charset=utf-8"
            };
            output.write()
            output.write(String.join("\r\n\r\n",String.join("\r\n",responseHeaders),read("./sites/login.html")).getBytes("utf-8"));
            socket.close();
          } else {
            send(404,socket);
          }
        } else if (method == "POST"){
          if(contains(path, "/login")){
            File database = new File("./data.db");
            if(database.exists()){
              String[] responseHeaders = {
                "HTTP/1.1 200 OK",
                "Content-Type: text/html; "
              };
            } else {
              String[] responseHeaders = {
                "HTTP/1.1 500 Internal Server Error"
              };
              output.write(String.join("\r\n",responseHeaders).concat("\r\n").getBytes("utf-8"));
              socket.close();
            }
          } else {
            send(404,socket);
          }
        }
      } catch(Exception e) {
        System.out.println(e);
        try {
          Socket socket = server.accept();
          socket.getOutputStream().write("HTTP/1.1 500 Internal Server Error\r\nContent-Type:text/html; charset=utf-8\r\nConnection:close\r\n\r\nHello,World!".getBytes("utf-8"));
          socket.close();
        } catch(Exception e2){
          System.out.println("Kill me");
        }
      }
    }
  }
}
