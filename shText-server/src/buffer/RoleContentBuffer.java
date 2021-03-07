package buffer;

import domain.Role;

public class RoleContentBuffer extends SimpleBuffer {
    private String rsex;
    private int bid;

    public RoleContentBuffer(Role role){
        super(role.getRid(),role.getRcontent(),role.getRname());
        rsex=role.getRsex();
        bid=role.getBid();
    }

    public String getRsex(){return rsex;}
    public void setRsex(String s){rsex=s;}

    public int getBid(){return bid;}
    public void setBid(int b){bid=b;}
}
