package dbcontroller;

import dbcontroller.rowmapper.BookMapper;
import domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


/*
* 封装book相关的数据库操作
* */
public class BookDBController {

    private JdbcTemplate jt;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }

    public List<Book> queryAllBook(){
        String sql="select * from book";
        try{
            List<Book> bl=jt.query(sql,new BookMapper());
            return bl;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<Book> queryBookByName(String name){
        String sql="select * from book where btitle like '%"+name+"%'";
        try{
            List<Book> bl=jt.query(sql,new BookMapper());
            return bl;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public Book queryBookByBid(int bid){
        String sql="select * from book where bid="+bid;
        try{
            Book book=jt.queryForObject(sql,new BookMapper());
            return book;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public Integer queryBookCount(){
        String sql="select count(*) from book";
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

    //更新书名
    public boolean updateBookName(Book book){
        String sql="update book set btitle=? where bid=?";
        return jt.update(sql,new Object[]{book.getBtitle(),book.getBid()})==1;
    }

    //更新书籍信息
    public boolean updateBook(Book book){
        String sql="update book set bcount=(select count(*) from chapter where chapter.bid=?),bcharNum=(select sum(chapter.ccharNum) from chapter where chapter.bid=?) where book.bid=?";
        Object obj[]=new Object[]{book.getBid(),book.getBid(),book.getBid()};
        return jt.update(sql,obj)==1;
    }

    public boolean insertBook(Book book){
        String sql="insert into book(bid,btitle,bcount,bcharNum) values(?,?,0,0)";
        Object obj[]=new Object[]{book.getBid(),book.getBtitle()};
        int type[]=new int[]{Types.INTEGER,Types.NVARCHAR};
        return jt.update(sql,obj,type)==1;
    }

    public boolean deleteBook(int bid){
        String sql="delete from book where bid=?";
        Object obj[]=new Object[]{bid};
        return jt.update(sql,obj)==1;
    }
}
