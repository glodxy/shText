package dbcontroller;

import dbcontroller.rowmapper.TUserMapper;
import dbcontroller.rowmapper.UserMapper;
import domain.TUser;
import domain.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class UserDBController {
    private JdbcTemplate jt;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    public User queryUserById(int uid){
        String sql="select * from user_info where uid='"+uid+"'";
        try{
            User u=jt.queryForObject(sql,new UserMapper());
            return u;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }


    public User queryUserByAct(String act){
        String sql="select * from user_info where act='"+act+"'";
        try{
            User u=jt.queryForObject(sql,new UserMapper());
            return u;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public Integer queryUserCount(){
        String sql="select count(*) from user_info";
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

    public boolean updateUser(User user){
        String sql="update user_info set psw=?,uname=? where uid=?";
        Object obj[]=new Object[]{user.getPassword(),user.getUname(),user.getUid()};
        return jt.update(sql,obj)==1;
    }

    public boolean insertUser(User user){
        String sql="insert into user_info(uid,act,psw,uname) values(?,?,?,?)";
        Object obj[]=new Object[]{user.getUid(),user.getAccount(),user.getPassword(),user.getUname()};
        int type[]=new int[]{Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.NVARCHAR};
        return jt.update(sql,obj,type)==1;
    }

    public boolean deleteUser(int uid){
        String sql="delete from user_info where uid=?";
        return jt.update(sql,uid)==1;
    }

}
