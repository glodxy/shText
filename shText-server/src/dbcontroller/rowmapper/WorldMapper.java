package dbcontroller.rowmapper;

import domain.World;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorldMapper implements RowMapper<World> {
    @Override
    public World mapRow(ResultSet resultSet, int i) throws SQLException {
        World world=new World();
        world.setWid(resultSet.getInt("wid"));
        world.setWname(resultSet.getString("wname"));
        world.setWcontent(resultSet.getString("wcontent"));
        world.setBid(resultSet.getInt("bid"));
        return world;
    }
}
