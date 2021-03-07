package classes.thread;

import classes.net.Connector;

import javax.annotation.Nullable;

public class DeleteChapterThread extends NThread {
    private int cid;

    public DeleteChapterThread(int c, @Nullable OnFinishListener listener){
        super(listener);
        cid=c;
    }

    @Override
    protected Object execute() throws Exception {
        return Connector.DeleteChapter(cid);
    }
}
