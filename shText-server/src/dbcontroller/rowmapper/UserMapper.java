package dbcontroller.rowmapper;

import domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user=new User();
        user.setUid(resultSet.getInt("uid"));
        user.setAccount(resultSet.getString("act"));
        user.setPassword(resultSet.getString("psw"));
        user.setUname(resultSet.getString("uname"));
        return user;
    }
}
