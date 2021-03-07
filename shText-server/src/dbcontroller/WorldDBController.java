package dbcontroller;

import buffer.WorldContentBuffer;
import dbcontroller.rowmapper.WorldMapper;
import domain.World;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class WorldDBController {
    private JdbcTemplate jt;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    public List<World> queryAllWorld(){
        String sql="select * from world";
        try{
            List<World> w=jt.query(sql,new WorldMapper());
            return w;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<World> queryWorldByName(String n){
        String sql="select * from world where wname like '%"+n+"%'";
        try{
            List<World> w=jt.query(sql,new WorldMapper());
            return w;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<World> queryWorldByBook(int bid){
        String sql="select * from world where bid="+bid;
        try{
            List<World> w=jt.query(sql,new WorldMapper());
            return w;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public Integer queryWorldCount(){
        String sql="select count(*) from world";
        try{
            int i=jt.queryForObject(sql, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getInt(1);
                }
            });
            return i;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }


    public boolean updateWorld(WorldContentBuffer rcb){
        String sql="update world set wname=?,rcontent=?,bid=? where rid=?";
        Object obj[]=new Object[]{rcb.getName(),rcb.getContent(),rcb.getBid(),rcb.getId()};
        return jt.update(sql,obj)==1;
    }

    public boolean updateWorldInfo(World world){
        String sql="update world set wname=?,bid=? where rid=?";
        Object obj[]=new Object[]{world.getWname(),world.getBid(),world.getWid()};
        return jt.update(sql,obj)==1;
    }

    public boolean insertWorld(World world){
        String sql="insert into world(rid,rname,rcontent,bid) values(?,?,?,?)";
        Object obj[]=new Object[]{world.getWid(),world.getWname(),world.getWcontent(),world.getBid()};
        int type[]=new int[]{Types.INTEGER,Types.NVARCHAR,Types.NVARCHAR,Types.INTEGER};
        return jt.update(sql,obj,type)==1;
    }

    public boolean deleteWorld(int rid){
        String sql="delete from world where rid=?";
        return jt.update(sql,rid)==1;
    }
}
