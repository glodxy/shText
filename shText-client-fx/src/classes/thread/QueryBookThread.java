package classes.thread;

import classes.domain.Book;
import classes.net.Connector;

import javax.annotation.Nullable;
import java.util.List;

public class QueryBookThread extends NThread {

    public QueryBookThread(@Nullable OnFinishListener listener){
        super(listener);
    }

    @Override
    protected Object execute() throws Exception {
        List<Book> bl= Connector.QueryBook();
        return bl;
    }
}
