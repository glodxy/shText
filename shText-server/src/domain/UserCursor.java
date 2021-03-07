package domain;

public class UserCursor {
    private int uid;
    private int cposition;

    public UserCursor(int uid,int c){
        this.uid=uid;
        cposition=c;
    }

    public UserCursor(){
        uid=cposition=0;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public int getCposition() {
        return cposition;
    }

    public void setCposition(int cposition) {
        this.cposition = cposition;
    }
}
