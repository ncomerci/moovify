package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.RoleDao;
import ar.edu.itba.paw.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Optional;

@Repository
public class RoleDaoImpl implements RoleDao {

    private static final String ROLES = TableNames.ROLES.getTableName();

    private static final RowMapper<Role> ROLE_ROW_MAPPER = (rs, rowNum) ->
            new Role(rs.getLong("role_id"), rs.getString("role"));

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoleDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<Role> findRoleById(long id) {
        return jdbcTemplate.query("SELECT * FROM " + ROLES + " WHERE " + ROLES + ".role_id = ?",
                new Object[]{ id }, ROLE_ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public Optional<Role> findRoleByName(String name) {
        return jdbcTemplate.query("SELECT * FROM " + ROLES + " WHERE " + ROLES + ".role = ?",
                new Object[]{ name }, ROLE_ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public Collection<Role> findRolesByName(Collection<String> roleNames) {
        final StringBuilder whereBuilder = new StringBuilder().append(" WHERE ").append(ROLES).append(".role IN (");

        for (int i = 0; i < roleNames.size() - 1; i++)
            whereBuilder.append("?, ");

        whereBuilder.append("?)");

        return jdbcTemplate.query("SELECT * FROM " + ROLES + whereBuilder.toString(),
                roleNames.toArray(), ROLE_ROW_MAPPER);
    }

    @Override
    public Collection<Role> getAllRoles() {
        return jdbcTemplate.query("SELECT * FROM " + ROLES, ROLE_ROW_MAPPER);
    }
}
