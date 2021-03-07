package dbcontroller.rowmapper;

import domain.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(ResultSet resultSet, int i) throws SQLException {
        Role role=new Role();
        role.setRid(resultSet.getInt("rid"));
        role.setRname(resultSet.getString("rname"));
        role.setRsex(resultSet.getString("rsex"));
        role.setRcontent(resultSet.getString("rcontent"));
        role.setBid(resultSet.getInt("bid"));
        return role;
    }
}
