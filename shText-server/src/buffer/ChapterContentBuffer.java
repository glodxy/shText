package buffer;

import domain.Operation;

import java.io.*;
import java.util.*;


import domain.UserCursor;
import util.Offset;
import util.OperationTypes;

//单个章节缓存
public class ChapterContentBuffer {
    private int cid;
    private String ctitle;
    private int cseq,bid;
    StringBuffer sb;

    //操作缓存
    Stack<Operation> operationBuffer;
    //撤销缓存，用于redo
    Stack<Operation> redoBuffer;

    //记录在访问中的用户
    List<Integer> userList;

    //记录用户指针位置
    Map<Integer,Integer> pointBuffer;

    //记录用户操作栈位置
    Map<Integer,Integer> user_optBuffer;

    //记录用户的指针偏移值，即所修改位置的偏移值
    Map<Integer, Offset> offsetBuffer;



    public ChapterContentBuffer(String s,int cid){
        this.cid=cid;
        sb=new StringBuffer(s);
        userList=new ArrayList<Integer>();
        operationBuffer=new Stack<>();
        redoBuffer=new Stack<>();
        pointBuffer=new HashMap<>();
        user_optBuffer=new HashMap<>();
        offsetBuffer=new HashMap<>();
    }
    //将内容写入
    public ChapterContentBuffer(InputStream in,int cid){
        this.cid=cid;
        sb=new StringBuffer();
        userList=new ArrayList<Integer>();
        operationBuffer=new Stack<>();
        redoBuffer=new Stack<>();
        pointBuffer=new HashMap<>();
        user_optBuffer=new HashMap<>();
        offsetBuffer=new HashMap<>();
        try{
            byte[] b = new byte[1024];
            int length;
            while ((length = in.read(b)) != -1) {
                sb.append(new String(b,0,length));
            }
        } catch(IOException e){
            System.out.println("text read error");
            e.printStackTrace();
        }
    }


    public String getContent(){
        return sb.toString();
    }

    public int getCid(){return cid;}

    public void setCtitle(String t){ctitle=t;}
    public String getCtitle(){return ctitle;}

    public void setCseq(int i){cseq=i;}
    public int getCseq(){return cseq;}

    public void setBid(int b){bid=b;}
    public int getBid(){return bid;}

    //执行操作
    public void execute(Operation opt){
        System.out.println("type:"+opt.getOpt()+", obj:"+opt.getOpc()+", position:"+opt.getPosition());
        System.out.println("offset:"+opt.getPosition()+offsetBuffer.get(opt.getUid()).getOffset(opt.getPosition()));
        if(!redoBuffer.isEmpty()&&redoBuffer.peek().equals(opt))
            redoBuffer.pop();
        else
            redoBuffer.clear();
        operationBuffer.push(opt);
        switch (opt.getOpt()){
            case OperationTypes.ADD:
            {
                addText(opt.getPosition()+offsetBuffer.get(opt.getUid()).getOffset(opt.getPosition()),opt.getOpc());
                offsetExcept(opt.getUid(),opt.getPosition(),opt.getOpc().length());
                pointBuffer.put(opt.getUid(),opt.getPosition()+offsetBuffer.get(opt.getUid()).getOffset(opt.getPosition())+opt.getOpc().length());
                break;
            }
            case OperationTypes.DELETE:
            {
                deleteText(opt.getPosition()+offsetBuffer.get(opt.getUid()).getOffset(opt.getPosition()),opt.getOpc());
                offsetExcept(opt.getUid(),opt.getPosition(),-opt.getOpc().length());
                pointBuffer.put(opt.getUid(),opt.getPosition()+offsetBuffer.get(opt.getUid()).getOffset(opt.getPosition())-opt.getOpc().length());
                break;
            }
        }
    }

    private void offsetExcept(int uid,int pos,int step){
        List<Integer> idList=new ArrayList<>(offsetBuffer.keySet());

        for(int i:idList){
            if(i!=uid){
                offsetBuffer.get(i).addOffset(pos,step);
            }
        }
    }

    public void move(int uid,int pos){
        pointBuffer.put(uid,offsetBuffer.get(uid).getOffset(pos)+pos);
    }


    //撤销
    public void undo(){
        Operation opt=operationBuffer.pop();
        undoOperation(opt);
        redoBuffer.push(opt);
    }
    //重做
    public void redo(){
        execute(redoBuffer.peek());
    }

    //添加用户，代表其进入访问
    public void addUser(int id){
        for(int i:userList){
            if(i==id)
                return;
        }
        userList.add(id);
        offsetBuffer.put(id,new Offset());
        //将用户指针移至文本末
        pointBuffer.put(id,sb.length());
        //将用户操作指针移至顶部
        user_optBuffer.put(id,operationBuffer.size()>0?operationBuffer.size()-1:0);
    }

    //移除用户，代表其停止访问
    public void removeUser(int id){
        for(int i=0;i<userList.size();++i){
            if(userList.get(i)==id){
                userList.remove(i);
                user_optBuffer.remove(i);
                pointBuffer.remove(i);
                offsetBuffer.remove(i);
                return;
            }
        }
    }

    //撤销操作
    private void undoOperation(Operation opt){
        switch (opt.getOpt()){
            case OperationTypes.ADD:
            {
                deleteText(opt.getPosition()+opt.getOpc().length(),opt.getOpc());
                break;
            }
            case OperationTypes.DELETE:
            {
                addText(opt.getPosition()-opt.getOpc().length(),opt.getOpc());
                break;
            }
        }
    }

    private void addText(int position,String text){
        sb.insert(position,text);
    }

    private void deleteText(int position,String text){
        sb.delete(position-text.length(),position);
    }


    public List<Operation> getNewOperationList(int uid){
        List<Operation> operations=new ArrayList<>();
        for(int i=user_optBuffer.get(uid)+1;i<operationBuffer.size();++i){
            operations.add(operationBuffer.get(i));
        }
        user_optBuffer.put(uid,operationBuffer.size()-1);
        offsetBuffer.get(uid).clear();
        return operations;
    }

    public List<Integer> getUserList(){
        return userList;
    }

    public List<UserCursor> getUserCursor(){
        List<UserCursor> ucl=new ArrayList<>();
        for(int uid:pointBuffer.keySet()){
            ucl.add(new UserCursor(uid,pointBuffer.get(uid)));
        }
        return ucl;
    }

    public void clearAllBuffer(){
        operationBuffer.clear();
        redoBuffer.clear();
        pointBuffer.clear();
        user_optBuffer.clear();
        userList.clear();
    }
}
