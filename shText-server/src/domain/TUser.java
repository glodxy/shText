package domain;

public class TUser {
    private int uid;
    private String uname;
    private int permission;

    public TUser(){}
    public TUser(User u){
        uid=u.getUid();
        uname=u.getUname();
    }

    public String getUname(){return uname;}
    public void setUname(String n){uname=n;}

    public int getUid(){return uid;}
    public void setUid(int id){uid=id;}

    public void setPermission(int permission) {
        this.permission = permission;
    }
    public int getPermission() {
        return permission;
    }
}
