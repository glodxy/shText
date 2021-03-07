package classes.thread;

import classes.net.Connector;

import javax.annotation.Nullable;

public class QueryUserThread extends NThread {
    private String act;

    public QueryUserThread(String a, @Nullable OnFinishListener listener){
        super(listener);
        act=a;
    }

    @Override
    protected Object execute() throws Exception {
        return Connector.QueryUser(act);
    }
}
