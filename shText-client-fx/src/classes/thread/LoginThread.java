package classes.thread;

import classes.net.Connector;

import javax.annotation.Nullable;

public class LoginThread extends NThread {
    String a,p;


    public LoginThread(String act, String psw, @Nullable OnFinishListener listener){
        super(listener);
        a=act;
        p=psw;

    }

    @Override
    protected Object execute() throws Exception {
        int uid = Connector.Login(a, p);
        return uid;
    }
}
