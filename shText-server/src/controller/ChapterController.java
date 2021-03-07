package controller;


import buffer.ChapterBuffer;
import buffer.ChapterContentBuffer;
import buffer.UserBuffer;
import dbcontroller.ChapterDBController;
import dbcontroller.UserBookDBController;
import dbcontroller.UserDBController;
import domain.Chapter;
import domain.Operation;
import domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@RequestMapping("/chapter")
@Controller
public class ChapterController extends controller.Controller {
    private static final Log logger= LogFactory.getLog(BookController.class);
    private ChapterDBController cc=(ChapterDBController)context.getBean("chapterDB");
    private UserBookDBController ucb=(UserBookDBController)context.getBean("user_bookDB");
    private UserDBController uc=(UserDBController)context.getBean("userDB");


    //根据bid获取该书的所有章节
    @RequestMapping("/info")
    public @ResponseBody Map<String,Object> getAllChapterInfo(HttpServletRequest request,@RequestBody Map<String,Integer> obj){
        int bid=obj.get("bid");
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_DEFAULT;
        HttpSession session=request.getSession();
        if(UserBuffer.isSessionValid(session)){
            int uid=(int)session.getAttribute(UserBuffer.U_SESSION_NAME);
            if(ucb.queryUserPermission(uid,bid)!=null){
                map.put("result",1);
                map.put("content",cc.queryChapterByBook(bid));
                return map;
            }
            else{
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

    //添加章节信息
    @RequestMapping("/add_chapter")
    public @ResponseBody Map<String,Object> addChapter(HttpServletRequest request, @RequestBody Map<String,Object> obj){
        int bid=(int)obj.get("bid");
        int cseq=(int)obj.get("cseq");
        String ctitle=(String)obj.get("ctitle");
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_DEFAULT;
        HttpSession session=request.getSession();
        if(UserBuffer.isSessionValid(session)){
            int uid=(int)session.getAttribute(UserBuffer.U_SESSION_NAME);
            if(ucb.queryUserPermission(uid,bid)!=null&&ucb.queryUserPermission(uid,bid)>1){
                int cid;
                Random r=new Random();
                do{
                    cid=r.nextInt();
                } while(cc.queryChapterByCid(cid)!=null);
                Chapter chapter=new Chapter();
                chapter.setCid(cid);
                chapter.setCtitle(ctitle);
                chapter.setCseq(cseq);
                chapter.setBid(bid);
                chapter.setCcharNum(0);
                if(cc.insertChapter(chapter))
                {
                    map.put("result",1);
                    map.put("content",chapter);
                    return map;
                }
            }
            else{
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


    @RequestMapping("/delete_chapter")
    public @ResponseBody Map<String,Object> deleteChapter(HttpServletRequest request, @RequestBody Map<String,Integer> obj){
        int cid=obj.get("cid");
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_DEFAULT;
        HttpSession session=request.getSession();
        if(UserBuffer.isSessionValid(session)){
            int uid=(int)session.getAttribute(UserBuffer.U_SESSION_NAME);
            if(cc.queryBidByCid(cid)!=null) {
                int bid=cc.queryBidByCid(cid);
                if (ucb.queryUserPermission(uid, bid) != null && ucb.queryUserPermission(uid, bid) > 1) {
                    Chapter chapter = cc.queryChapterByCid(cid);
                    if (cc.deleteChapter(cid)) {
                        map.put("result", 1);
                        map.put("content", chapter);
                        return map;
                    }
                } else {
                    reason = ERROR_USER_PERMISSION;
                }
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

    @RequestMapping("/update_chapter")
    public @ResponseBody Map<String,Object> updateChapter(HttpServletRequest request, @RequestBody Chapter obj){
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_DEFAULT;
        HttpSession session=request.getSession();
        if(UserBuffer.isSessionValid(session)){
            int uid=(int)session.getAttribute(UserBuffer.U_SESSION_NAME);
            int bid=obj.getBid();
            if(ucb.queryUserPermission(uid,bid)!=null&&ucb.queryUserPermission(uid,bid)>1){
                System.out.println(bid+","+obj.getCid()+","+obj.getCtitle());
                if(cc.updateChapterInfo(obj))
                {
                    map.put("result",1);
                    map.put("content",obj);
                    return map;
                }
            }
            else{
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

//    //获取章节内容
//    @RequestMapping("/content")
//    public @ResponseBody Map<String,Object> getChapterContent(HttpServletRequest request,@RequestBody Map<String,Integer> obj){
//        int cid=obj.get("cid");
//        Map<String,Object> map=new HashMap<>();
//        String reason=ERROR_DEFAULT;
//        HttpSession session=request.getSession();
//        if(UserBuffer.isSessionValid(session)){
//            ChapterContentBuffer ccb=cc.queryChapterContent(cid);
//            int uid=(int)session.getAttribute(UserBuffer.U_SESSION_NAME);
//            if(ucb.queryUserPermission(uid,ccb.getBid())!=null){
//                map.put("result",1);
//                map.put("content",ccb.getContent());
//                return map;
//            }
//            else{
//                reason=ERROR_USER_PERMISSION;
//            }
//            map.put("result",0);
//            map.put("reason",reason);
//            return map;
//        }
//        reason=ERROR_SESSIOON_TIMEOUT;
//        map.put("result",0);
//        map.put("reason",reason);
//        return map;
//    }
//
//    //查询内容更新
//    @RequestMapping("/content_update")
//    public @ResponseBody Map<String,Object> updateChapter(HttpServletRequest request,@RequestBody Map<String,Object> obj){
//        int cid=(int)obj.get("cid");
//        List<Operation> ol=(List<Operation>)obj.get("ol");
//        Map<String,Object> map=new HashMap<>();
//        String reason=ERROR_DEFAULT;
//        HttpSession session=request.getSession();
//        if(UserBuffer.isSessionValid(session)){
//            ChapterContentBuffer ccb=ChapterBuffer.getChapterBuffer().getChapterContentByCid(cid);
//            int uid=(int)session.getAttribute(UserBuffer.U_SESSION_NAME);
//            if(ucb.queryUserPermission(uid,ccb.getBid())!=null){
//                for(Operation opt:ol){
//                    ccb.execute(opt);
//                }
//                map.put("result",0);
//                map.put("content",ccb.getNewOperationList(ol.get(0).getUid()));
//                return map;
//            }
//            else{
//                reason=ERROR_USER_PERMISSION;
//            }
//            map.put("result",1);
//            map.put("reason",reason);
//            return map;
//        }
//        reason=ERROR_SESSIOON_TIMEOUT;
//        map.put("result",0);
//        map.put("reason",reason);
//        return map;
//    }
//
//
//    //查询在线用户
//    @RequestMapping("/user_online")
//    public @ResponseBody Map<String,Object> getOnlineUser(HttpServletRequest request,@RequestBody Map<String,Integer> obj){
//        int cid=obj.get("cid");
//        Map<String,Object> map=new HashMap<>();
//        String reason=ERROR_DEFAULT;
//        HttpSession session=request.getSession();
//        if(UserBuffer.isSessionValid(session)){
//            ChapterContentBuffer ccb=ChapterBuffer.getChapterBuffer().getChapterContentByCid(cid);
//            int uid=(int)session.getAttribute(UserBuffer.U_SESSION_NAME);
//            if(ucb.queryUserPermission(uid,ccb.getBid())!=null){
//                List<Integer> ul=ccb.getUserList();
//                List<User> users=new ArrayList<>();
//                for(int i:ul){
//                    if(i!=uid){
//                        User temp;
//                        if((temp=uc.queryUserById(i))!=null){
//                            users.add(temp);
//                        }
//                    }
//                }
//                map.put("result",1);
//                map.put("content",users);
//                return map;
//
//            }
//            else{
//                reason=ERROR_USER_PERMISSION;
//            }
//            map.put("result",1);
//            map.put("reason",reason);
//            return map;
//        }
//        reason=ERROR_SESSIOON_TIMEOUT;
//        map.put("result",0);
//        map.put("reason",reason);
//        return map;
//    }
}
