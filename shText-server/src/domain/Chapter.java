package domain;

public class Chapter {

    private int cid;
    private String ctitle;
    private int cseq;
    private int ccharNum;
    private int bid;

    public int getCid(){
        return cid;
    }

    public void setCid(int i){
        cid=i;
    }

    public String getCtitle(){
        return ctitle;
    }

    public void setCtitle(String t){
        ctitle=t;
    }

    public int getCcharNum(){
        return ccharNum;
    }

    public void setCcharNum(int c){
        ccharNum=c;
    }

    public int getCseq(){return cseq;}

    public void setCseq(int s){cseq=s;}

    public int getBid(){
        return bid;
    }

    public void setBid(int b){
        bid=b;
    }

}
