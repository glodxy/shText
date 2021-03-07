package classes.parser;


import org.json.JSONException;
import org.json.JSONObject;

public class ResultObject {
    boolean success;
    String reason=null;
    Object content;
    String c;
    public ResultObject(String s){
        JSONObject obj=new JSONObject(s);
        c=s;
        success=obj.getInt("result")==1;
        if(!success){
            reason=obj.getString("reason");
        }else{
            try {
                content = obj.get("content");
            }catch(JSONException e){
                System.out.println("result no content");
                content=null;
            }
        }
    }

    public Object getKey(String key){
        JSONObject obj=new JSONObject(c);
        return obj.get(key);
    }

    public boolean isSuccess(){
        return success;
    }

    public String getReason(){
        return reason;
    }

    public Object getContent(){
        return content;
    }
}
