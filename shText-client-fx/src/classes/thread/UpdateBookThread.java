package classes.thread;

import classes.domain.Book;
import classes.net.Connector;

import javax.annotation.Nullable;

public class UpdateBookThread extends NThread {
    private Book book;
    public UpdateBookThread(Book b, @Nullable OnFinishListener listener){
        super(listener);
        book=b;
    }

    @Override
    protected Object execute() throws Exception {
        return Connector.UpdateBook(book);
    }
}
