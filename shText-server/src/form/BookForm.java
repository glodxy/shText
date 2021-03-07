package form;

public class BookForm {
    private String title;
    private int count;
    private int charNum;
    public String getTitle(){
        return title;
    }

    public void setTitle(String t){
        title=t;
    }

    public int getCount(){
        return count;
    }

    public void setCount(Integer c){
        count=c.intValue();
    }

    public int getCharNum(){
        return charNum;
    }

    public void setCharNum(Integer c){
        charNum=c.intValue();
    }
}
