package classes.thread;

import classes.domain.Book;
import classes.domain.Chapter;
import classes.net.Connector;

import javax.annotation.Nullable;
import java.util.List;


public class QueryChapterThread extends NThread {
    private Book b;
    public QueryChapterThread(Book book, @Nullable OnFinishListener listener){
        super(listener);
        b=book;
    }

    @Override
    protected Object execute() throws Exception {
        List<Chapter>cl= Connector.QueryChapter(b);
        return cl;
    }
}
