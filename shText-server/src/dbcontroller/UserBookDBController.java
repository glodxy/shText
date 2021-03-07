package dbcontroller;

import dbcontroller.rowmapper.BookMapper;
import dbcontroller.rowmapper.TUserMapper;
import dbcontroller.rowmapper.UserMapper;
import domain.Book;
import domain.TUser;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class UserBookDBController {
    private JdbcTemplate jt;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    //使用uid查询书籍
    public List<Book> queryBookByUid(int uid){
        String sql="select book.bid,btitle,bcount,bcharNum from book,user_permission where book.bid=user_permission.bid and user_permission.uid="+uid;
        try{
            List<Book> b=jt.query(sql,new BookMapper());
            return b;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //使用bid查询用户
    public List<TUser> queryUserByBid(int bid){
        String sql="select user_info.uid,uname,permission from user_info,user_permission where user_info.uid=user_permission.uid and user_permission.bid="+bid;
        try{
            List<TUser> u=jt.query(sql,new TUserMapper());
            return u;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public Integer queryUserPermission(int uid,int bid){
        String sql="select permission from user_permission where uid="+uid+" and bid="+bid;
        try{
            int i=jt.queryForObject(sql,new RowMapper<Integer>(){
                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getInt("permission");
                }
            });
            return i;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //更新目标权限
    public boolean updateUserBook(int uid,int bid,int permission){
        String sql="update user_permission set permission=? where uid=? and bid=?";
        Object obj[]=new Object[]{permission,uid,bid};
        return jt.update(sql,obj)==1;
    }

    //添加权限
    public boolean insertUserBook(int uid,int bid,int permission){
        String sql="insert into user_permission(bid,uid,permission) values(?,?,?)";
        Object obj[]=new Object[]{bid,uid,permission};
        int type[]=new int[]{Types.INTEGER,Types.INTEGER,Types.INTEGER};
        return jt.update(sql,obj,type)==1;
    }

    //删除权限
    public boolean deleteUserBook(int uid,int bid){
        String sql="delete from user_permission where uid=? and bid=?";
        Object obj[]=new Object[]{uid,bid};
        return jt.update(sql,obj)==1;
    }

    public boolean deleteBook(int bid){
        String sql="delete from user_permission where bid=?";
        Object obj[]=new Object[]{bid};
        return jt.update(sql,obj)==1;
    }

}
