package classes.thread;

import classes.domain.Chapter;
import classes.net.Connector;

import javax.annotation.Nullable;

public class UpdateChapterThread extends NThread {
    private int cid,cseq,bid;
    private String title;

    public UpdateChapterThread(int c, int s,int b, String t, @Nullable OnFinishListener listener){
        super(listener);
        cid=c;
        cseq=s;
        title=t;
        bid=b;
    }

    @Override
    protected Object execute() throws Exception {
        Chapter chapter=new Chapter();
        chapter.setBid(bid);
        chapter.setCcharNum(0);
        chapter.setCid(cid);
        chapter.setCseq(cseq);
        chapter.setCtitle(title);
        return Connector.UpdateChapter(chapter);
    }
}
