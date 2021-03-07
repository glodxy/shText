package buffer;


import java.util.HashMap;
import java.util.Map;

/*
* 章节缓存
* 用于缓存章节修改
* */
public class ChapterBuffer {
    static ChapterBuffer chapterBuffer=null;

    //存储已有的内容缓存
    private Map<Integer,ChapterContentBuffer> contentBufferMap;


    private ChapterBuffer(){
        contentBufferMap=new HashMap<>();
    }

    public ChapterContentBuffer getChapterContentByCid(int cid){
        return contentBufferMap.get(cid);
    }

    public void addChapterContent(int cid,ChapterContentBuffer ccb){
        contentBufferMap.put(cid,ccb);
    }

    public void removeUser(int uid){
        for(ChapterContentBuffer ccb:contentBufferMap.values())
            ccb.removeUser(uid);
    }


    public static ChapterBuffer getChapterBuffer(){
        if(chapterBuffer==null)
            chapterBuffer=new ChapterBuffer();
        return chapterBuffer;
    }
}
