package classes.net;

import classes.domain.Book;
import classes.domain.Chapter;
import classes.domain.TUser;
import classes.domain.User;
import classes.parser.JsonParser;
import classes.parser.ResultObject;

import java.util.List;

public class Connector {
    static String url="http://localhost:8080/";
    public static int Login(String act, String psw)throws Exception{
        String s= JsonSender.send(url+"user/login", JsonParser.loginInfoToJSONObject(act,psw));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()) {
            int uid = JsonParser.parseForInt(obj.getContent());
            return uid;
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static int Regist(String act,String psw,String uname) throws Exception{
        String s= JsonSender.send(url+"user/register", JsonParser.registInfoToJSONObject(act,psw,uname));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()) {
            int uid = JsonParser.parseForInt(obj.getContent());
            return uid;
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static List<Book> QueryBook()throws Exception{
        String s=JsonSender.send(url+"book/info",null);
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            List<Book> bl=JsonParser.parseForBookList(obj.getContent());
            return bl;
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static List<Chapter> QueryChapter(Book b)throws Exception{
        String s=JsonSender.send(url+"chapter/info",JsonParser.toJSONObject(b.getBid(),"bid"));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            List<Chapter> cl=JsonParser.parseForChapterList(obj.getContent());
            return cl;
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static List<TUser> QueryUserPermission(Book b)throws Exception{
        String s=JsonSender.send(url+"book/user_permission",JsonParser.toJSONObject(b.getBid(),"bid"));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            List<TUser> ul=JsonParser.parseForTUserList(obj.getContent());
            return ul;
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static TUser UpdatePermission(int bid,int uid,int permission)throws Exception{
        String s=JsonSender.send(url+"user/alter_userpermission",JsonParser.permissionToJSONObject(uid,bid,permission));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            return JsonParser.parseForTUser(obj.getContent());
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static TUser QueryUser(String act)throws Exception{
        String s=JsonSender.send(url+"user/info",JsonParser.toJSONObject(act,"act"));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            return JsonParser.parseForTUser(obj.getContent());
        }else{
            throw new Exception(obj.getReason());
        }
    }


    public static Book UpdateBook(Book book)throws Exception{
        String s=JsonSender.send(url+"book/update_book",JsonParser.toJSONObject(book));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            return JsonParser.parseForBookList(obj.getContent()).get(1);
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static Book DeleteBook(int bid)throws Exception{
        String s=JsonSender.send(url+"book/delete",JsonParser.toJSONObject(bid,"bid"));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            return JsonParser.parseForBook(obj.getContent());
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static Book AddBook(String title)throws Exception{
        Book book=new Book();
        book.setBtitle(title);
        book.setBid(0);
        book.setBcount(0);
        book.setBcharNum(0);
        String s=JsonSender.send(url+"book/add",JsonParser.toJSONObject(book));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            return JsonParser.parseForBook(obj.getContent());
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static Chapter AddChapter(String title,int bid,int cseq)throws Exception{
        String s=JsonSender.send(url+"chapter/add_chapter",JsonParser.chapterInfoToJSONObject(title,bid,cseq));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            return JsonParser.parseForChapter(obj.getContent());
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static Chapter DeleteChapter(int cid)throws Exception{
        String s=JsonSender.send(url+"chapter/delete_chapter",JsonParser.toJSONObject(cid,"cid"));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            return JsonParser.parseForChapter(obj.getContent());
        }else{
            throw new Exception(obj.getReason());
        }
    }

    public static Chapter UpdateChapter(Chapter chapter)throws Exception{
        String s=JsonSender.send(url+"chapter/update_chapter",JsonParser.toJSONObject(chapter));
        if(s==null)
            throw new Exception("网络异常");
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            return JsonParser.parseForChapter(obj.getContent());
        }else{
            throw new Exception(obj.getReason());
        }
    }


}
