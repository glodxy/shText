package domain;

public class Role {
    private int rid;
    private String rname;
    private String rsex;
    private String rcontent;
    private int bid;

    public void setRid(int id){rid=id;}
    public int getRid(){return rid;}

    public void setRname(String name){rname=name;}
    public String getRname(){return rname;}

    public void setRsex(String sex){rsex=sex;}
    public String getRsex(){return rsex;}

    public void setRcontent(String c){rcontent=c;}
    public String getRcontent(){return rcontent;}

    public void setBid(int b){bid=b;}
    public int getBid(){return bid;}
}
