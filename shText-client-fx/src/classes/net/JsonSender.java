package classes.net;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonSender {
    private static String session=null;

    public static String send(String u, JSONObject jobj){
        try{
            URL url=new URL(u);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            if(session!=null){
                connection.setRequestProperty("Cookie",session);
            }
            connection.connect();

            OutputStream out = new DataOutputStream(connection.getOutputStream()) ;
            // 写入请求的字符串
            if(jobj!=null)
                out.write((jobj.toString()).getBytes());
            out.flush();
            out.close();

            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                InputStream in=connection.getInputStream();
                try{
                    String temp;
                    StringBuffer sb=new StringBuffer();
                    BufferedReader bf=new BufferedReader(new InputStreamReader(in));
                    while((temp=bf.readLine())!=null){
                        sb.append(temp);
                    }
                    String s=connection.getHeaderField("Set-Cookie");
                    System.out.println(sb.toString());
                    if(s!=null)
                        session=s.substring(0,s.indexOf(";"));
                    bf.close();
                    return sb.toString();
                }catch(IOException e){
                    System.out.println("response read error");
                }
            }
            else{
                System.out.println("response:"+connection.getResponseCode());
                System.out.println("error:"+connection.getResponseMessage());
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void clearSession(){
        session=null;
    }

    public static String getSession(){return session;}

}
