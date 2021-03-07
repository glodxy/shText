package controller;


import buffer.ChapterBuffer;
import buffer.ChapterContentBuffer;
import buffer.UserBuffer;
import dbcontroller.ChapterDBController;
import dbcontroller.UserBookDBController;
import dbcontroller.UserDBController;
import domain.Operation;
import domain.User;
import domain.UserCursor;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.ui.context.Theme;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChapterContentSocketHandler extends Controller implements WebSocketHandler {

    private  Map<Integer,WebSocketSession> users=new HashMap<>();
    private  Map<Integer,MThread> threads=new HashMap<>();
    private  Map<Integer,Integer> userObj=new HashMap<>();

    private ChapterDBController cb=(ChapterDBController)context.getBean("chapterDB");
    private UserBookDBController ucb=(UserBookDBController)context.getBean("user_bookDB");
    private UserDBController uc=(UserDBController)context.getBean("userDB");
    //连接建立后
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {

        users.put((int)webSocketSession.getAttributes().get("uid"),webSocketSession);
    }

    //连接断开后
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        int uid=(int)webSocketSession.getAttributes().get("uid");
        users.remove(uid);
        //当uid有操作章节且未退出时,做退出操作
        if(userObj.get(uid)!=null)
        {
            System.out.println("auto exit called");
            handleLeaveEdit(userObj.get(uid),uid);
            MThread thread=threads.get(uid);
            thread.exit=true;
            threads.remove(uid);
            userObj.remove(uid);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    //处理信息
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        JSONObject jobj=new JSONObject(webSocketMessage.getPayload().toString());
        switch ((String)jobj.get("action")){
            //获得开始信息后,开始定时传输更新信息
            case "start":{
                int uid=(int)webSocketSession.getAttributes().get("uid");
                handleStartEdit((int)jobj.get("cid"),uid);
                int cid=jobj.getInt("cid");
                userObj.put(uid,cid);
                Thread thread=threads.get(uid);
                if(thread==null){
                    System.out.println("thread "+cid+" created");
                    MThread nt=new MThread(cid,uid);
                    nt.start();
                    threads.put(uid,nt);
                }
                break;
            }

            case "end":{
                int uid=(int)webSocketSession.getAttributes().get("uid");
                handleLeaveEdit((int)jobj.get("cid"),uid);
                MThread thread=threads.get(uid);
                thread.exit=true;
                threads.put(uid,null);
                userObj.remove(uid);

                break;
            }

            case "edit":{
                int uid=(int)webSocketSession.getAttributes().get("uid");
                int cid=jobj.getInt("cid");
                List<Operation> operations=parseForOperationList(jobj.getJSONArray("ol"));
                handleEdit(uid,cid,operations);
                break;
            }

            case "move":{
                int uid=(int)webSocketSession.getAttributes().get("uid");
                int cid=jobj.getInt("cid");
                int pos=jobj.getInt("pos");
                handleMove(uid,cid,pos);
            }
        }
    }

    //处理错误
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    //传递信息
    private void sendUpdation(int cid,int uid){
        ChapterContentBuffer ccb=cb.queryChapterContent(cid);
            if (ucb.queryUserPermission(uid, ccb.getBid()) != null) {
                List<Integer> ul=ccb.getUserList();
                List<User> us=new ArrayList<>();
                for(int i:ul){
                    if(i!=uid){
                        User temp;
                        if((temp=uc.queryUserById(i))!=null){
                            us.add(temp);
                        }
                    }
                }
                try {
                    synchronized (ccb) {
                        //传递用户和操作信息
                        users.get(uid).sendMessage(getUpdateListResult(us, ccb.getNewOperationList(uid), ccb.getUserCursor()));
                    }
                } catch (IOException e) {
                    System.out.println("handle edit error");
                }
            } else {
                try {
                    users.get(uid).sendMessage(getError(ERROR_USER_PERMISSION));
                } catch (IOException e) {
                    System.out.println("handle start edit error");
                }
            }
    }

    private void handleMove(int uid,int cid,int pos){
        ChapterContentBuffer ccb=cb.queryChapterContent(cid);
        if(ucb.queryUserPermission(uid,ccb.getBid())>1){
            ccb.move(uid,pos);
        }
        else{
            try{
                users.get(uid).sendMessage(getError(ERROR_USER_PERMISSION));
            }catch(IOException e){
                System.out.println("handle start edit error");
            }
        }
    }

    //处理编辑信息
    private void handleEdit(int uid, int cid, List<Operation> opts){
        ChapterContentBuffer ccb=cb.queryChapterContent(cid);
        if(ucb.queryUserPermission(uid,ccb.getBid())>1){
            synchronized (ccb) {
                for (Operation opt : opts) {
                    ccb.execute(opt);
                }
            }
        }
        else{
            try{
                users.get(uid).sendMessage(getError(ERROR_USER_PERMISSION));
            }catch(IOException e){
                System.out.println("handle start edit error");
            }
        }
    }

    //处理开始编辑信息
    private void handleStartEdit(int cid,int uid){
        ChapterContentBuffer ccb=cb.queryChapterContent(cid);
        if(ucb.queryUserPermission(uid,ccb.getBid())!=null){
            ccb.addUser(uid);
            try {
                TextMessage message=getStringResult(ccb.getContent());
                if(message==null||users.get(uid)==null){
                    System.out.println("message null");
                }
                users.get(uid).sendMessage(message);
            }catch(IOException e){
                e.printStackTrace();
                System.out.println("return start text error");
            }
        }
        else{
            try{
                users.get(uid).sendMessage(getError(ERROR_USER_PERMISSION));
            }catch(IOException e){
                System.out.println("handle start edit error");
            }
        }
    }

    //处理离开编辑信息
    private void handleLeaveEdit(int cid,int uid){
        ChapterContentBuffer ccb=cb.queryChapterContent(cid);
        if(ucb.queryUserPermission(uid,ccb.getBid())!=null){
            ccb.removeUser(uid);
            //如果没有用户编辑该章节
            if(ccb.getUserList().isEmpty()){
                System.out.println("all user leave:"+cid);
                //则清除所有缓存
                ccb.clearAllBuffer();
                if(!cb.updateChapterContent(ccb))
                    System.out.println("content:"+ccb.getCid()+" update failed");
            }
        }
        else{
            try{
                users.get(uid).sendMessage(getError(ERROR_USER_PERMISSION));
            }catch(IOException e){
                System.out.println("handle leave edit error");
            }
        }
    }

    private TextMessage getStringResult(String s){
        JSONObject obj=new JSONObject();
        obj.put("action","start");
        obj.put("result",1);
        obj.put("content",s==null?"":s);
        System.out.println(obj.toString());
        return new TextMessage(obj.toString());
    }

    private TextMessage getUpdateListResult(List<User> ul, List<Operation> ol, List<UserCursor> ucl){
        JSONObject o=new JSONObject();
        o.put("result",1);
        o.put("action","update");

        JSONObject content=new JSONObject();

        JSONArray a1=new JSONArray();
        for(User u:ul){
            JSONObject obj=new JSONObject();
            obj.put("uid",u.getUid());
            obj.put("uname",u.getUname());
            a1.put(obj);
        }
        content.put("ul",a1);

        JSONArray array=new JSONArray();
        for(Operation opt:ol){
            JSONObject obj=new JSONObject();
            obj.put("uid",opt.getUid());
            obj.put("position",opt.getPosition());
            obj.put("opt",opt.getOpt());
            obj.put("opc",opt.getOpc());
            array.put(obj);
        }
        content.put("ol",array);

        JSONArray a2=new JSONArray();
        for(UserCursor uc:ucl){
            JSONObject obj=new JSONObject();
            obj.put("uid",uc.getUid());
            obj.put("cposition",uc.getCposition());
            a2.put(obj);
        }
        content.put("ucl",a2);

        o.put("content",content);
        return new TextMessage(o.toString());
    }

    private TextMessage getError(String e){
        JSONObject obj=new JSONObject();
        obj.put("result",0);
        obj.put("reason",e);
        return new TextMessage(obj.toString(),true);
    }

    private Operation parseForOperation(JSONObject obj){
        Operation opt=new Operation();
        try {
            opt.setUid(obj.getInt("uid"));
            opt.setPosition(obj.getInt("position"));
            opt.setOpt(obj.getInt("opt"));
            opt.setOpc(obj.getString("opc"));
            return opt;
        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    private List<Operation> parseForOperationList(JSONArray array){
        List<Operation> ol=new ArrayList<>();
        for(int i=0;i<array.length();++i){
            JSONObject o=array.getJSONObject(i);
            ol.add(parseForOperation(o));
        }
        return ol;
    }

    public class MThread extends Thread{
        public boolean exit=false;
        private int cid,uid;

        public MThread(int c,int u){
            cid=c;
            uid=u;
        }

        @Override
        public void run() {
            while(!exit){
                sendUpdation(cid,uid);
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }


}
