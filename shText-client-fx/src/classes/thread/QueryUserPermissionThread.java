package classes.thread;

import classes.domain.Book;
import classes.domain.TUser;
import classes.net.Connector;

import javax.annotation.Nullable;
import java.util.List;

public class QueryUserPermissionThread extends NThread {
    private Book book;

    public QueryUserPermissionThread(Book b, @Nullable OnFinishListener listener){
        super(listener);
        book=b;
    }

    @Override
    protected Object execute() throws Exception {
        List<TUser> ul=Connector.QueryUserPermission(book);
        return ul;
    }
}
