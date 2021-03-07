package buffer;

import java.util.HashMap;
import java.util.Map;

public class RoleBuffer {
    static RoleBuffer roleBuffer=null;

    Map<Integer,RoleContentBuffer> contentBuffer;

    private RoleBuffer(){
        contentBuffer=new HashMap<>();
    }

    public RoleContentBuffer getRoleContentBufferByRid(int rid){
        return contentBuffer.get(rid);
    }

    public void addRoleContent(int rid,RoleContentBuffer rcb){
        contentBuffer.put(rid,rcb);
    }

    public static RoleBuffer getRoleBuffer(){
        if(roleBuffer==null)
            roleBuffer=new RoleBuffer();
        return roleBuffer;
    }

    public void removeUser(int uid){
        for(RoleContentBuffer rcb:contentBuffer.values()){
            rcb.removeUser(uid);
        }
    }

}
