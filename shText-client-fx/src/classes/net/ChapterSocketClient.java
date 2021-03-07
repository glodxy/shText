package classes.net;

import classes.domain.Operation;
import classes.domain.User;
import classes.domain.UserCursor;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import classes.parser.JsonParser;
import classes.parser.ResultObject;

import java.net.URI;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChapterSocketClient extends WebSocketClient{
    StringBuffer netString;
    StringBuffer localString;

    //存储已完成的操作
    Stack<Operation> operations;
    //存储未完成的操作
    List<Operation> tempOperations;
    private SocketListener listener;
    private UpdateListener ulistener;

    final private Lock operationLock=new ReentrantLock();

    ChapterSocketClient client;

    int cid;
    boolean running=false;
    boolean taskAvailable=true;

    private Timer timer;
    private TimerTask task;

    //上一次更新前端文本的时间
    long lastTime=0;
    public ChapterSocketClient(URI uri, Map<String,String> map,int id,SocketListener l){
        super(uri,map);
        listener=l;
        this.cid=id;
        client=this;
        timer=new Timer();
        operations=new Stack<>();
        tempOperations=new ArrayList<>();

        task=new TimerTask() {
            @Override
            public void run() {
                while (!client.getReadyState().equals(ReadyState.OPEN)) {
                    try{
                        Thread.sleep(1);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                if(taskAvailable) {
                    JSONObject obj = new JSONObject();
                    obj.put("action", "edit");
                    obj.put("cid", cid);
                    synchronized (operationLock) {
                        obj.put("ol", JsonParser.toJSONArray(tempOperations));
                        tempOperations.clear();
                    }
                    send(obj.toString());
                }
            }
        };
    }

    public void addUpdateListener(UpdateListener l){
        ulistener=l;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        listener.onOpen();
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        listener.onClose();
    }

    @Override
    public void onError(Exception e) {
        listener.onError(e.getMessage());
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
        ResultObject obj=new ResultObject(s);
        if(obj.isSuccess()){
            //如果不是更新操作
            if(obj.getKey("action").equals("start")){
                String result= JsonParser.parseForString(obj.getContent());
                netString=new StringBuffer(result);
                localString=new StringBuffer(netString);
                running=true;
                listener.onConnectionStart(localString);
                //得到返回后开始运行
                timer.schedule(task,1000,1000);
            }
            else if(obj.getKey("action").equals("update")){
                JSONObject temp=(JSONObject)obj.getContent();
                List<User> users=JsonParser.parseForUserList(temp.get("ul"));
                List<UserCursor> userCursors=JsonParser.parseForUserCursorList(temp.get("ucl"));
                List<Operation> operations=JsonParser.parseForOperationList(temp.get("ol"));
                for(int i=0;i<operations.size();++i){
                    for(int j=i+1;j<operations.size();++j){
                        //当非本人操作时，进行偏移值更新
                        if(operations.get(j).getUid()!=operations.get(i).getUid()&&operations.get(j).getPosition()>operations.get(i).getPosition()){
                            if(operations.get(i).getOpt()==1){
                                operations.get(j).setPosition(operations.get(j).getPosition()+operations.get(i).getOpc().length());
                            }
                            else if(operations.get(i).getOpt()==2){
                                operations.get(j).setPosition(operations.get(j).getPosition()-operations.get(i).getOpc().length());
                            }
                        }
                    }
                    execute(netString,operations.get(i));
                    this.operations.push(operations.get(i));
                }
                if(tempOperations.isEmpty()) {
                    if(ulistener!=null)
                        ulistener.beforeUpdate();
                    localString.setLength(0);
                    localString.append(netString);
                    listener.onMessageUpdated(localString, userCursors);
                    if(ulistener!=null)
                        ulistener.afterUpdate();
                }
//                    lastTime=curTime;
//                }
            }
        }
    }

    public void addOperation(Operation operation){
            execute(localString, operation);
        synchronized (operationLock) {
            if(!tempOperations.isEmpty()) {
                Operation lastOpt = tempOperations.get(tempOperations.size() - 1);
                if (lastOpt.getOpt() == operation.getOpt()) {
                    if (lastOpt.getOpt() == 1 && lastOpt.getPosition() + lastOpt.getOpc().length() == operation.getPosition()) {
                        lastOpt.setOpc(lastOpt.getOpc() + operation.getOpc());
                        return;
                    } else if (lastOpt.getOpt() == 2 && lastOpt.getPosition() - lastOpt.getOpc().length() == operation.getPosition()) {
                        lastOpt.setOpc(operation.getOpc() + lastOpt.getOpc());
                        return;
                    }
                }
            }
            tempOperations.add(operation);
        }
    }


    public void move(int pos){
        JSONObject obj=new JSONObject();
        obj.put("action","move");
        obj.put("cid",cid);
        obj.put("pos",pos);
        send(obj.toString());
    }

    //开始对cid章节的编辑
    public void start(){
        JSONObject obj=new JSONObject();
        obj.put("action","start");
        obj.put("cid",cid);
        send(obj.toString());
    }

    public void end(){
        timer.cancel();
        running=false;
        JSONObject obj=new JSONObject();
        obj.put("action","end");
        obj.put("cid",cid);
        send(obj.toString());
        System.out.println("end called");
    }

    public void setSendOperationDisabled(boolean r){
        taskAvailable=!r;
    }

    public boolean isRunning(){
        return running;
    }

    public void execute(StringBuffer sb, Operation opt){
        switch (opt.getOpt()){
            case 1:
            {
                sb.insert(opt.getPosition(),opt.getOpc());
                break;
            }
            case 2:
            {
                sb.delete(opt.getPosition()-opt.getOpc().length(),opt.getPosition());
                break;
            }
        }
    }

    public String content(){
        return localString.toString();
    }

    public interface UpdateListener{
        public void beforeUpdate();
        public void afterUpdate();
    }

    public interface SocketListener{
        public void onMessageUpdated(StringBuffer newText,List<UserCursor> cursorPosition);
        public void onConnectionStart(StringBuffer text);
        public void onOpen();
        public void onClose();
        public void onError(String reason);
    }
}
