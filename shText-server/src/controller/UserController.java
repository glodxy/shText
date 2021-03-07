package controller;


import buffer.UserBuffer;
import com.fasterxml.jackson.databind.util.JSONPObject;
import dbcontroller.UserBookDBController;
import dbcontroller.UserDBController;
import domain.TUser;
import domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping(value = "/user")
public class UserController extends controller.Controller {
    private static final Log logger= LogFactory.getLog(BookController.class);
    private UserDBController uc=(UserDBController)context.getBean("userDB");
    private UserBookDBController ucb=(UserBookDBController)context.getBean("user_bookDB");




    @RequestMapping("/login")
    public @ResponseBody Map<String,Object> loginUser(HttpServletRequest request, @RequestBody Map<String,String> obj){
        String act=obj.get("act");
        String psw=obj.get("psw");
        User user=uc.queryUserByAct(act);
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_LOGIN;
        System.out.println(act+","+psw);
        if(user!=null&&user.getPassword().equals(psw)){
            HttpSession session=request.getSession();
            session.setAttribute(UserBuffer.U_SESSION_NAME,user.getUid());
            map.put("result",1);
            map.put("content",user.getUid());
            return map;
        }
        map.put("result",0);
        map.put("reason",reason);
        return map;
    }

    @RequestMapping("/register")
    public @ResponseBody Map<String,Object> registUser(HttpServletRequest request,@RequestBody Map<String,String> obj){
        String act=obj.get("act");
        String psw=obj.get("psw");
        String uname=obj.get("uname");
        int uid;
        do{
            Random r=new Random();
            uid=r.nextInt();
        } while(uc.queryUserById(uid)!=null);
        Map<String,Object> map=new HashMap<>();
        User user=new User();
        user.setUid(uid);
        user.setAccount(act);
        user.setPassword(psw);
        user.setUname(uname);
        String reason=ERROR_DEFAULT;
        if(uc.queryUserByAct(act)!=null){
            reason=ERROR_ACCOUNT_EXIST;
        }
        else {
            if (uc.insertUser(user)) {
                HttpSession session = request.getSession();
                session.setAttribute(UserBuffer.U_SESSION_NAME, user.getUid());
                map.put("result", 1);
                map.put("content",user.getUid());
                return map;
            }
        }
        map.put("result",0);
        map.put("reason",reason);
        return map;
    }

    @RequestMapping("/update")
    public @ResponseBody Map<String,Object> updateUser(HttpServletRequest request,@RequestBody Map<String,String> obj){
        String uname=obj.get("uname");
        Map<String,Object> map=new HashMap<>();
        HttpSession session=request.getSession();
        String reason=ERROR_DEFAULT;
        if(UserBuffer.isSessionValid(session)) {
            User user = UserBuffer.getUserBySession(session.getId());
            if (user != null) {
                user.setUname(uname);
                if(uc.updateUser(user)) {
                    map.put("result", 1);
                    map.put("content",user.getUid());
                    return map;
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

    @RequestMapping("/logout")
    public @ResponseBody Map<String,Object> logoutUser(HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        HttpSession session=request.getSession();
        String reason=ERROR_SESSIOON_TIMEOUT;
        if(UserBuffer.isSessionValid(session)) {
            String id = session.getId();
            User user = UserBuffer.getUserBySession(id);
            session.invalidate();
            map.put("result", 1);
            map.put("content",user.getUid());
            return map;
        }
        else{
            map.put("result",0);
            map.put("reason",reason);
            return map;
        }
    }

    @RequestMapping("/info")
    public @ResponseBody Map<String,Object> queryUser(HttpServletRequest request,@RequestBody Map<String,String> jobj){
        String act=jobj.get("act");
        Map<String, Object> map = new HashMap<>();
        HttpSession session=request.getSession();
        String reason=ERROR_SESSIOON_TIMEOUT;
        if(UserBuffer.isSessionValid(session)) {
            User user=uc.queryUserByAct(act);
            if(user!=null) {
                TUser u=new TUser(user);
                map.put("result", 1);
                map.put("content", u);
                return map;
            }
            reason=ERROR_USER_NOTEXIST;

        }
        map.put("result",0);
        map.put("reason",reason);
        return map;
    }

    //修改uid对书籍bid的权限为permission
    @RequestMapping("/alter_userpermission")
    public @ResponseBody Map<String,Object> alterUserPermission(HttpServletRequest request,@RequestBody Map<String,Integer> obj){
        int uid=obj.get("uid");
        int bid=obj.get("bid");
        int permission=obj.get("permission");
        Map<String,Object> map=new HashMap<>();
        String reason=ERROR_DEFAULT;
        HttpSession session=request.getSession();
        if(UserBuffer.isSessionValid(session)){
            User user=UserBuffer.getUserBySession(session.getId());
            User us=null;
            if((us=uc.queryUserById(uid))!=null) {
                if (user != null && ucb.queryUserPermission(user.getUid(), bid) == 3) {
                    TUser u=new TUser(us);
                    u.setPermission(permission);
                    if (ucb.queryUserPermission(uid, bid) != null) {
                        if (permission == 0 && ucb.deleteUserBook(uid, bid)) {
                            map.put("result", 1);
                            map.put("content", u);
                            return map;
                        }
                        if (ucb.updateUserBook(uid, bid, permission)) {
                            map.put("result", 1);
                            map.put("content",u);
                            return map;
                        }
                    } else {
                        if (ucb.insertUserBook(uid, bid, permission)) {
                            map.put("result", 1);
                            map.put("content",u);
                            return map;
                        }
                    }
                }
                reason=ERROR_USER_PERMISSION;
                map.put("result",0);
                map.put("reason",reason);
                return map;
            }
            reason=ERROR_USER_NOTEXIST;
            map.put("result",0);
            map.put("reason",reason);
            return map;
        }
        reason=ERROR_SESSIOON_TIMEOUT;
        map.put("result",0);
        map.put("reason",reason);
        return map;
    }
}
