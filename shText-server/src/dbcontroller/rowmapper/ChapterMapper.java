package dbcontroller.rowmapper;

import domain.Chapter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChapterMapper implements RowMapper<Chapter> {
    @Override
    public Chapter mapRow(ResultSet resultSet, int i) throws SQLException {
        Chapter chapter=new Chapter();
        chapter.setCid(resultSet.getInt(1));
        chapter.setCtitle(resultSet.getString(2));
        chapter.setCseq(resultSet.getInt(3));
        chapter.setCcharNum(resultSet.getInt(4));
        chapter.setBid(resultSet.getInt(5));
        return chapter;
    }
}
