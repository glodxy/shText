package classes.thread;

import classes.domain.Book;
import classes.domain.Chapter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryInfoThread extends NThread {
    //存储查询结果
    private Map<Book, List<Chapter>> bookInfo;

    //存储线程
    Thread bookQueryTask=null;
    Map<Book,Thread> threads;

    boolean chapterError=false;

    //线程状态
    private Map<Book,Integer> threadState;
    private int bookQueryState=RUNNING;

    private static final int RUNNING=0;
    private static final int END_NO_ERROR=1;
    private static final int END_ERROR=2;
    private static final String BOOK_QUERY_ERROR="book query failed";


    public QueryInfoThread(@Nullable OnFinishListener listener){
        super(listener);
        bookInfo=new HashMap<>();
        threadState=new HashMap<>();
        threads=new HashMap<>();
    }

    @Override
    protected Object execute() throws Exception {
        try{
            update();
            return bookInfo;
        }catch(Exception e){
            //章节错误
            if(chapterError){
                throw new Exception("get chapter:"+e.getMessage().replace(" ",",")+" info failed");
            }
            //其他错误
            throw new Exception(e.getMessage());
        }
    }

    public void update()throws Exception{
        if(bookQueryTask==null) {
            bookQueryTask = new QueryBookThread(new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    synchronized (bookInfo) {
                        List<Book> bl = (List<Book>) result;
                        for (Book b : bl) {
                            bookInfo.put(b, null);
                        }
                    }
                    bookQueryState = END_NO_ERROR;
                    bookQueryTask = null;
                }

                @Override
                public void onFailed(String reason) {
                    bookQueryState = END_ERROR;
                    bookQueryTask = null;
                }
            });
            bookQueryTask.start();
        }
        //等待书籍查询结束
        while(bookQueryState==RUNNING){
            Thread.sleep(1);
        }
        if(bookQueryState==END_ERROR){
            throw new Exception(BOOK_QUERY_ERROR);
        }


        //启动查询章节线程
        for(Book b:bookInfo.keySet()){
            QueryChapterThread qcthread;
            if((threads.get(b))==null) {
                qcthread = new QueryChapterThread(b, new OnFinishListener() {
                    @Override
                    public void onSuccess(Object result) {
                        synchronized (bookInfo) {
                            List<Chapter> cl = (List<Chapter>) result;
                            bookInfo.put(b, cl);
                            threadState.put(b, END_NO_ERROR);
                        }
                        threads.remove(b);
                    }

                    @Override
                    public void onFailed(String reason) {
                        threadState.put(b, END_ERROR);
                        threads.remove(b);
                    }
                });
                threadState.put(b, RUNNING);
                threads.put(b, qcthread);
                qcthread.start();
            }
        }

        while(!queryChapterOver()){
            Thread.sleep(1);
        }
        //直到查询结束
        System.out.println("all over");
        //将查询失败的bid加入字符串
        StringBuffer sb=new StringBuffer();
        for(Book b:threadState.keySet()){
            if(threadState.get(b)==END_ERROR){
                if(sb.length()!=0)
                    sb.append(" ");
                sb.append(String.valueOf(b.getBid()));
            }
        }
        if(sb.length()>0)
        {
            chapterError=true;
            throw new Exception(sb.toString());
        }
    }

    private boolean queryChapterOver(){
        for(int state:threadState.values()){
            if(state==RUNNING)
                return false;
        }
        return true;
    }

    public void stopAll(){
        if(bookQueryTask!=null)
            bookQueryTask.interrupt();
        for(Thread th:threads.values()){
            if(th!=null)
                th.interrupt();
        }
    }
}
