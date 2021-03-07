package classes.domain;




public class Book{
    private static final long serialVersionUID=20171595L;
    private int bid;
    private String title;
    private int count;
    private int charNum;

    public int getBid(){
        return bid;
    }

    public void setBid(int i){
        bid=i;
    }

    public String getBtitle(){
        return title;
    }

    public void setBtitle(String t){
        title=t;
    }

    public int getBcount(){
        return count;
    }

    public void setBcount(int c){
        count=c;
    }

    public int getBcharNum(){
        return charNum;
    }

    public void setBcharNum(int c){
        charNum=c;
    }

}
