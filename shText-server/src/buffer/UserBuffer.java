package buffer;

import dbcontroller.UserDBController;
import domain.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserBuffer {
    public static final String U_SESSION_NAME="userInfo";

    public static Map<String, HttpSession> sessionMap=new HashMap<>();
    private static List<User> userList=new ArrayList<>();


    //在给session添加属性为userInfo时调用
    public static void addUserSession(HttpSessionBindingEvent event){
        HttpSession session=event.getSession();
        for(HttpSession s:sessionMap.values()){
            //当该session值存在且id不同时，即在其他地方登录，则取消已存在的session
            if(event.getValue().equals(s.getAttribute(U_SESSION_NAME))&&session.getId()!=s.getId()){
                sessionMap.remove(s.getId());
                s.invalidate();
                sessionMap.put(session.getId(),session);
                return;
            }
        }
        //检查是否已加入userlist
       for(User u:userList){
           if(session.getAttribute(U_SESSION_NAME).equals(u.getUid()))
               return;
       }
       //否则加入
       UserDBController uc=(UserDBController)new ClassPathXmlApplicationContext("dbcontroller/applicationContext.xml").getBean("userDB");
       userList.add(uc.queryUserById((int)session.getAttribute(U_SESSION_NAME)));
    }


    public static void removeUserSession(HttpSessionBindingEvent event){
        HttpSession session=event.getSession();
        for(HttpSession s:sessionMap.values()){
            //当该session值存在且id不同时，即在其他地方登录，则取消已存在的session
            if(event.getValue().equals(s.getAttribute(U_SESSION_NAME))&&session.getId()!=s.getId()){
                if(session.isNew())
                    session.invalidate();
                return;
            }
        }
        for(User u:userList){
            if(session.getAttribute(U_SESSION_NAME).equals(u.getUid())){
                removeUser(u);

                return;
            }
        }
    }

    public static User getUserBySession(String sessionId){
        HttpSession session=sessionMap.get(sessionId);
        for(User u:userList){
            if(session.getAttribute(U_SESSION_NAME).equals(u.getUid()))
                return u;
        }
        return null;
    }

    //从所有缓冲区中移除该用户，代表其已下线
    private static void removeUser(User u){
        userList.remove(u);
    }

    public static boolean isSessionValid(HttpSession session){
        return session!=null&&session.getAttribute(U_SESSION_NAME)!=null;
    }

}
