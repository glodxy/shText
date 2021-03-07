package buffer;

import java.util.HashMap;
import java.util.Map;

public class WorldBuffer {
    static WorldBuffer worldBuffer=null;

    Map<Integer,WorldContentBuffer> contentBuffer;

    private WorldBuffer(){
        contentBuffer=new HashMap<>();
    }

    public WorldContentBuffer getWorldContentBufferByRid(int rid){
        return contentBuffer.get(rid);
    }

    public void addWorldContent(int rid,WorldContentBuffer rcb){
        contentBuffer.put(rid,rcb);
    }

    public static WorldBuffer getWorldBuffer(){
        if(worldBuffer==null)
            worldBuffer=new WorldBuffer();
        return worldBuffer;
    }

    public void removeUser(int uid){
        for(WorldContentBuffer rcb:contentBuffer.values()){
            rcb.removeUser(uid);
        }
    }
}
