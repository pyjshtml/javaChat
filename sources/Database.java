package sources;
import java.io.*;
import java.util.*;
public class Database{
  public File file;
  public Database(String filename){
    try{
      this.file = new File(filename);
    } catch(Exception e){
      e.printStackTrace();
    }
  }
  public String read() {
    String content = "";
    try {
      if(!file.exists()) throw new FileNotFoundException();
      Scanner myReader = new Scanner(this.file);
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        content += data;
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    return content;
  }
  public void write(String text){
    try{
      FileWriter writer = new FileWriter(file);
      writer.write(text);
      writer.close();
    } catch(Exception e){
      e.printStackTrace();
    }
  }
  public void insert(Map<String, String> values){
    try{
      FileWriter writer = new FileWriter(file,true);
      ArrayList<String> content = new ArrayList<>();
      for(Map.Entry<String, String> entry : values.entrySet()){
        content.add(entry.toString());
      }
      writer.write("{");
      writer.write(String.join(",",content));
      writer.write("}\r\n");
      writer.close();
    } catch (Exception e){
      e.printStackTrace();
    }
  }
  public void clear(){
    try{
      FileWriter writer = new FileWriter(file);
      writer.write("");
      writer.close();
    } catch(Exception e){
      e.printStackTrace();
    }
  }
  public boolean isEmpty(){
    return this.read().length() == 0;
  }
  public ArrayList<Map<String, String>> find(){
    if(this.isEmpty()){
      return new ArrayList<Map<String, String>>();
    }
    String text = this.read();
    StringBuilder sb = new StringBuilder(text).deleteCharAt(text.lastIndexOf("}"));
    while(sb.indexOf("{") >= 0){
      sb.deleteCharAt(sb.indexOf("{"));
    }
    ArrayList<Map<String, String>> content = new ArrayList<>(sb.toString().split("}").length);
    for(String myObj : sb.toString().split("}")){
      Map<String, String> subContent = new HashMap<>();
      for(String pair : myObj.split(",")){
        subContent.put(pair.split("=")[0],pair.split("=")[1]);
      }
      content.add(subContent);
    }
    return content;
  }
  public ArrayList<Map<String, String>> find(Map<String, String> query) throws Exception{
    ArrayList<Map<String, String>> content = this.find();
    ArrayList<Map<String, String>> res = new ArrayList<>();
    for(Map<String, String> map : content){
      boolean poss = true;
      for(Map.Entry<String, String> entry : query.entrySet()){
        if(!map.containsKey(entry.getKey()) || !map.get(entry.getKey()).equals(entry.getValue())){
          poss = false;
          break;
        }
      }
      if(poss){
        res.add(map);
      }
    }
    return res;
  }
}
