package classes.thread;

import classes.net.Connector;

import javax.annotation.Nullable;

public class AddChapterThread extends NThread {
    private int cseq,bid;
    private String title;
    public AddChapterThread(String t,int b,int c, @Nullable OnFinishListener listener){
        super(listener);
        cseq=c;
        title=t;
        bid=b;
    }

    @Override
    protected Object execute() throws Exception {
        return Connector.AddChapter(title,bid,cseq);
    }
}
