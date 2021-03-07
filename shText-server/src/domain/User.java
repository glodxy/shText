package domain;

public class User {
    private int uid;
    private String account;
    private String password;
    private String uname;


    public int getUid(){return uid;}
    public void setUid(int id){uid=id;}

    public String getAccount(){return account;}
    public void setAccount(String a){account=a;}

    public String getPassword(){return password;}
    public void setPassword(String p){password=p;}

    public String getUname(){return uname;}
    public void setUname(String n){uname=n;}
}
