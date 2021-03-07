package classes.parser;

import classes.domain.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    public static Book parseForBook(Object obj){
        JSONObject o=(JSONObject)obj;
        Book book=new Book();
        try {
            book.setBid(o.getInt("bid"));
            book.setBtitle(o.getString("btitle"));
            book.setBcharNum(o.getInt("bcharNum"));
            book.setBcount(o.getInt("bcount"));
            return book;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<Book> parseForBookList(Object obj){
        JSONArray array=(JSONArray)obj;
        List<Book> bl=new ArrayList<>();
        for(int i=0;i<array.length();++i){
            JSONObject o=array.getJSONObject(i);
            bl.add(parseForBook(o));
        }
        return bl;
    }

    public static Chapter parseForChapter(Object obj){
        JSONObject o=(JSONObject)obj;
        Chapter chapter=new Chapter();
        try {
            chapter.setCid(o.getInt("cid"));
            chapter.setCtitle(o.getString("ctitle"));
            chapter.setCseq(o.getInt("cseq"));
            chapter.setBid(o.getInt("bid"));
            chapter.setCcharNum(o.getInt("ccharNum"));
            return chapter;
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<Chapter> parseForChapterList(Object obj){
        JSONArray array=(JSONArray)obj;
        List<Chapter> cl=new ArrayList<>();
        for(int i=0;i<array.length();++i){
            JSONObject o=array.getJSONObject(i);
            cl.add(parseForChapter(o));
        }
        return cl;
    }

    public static Operation parseForOperation(Object obj){
        JSONObject o=(JSONObject)obj;
        Operation opt=new Operation();
        try {
            opt.setUid(o.getInt("uid"));
            opt.setPosition(o.getInt("position"));
            opt.setOpt(o.getInt("opt"));
            opt.setOpc(o.getString("opc"));
            return opt;
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<Operation> parseForOperationList(Object obj){
        JSONArray array=(JSONArray)obj;
        List<Operation> ol=new ArrayList<>();
        for(int i=0;i<array.length();++i){
            JSONObject o=array.getJSONObject(i);
            ol.add(parseForOperation(o));
        }
        return ol;
    }

    public static User parseForUser(Object obj){
        JSONObject o=(JSONObject)obj;
        User user=new User();
        try{
            user.setUid(o.getInt("uid"));
            user.setUname(o.getString("uname"));
            return user;
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> parseForUserList(Object obj){
        JSONArray array=new JSONArray();
        List<User> ul=new ArrayList<>();
        for(int i=0;i<array.length();++i){
            ul.add(parseForUser(array.get(i)));
        }
        return ul;
    }

    public static TUser parseForTUser(Object obj){
        JSONObject o=(JSONObject)obj;
        TUser user=new TUser();
        try{
            user.setUid(o.getInt("uid"));
            user.setUname(o.getString("uname"));
            user.setPermission(o.getInt("permission"));
            return user;
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<TUser> parseForTUserList(Object obj){
        JSONArray array=(JSONArray)obj;
        List<TUser> ul=new ArrayList<>();
        for(int i=0;i<array.length();++i){
            ul.add(parseForTUser(array.get(i)));
        }
        return ul;
    }

    public static UserCursor parseForUserCursor(Object obj){
        JSONObject o=(JSONObject)obj;
        UserCursor u=new UserCursor();
        u.setUid(o.getInt("uid"));
        u.setCposition(o.getInt("cposition"));
        return u;
    }

    public static List<UserCursor> parseForUserCursorList(Object obj){
        JSONArray array=(JSONArray)obj;
        List<UserCursor> ul=new ArrayList<>();
        for(int i=0;i<array.length();++i){
            ul.add(parseForUserCursor(array.get(i)));
        }
        return ul;
    }


    public static int parseForInt(Object obj){
        return (int)obj;
    }

    public static String parseForString(Object obj){
        return (String)obj;
    }

    public static JSONObject toJSONObject(Book book){
        JSONObject obj=new JSONObject();
        obj.put("bid",book.getBid());
        obj.put("btitle",book.getBtitle());
        obj.put("bcount",book.getBcount());
        obj.put("bcharNum",book.getBcharNum());
        return obj;
    }

    public static JSONObject toJSONObject(int id,String type){
        JSONObject obj=new JSONObject();
        obj.put(type,id);
        return obj;
    }

    public static JSONObject toJSONObject(String value,String key){
        JSONObject obj=new JSONObject();
        obj.put(key,value);
        return obj;
    }

    public static JSONObject toJSONObject(Chapter chapter){
        JSONObject obj=new JSONObject();
        obj.put("cid",chapter.getCid());
        obj.put("ctitle",chapter.getCtitle());
        obj.put("cseq",chapter.getCseq());
        obj.put("bid",chapter.getBid());
        return obj;
    }

    public static JSONObject loginInfoToJSONObject(String act,String psw){
        JSONObject obj=new JSONObject();
        obj.put("act",act);
        obj.put("psw",psw);
        return obj;
    }

    public static JSONObject registInfoToJSONObject(String act,String psw,String uname){
        JSONObject obj=new JSONObject();
        obj.put("act",act);
        obj.put("psw",psw);
        obj.put("uname",uname);
        return obj;
    }

    public static JSONObject permissionToJSONObject(int uid,int bid,int permission){
        JSONObject obj=new JSONObject();
        obj.put("uid",uid);
        obj.put("bid",bid);
        obj.put("permission",permission);
        return obj;
    }

    public static JSONObject chapterInfoToJSONObject(String title,int bid,int cseq){
        JSONObject obj=new JSONObject();
        obj.put("ctitle",title);
        obj.put("bid",bid);
        obj.put("cseq",cseq);
        return obj;
    }

    public static JSONObject toJSONObject(List<Operation> operations){
        JSONObject o=new JSONObject();
        JSONArray array=new JSONArray();
        for(Operation opt:operations){
            JSONObject obj=new JSONObject();
            obj.put("uid",opt.getUid());
            obj.put("position",opt.getPosition());
            obj.put("opt",opt.getOpt());
            obj.put("opc",opt.getOpc());
            array.put(obj);
        }
        o.put("ol",array);
        return o;
    }

    public static JSONArray toJSONArray(List<Operation> operations){
        JSONArray array=new JSONArray();
        for(Operation opt:operations){
            JSONObject obj=new JSONObject();
            obj.put("uid",opt.getUid());
            obj.put("position",opt.getPosition());
            obj.put("opt",opt.getOpt());
            obj.put("opc",opt.getOpc());
            array.put(obj);
        }
        return array;
    }

}
