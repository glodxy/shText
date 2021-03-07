package buffer;

import java.util.List;

public class SimpleBuffer {
    protected StringBuffer buffer;
    protected List<Integer> userList;
    protected int id;
    protected String name;

    public SimpleBuffer(int i,String s,String n){
        id=i;
        buffer=new StringBuffer(s);
        name=n;
    }

    public int getId(){return id;}
    public void setId(int i){id=i;}

    public String getName(){return name;}
    public void setName(String n){name=n;}

    public String getContent(){return buffer.toString();}

    //添加用户，代表其进入访问
    public void addUser(int id){
        for(int i:userList){
            if(i==id)
                return;
        }
        userList.add(id);
    }

    //移除用户，代表其停止访问
    public void removeUser(int id) {
        for (int i : userList) {
            if (i == id) {
                userList.remove(i);
                return;
            }
        }
    }

}
