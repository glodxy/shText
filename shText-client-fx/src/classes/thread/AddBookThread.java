package classes.thread;

import classes.net.Connector;

import javax.annotation.Nullable;

public class AddBookThread extends NThread {
    private String title;

    public AddBookThread(String t,@Nullable OnFinishListener listener){
        super(listener);
        title=t;
    }

    @Override
    protected Object execute() throws Exception {
        return Connector.AddBook(title);
    }
}
