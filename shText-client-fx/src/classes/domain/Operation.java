package classes.domain;

public class Operation {
    int uid;
    //操作位置
    int position;
    //操作类别
    int opt;
    //操作对象
    String opc;

    public void setUid(int u){uid=u;}
    public int getUid(){return uid;}

    public void setPosition(int p){position=p;}
    public int getPosition() {
        return position;
    }

    public void setOpt(int opt) {
        this.opt = opt;
    }
    public int getOpt() {
        return opt;
    }

    public void setOpc(String opc) {
        this.opc = opc;
    }
    public String getOpc() {
        return opc;
    }

    @Override
    public boolean equals(Object obj) {
        Operation o=(Operation)obj;
        return uid==o.uid&&position==o.position&&opt==o.opt&&opc.equals(o.opc);
    }
}
