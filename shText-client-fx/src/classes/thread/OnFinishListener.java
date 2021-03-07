package classes.thread;

public interface OnFinishListener{
    public void onSuccess(Object result);
    public void onFailed(String reason);
}
