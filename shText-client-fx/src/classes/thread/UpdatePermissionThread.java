package classes.thread;

import classes.net.Connector;

import javax.annotation.Nullable;

public class UpdatePermissionThread extends NThread {
    int bid,uid,permission;
    public UpdatePermissionThread(int bid, int uid,int permission, @Nullable OnFinishListener listener){
        super(listener);
        this.bid=bid;
        this.uid=uid;
        this.permission=permission;
    }

    @Override
    protected Object execute() throws Exception {
        return Connector.UpdatePermission(bid,uid,permission);
    }
}
