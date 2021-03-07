package buffer;

import domain.World;

public class WorldContentBuffer extends SimpleBuffer {
    private int bid;

    public WorldContentBuffer(World world){
        super(world.getWid(),world.getWcontent(),world.getWname());
        bid=world.getBid();
    }

    public int getBid(){return bid;}
    public void setBid(int b){bid=b;}
}
