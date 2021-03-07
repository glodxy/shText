package domain;

public class World {
    private int wid;
    private String wname;
    private String wcontent;
    private int bid;

    public void setWid(int id){wid=id;}
    public int getWid(){return wid;}

    public void setWname(String name){wname=name;}
    public String getWname(){return wname;}

    public void setWcontent(String c){wcontent=c;}
    public String getWcontent(){return wcontent;}

    public void setBid(int b){bid=b;}
    public int getBid(){return bid;}
}
