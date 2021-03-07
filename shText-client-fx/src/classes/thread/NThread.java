package classes.thread;

import javax.annotation.Nullable;

public abstract class NThread extends Thread {
    protected OnFinishListener finishListener;

    public NThread(@Nullable OnFinishListener listener){
        if(listener!=null)
            setFinishListener(listener);
    }

    public void setFinishListener(OnFinishListener listener){
        finishListener=listener;
    }

    protected  abstract Object execute()throws Exception;

    @Override
    public void run() {
        try {
            Object obj=execute();
            if(finishListener!=null)
                finishListener.onSuccess(obj);
        } catch (Exception ec) {
            if(finishListener!=null)
                finishListener.onFailed(ec.getMessage());
        }
    }
}
