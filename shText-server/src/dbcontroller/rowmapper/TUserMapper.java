package dbcontroller.rowmapper;

import domain.TUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TUserMapper implements RowMapper<TUser> {
    @Override
    public TUser mapRow(ResultSet resultSet, int i) throws SQLException {
        TUser user=new TUser();
        user.setUid(resultSet.getInt("uid"));
        user.setUname(resultSet.getString("uname"));
        user.setPermission(resultSet.getInt("permission"));
        return user;
    }
}
