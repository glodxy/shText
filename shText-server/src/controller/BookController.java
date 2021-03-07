package controller;

import java.math.BigDecimal;
import java.util.*;

import buffer.UserBuffer;
import dbcontroller.BookDBController;
import dbcontroller.ChapterDBController;
import dbcontroller.UserBookDBController;
import domain.Chapter;
import domain.TUser;
import domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import domain.Book;
import form.BookForm;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/book")
public class BookController extends controller.Controller {
    private static final Log logger=LogFactory.getLog(BookController.class);
    private BookDBController bc=(BookDBController)context.getBean("bookDB");
    private UserBookDBController ubc=(UserBookDBController)context.getBean("user_bookDB");
    private ChapterDBController cc=(ChapterDBController)context.getBean("chapterDB");

    @RequestMapping("/info")
    public @ResponseBody Map<String,Object> form(HttpServletRequest request){
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_DEFAULT;
        HttpSession session=request.getSession();
        if(!UserBuffer.isSessionValid(session))
        {
            logger.info("not login");
            reason=ERROR_SESSIOON_TIMEOUT;
            map.put("result",0);
            map.put("reason",reason);
            return map;
        }
        List<Book> b=ubc.queryBookByUid((int)session.getAttribute(UserBuffer.U_SESSION_NAME));
        if(b!=null)
        {
            map.put("result",1);
            map.put("content",b);
            return map;
        }
        map.put("result",0);
        map.put("reason",reason);
        return map;
    }

    @RequestMapping("/add")
    public @ResponseBody Map<String,Object> addBook(HttpServletRequest request,@RequestBody Book bf){
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_DEFAULT;
        HttpSession session=request.getSession();
        if(UserBuffer.isSessionValid(session)) {
            int uid=(int)session.getAttribute(UserBuffer.U_SESSION_NAME);
            int bid;
            do{
                Random r=new Random();
                bid=r.nextInt();
            } while(bc.queryBookByBid(bid)!=null);
            bf.setBid(bid);
            if (bc.insertBook(bf)&&ubc.insertUserBook(uid,bf.getBid(),3))
            {
                System.out.println("insert success");
                map.put("result",1);
                map.put("content",bf);
                return map;
            }
            map.put("result",0);
            map.put("reason",reason);
            return map;
        }
        reason=ERROR_SESSIOON_TIMEOUT;
        map.put("result",0);
        map.put("reason",reason);
        return map;
    }

    @RequestMapping("/delete")
    public @ResponseBody Map<String,Object> deleteBook(HttpServletRequest request,@RequestBody Map<String ,Integer> obj){
        int bid=obj.get("bid");
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_DEFAULT;
        HttpSession session=request.getSession();
        if(UserBuffer.isSessionValid(session)) {
            int uid=(int)session.getAttribute(UserBuffer.U_SESSION_NAME);
            if(ubc.queryUserPermission(uid,bid)!=null&&ubc.queryUserPermission(uid,bid)==3) {
                Book book = bc.queryBookByBid(bid);
                List<Chapter> cl=cc.queryChapterByBook(bid);
                if (book != null &&deleteChapterList(cl)&& ubc.deleteBook(bid)&&bc.deleteBook(bid) ) {
                    System.out.println("delete book success:" + book.getBtitle());
                    map.put("result", 1);
                    map.put("content", book);
                    return map;
                }
            }else{
                reason=ERROR_USER_PERMISSION;
            }
            map.put("result",0);
            map.put("reason",reason);
            return map;
        }
        reason=ERROR_SESSIOON_TIMEOUT;
        map.put("result",0);
        map.put("reason",reason);
        return map;
    }



    private boolean deleteChapterList(List<Chapter> chapters){
        for(Chapter c:chapters){
            if(!cc.deleteChapter(c.getCid()))
                return false;
        }
        return true;
    }


    @RequestMapping("/update_book")
    public @ResponseBody Map<String,Object> alterBook(HttpServletRequest request,@RequestBody Book a){
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_DEFAULT;
        HttpSession session=request.getSession();
        if(UserBuffer.isSessionValid(session)) {
            int uid=(int)session.getAttribute(UserBuffer.U_SESSION_NAME);
            if(ubc.queryUserPermission(uid,a.getBid())>1) {
                Book book = bc.queryBookByBid(a.getBid());
                if (book != null && bc.updateBookName(a)) {
                    List<Book> l = new ArrayList<>();
                    l.add(book);
                    l.add(a);
                    map.put("result", 1);
                    map.put("content", l);
                    return map;
                }
            }else{
                reason=ERROR_USER_PERMISSION;
            }
            map.put("result",0);
            map.put("reason",reason);
            return map;
        }
        reason=ERROR_SESSIOON_TIMEOUT;
        map.put("result",0);
        map.put("reason",reason);
        return map;
    }

    @RequestMapping("/user_permission")
    public @ResponseBody Map<String,Object> bookUser(HttpServletRequest request,@RequestBody Map<String,Integer> obj){
        int bid=obj.get("bid");
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_DEFAULT;
        HttpSession session=request.getSession();
        if(!UserBuffer.isSessionValid(session)) {
            map.put("result",0);
            map.put("reason",reason);
            return map;
        }
        List<TUser> ul=ubc.queryUserByBid(bid);
        if(ul!=null){
            map.put("result",1);
            map.put("content",ul);
            return map;
        }
        reason=ERROR_SESSIOON_TIMEOUT;
        map.put("result",0);
        map.put("reason",reason);
        return map;
    }

}