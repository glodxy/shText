package classes.thread;

import classes.net.Connector;

import javax.annotation.Nullable;

public class RegistThread extends NThread {
    String a,p,u;

    public RegistThread(String act,String psw,String uname,@Nullable OnFinishListener listener){
        super(listener);
        a=act;
        p=psw;
        u=uname;

    }

    @Override
    protected Object execute() throws Exception {
        int uid= Connector.Regist(a,p,u);
        return uid;
    }
}
