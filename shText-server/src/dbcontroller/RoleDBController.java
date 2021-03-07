package dbcontroller;

import buffer.RoleBuffer;
import buffer.RoleContentBuffer;
import dbcontroller.rowmapper.RoleMapper;
import domain.Role;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class RoleDBController{
    private JdbcTemplate jt;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    public List<Role> queryAllRole(){
        String sql="select * from role";
        try{
            List<Role> r=jt.query(sql,new RoleMapper());
            return r;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<Role> queryRoleByName(String n){
        String sql="select * from role where rname like '%"+n+"%'";
        try{
            List<Role> r=jt.query(sql,new RoleMapper());
            return r;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<Role> queryRoleBySex(String s){
        String sql="select * from role where rsex like '%"+s+"%'";
        try{
            List<Role> r=jt.query(sql,new RoleMapper());
            return r;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<Role> queryRoleByBook(int bid){
        String sql="select * from role where bid="+bid;
        try{
            List<Role> r=jt.query(sql,new RoleMapper());
            return r;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public RoleContentBuffer queryRoleContent(int rid){
        if(RoleBuffer.getRoleBuffer().getRoleContentBufferByRid(rid)!=null)
            return RoleBuffer.getRoleBuffer().getRoleContentBufferByRid(rid);
        else{
            String sql="select rid,rname,rsex,rcoontent,bid from role where rid="+rid;
            try{
                RoleContentBuffer r=jt.queryForObject(sql,new RowMapper<RoleContentBuffer>(){
                    @Override
                    public RoleContentBuffer mapRow(ResultSet resultSet, int i) throws SQLException {
                        Role role=new Role();
                        role.setRid(rid);
                        role.setRname(resultSet.getString("rname"));
                        role.setRsex(resultSet.getString("rsex"));

                        role.setBid(resultSet.getInt("bid"));role.setRcontent(resultSet.getString("rcontent"));
                        RoleContentBuffer rcb=new RoleContentBuffer(role);
                        RoleBuffer.getRoleBuffer().addRoleContent(rid,rcb);
                        return rcb;
                    }
                });
                return r;
            }catch(EmptyResultDataAccessException e){
                return null;
            }
        }
    }

    public Integer queryRoleCount(){
        String sql="select count(*) from role";
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


    public boolean updateRole(RoleContentBuffer rcb){
        String sql="update role set rname=?,rsex=?,rcontent=?,bid=? where rid=?";
        Object obj[]=new Object[]{rcb.getName(),rcb.getRsex(),rcb.getContent(),rcb.getBid(),rcb.getId()};
        return jt.update(sql,obj)==1;
    }

    public boolean updateRoleInfo(Role role){
        String sql="update role set rname=?.rsex=?,bid=? where rid=?";
        Object obj[]=new Object[]{role.getRname(),role.getRsex(),role.getBid(),role.getRid()};
        return jt.update(sql,obj)==1;
    }

    public boolean insertRole(Role role){
        String sql="insert into role(rid,rname,rsex,rcontent,bid) values(?,?,?,?,?)";
        Object obj[]=new Object[]{role.getRid(),role.getRname(),role.getRsex(),role.getRcontent(),role.getBid()};
        int type[]=new int[]{Types.INTEGER,Types.NVARCHAR,Types.NVARCHAR,Types.NVARCHAR,Types.INTEGER};
        return jt.update(sql,obj,type)==1;
    }

    public boolean deleteRole(int rid){
        String sql="delete from role where rid=?";
        return jt.update(sql,rid)==1;
    }
}
