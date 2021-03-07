package classes.domain;

import classes.thread.OnFinishListener;
import classes.thread.QueryInfoThread;

import java.util.*;

public class Data {
    private static QueryInfoThread thread=null;
    private static Map<Book,List<Chapter>> bookInfo=new HashMap<>();

    public static boolean available=false;
    public static boolean failed=false;
    public static String errorReason=null;
    public static int uid;


    public static void update(){
        available=false;
        failed=false;
        if(thread==null) {
            thread = new QueryInfoThread(new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    bookInfo =new TreeMap<>(new BookComparator());
                    bookInfo.putAll((Map<Book,List<Chapter>>)result);
                    available = true;
                    thread=null;
                    System.out.println("update suc");
                }

                @Override
                public void onFailed(String reason) {
                    available = true;
                    failed = true;
                    thread=null;
                    errorReason = reason;
                    System.out.println("update failed");
                }
            });
            thread.start();
        }
    }

    public static List<Book> getBookList(){
        return new ArrayList<>(bookInfo.keySet());
    }

    public static List<Chapter> getChapterList(Book b){
        return bookInfo.get(b);
    }

    public static List<String> getBookNameList(){
        List<String> sl=new ArrayList<>();
        for(Book b:bookInfo.keySet()){
            sl.add(b.getBtitle());
        }
        return sl;
    }

    public static int getBidByName(String title){
        for(Book b:bookInfo.keySet()){
            if(b.getBtitle().equals(title))
                return b.getBid();
        }
        return 0;
    }

    public static int getCidByValue(int bid,String value){
        for(Book b:bookInfo.keySet()){
            if(b.getBid()==bid){
                for(Chapter c:bookInfo.get(b)){
                    if((c.getCseq()+"."+c.getCtitle()).equals(value))
                        return c.getCid();
                }
            }
        }
        return 0;
    }

    public static Book getBookByBid(int bid){
        for(Book b:bookInfo.keySet()){
            if(b.getBid()==bid)
                return b;
        }
        return null;
    }

}

class BookComparator implements Comparator<Book>{
    @Override
    public int compare(Book o1, Book o2) {
        return o1.getBtitle().compareTo(o2.getBtitle());
    }
}
