package dbcontroller.rowmapper;

import domain.Book;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class BookMapper implements RowMapper<Book> {
    @Override
    public Book mapRow(ResultSet resultSet, int i) throws SQLException {
        Book book=new Book();
        book.setBid(resultSet.getInt(1));
        book.setBtitle(resultSet.getString(2));
        book.setBcount(resultSet.getInt(3));
        book.setBcharNum(resultSet.getInt(4));
        return book;
    }
}
