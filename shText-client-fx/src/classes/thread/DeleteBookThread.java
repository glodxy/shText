package classes.thread;

import classes.net.Connector;

import javax.annotation.Nullable;

public class DeleteBookThread extends NThread{
    private int bid;

    public DeleteBookThread(int b, @Nullable OnFinishListener listener){
        super(listener);
        bid=b;
    }

    @Override
    protected Object execute() throws Exception {
        return Connector.DeleteBook(bid);
    }
}
