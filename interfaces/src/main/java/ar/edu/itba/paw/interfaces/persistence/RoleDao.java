package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Role;

import java.util.Collection;
import java.util.Optional;

public interface RoleDao {

    Optional<Role> findRoleById(long id);

    Optional<Role> findRoleByName(String name);

    Collection<Role> findRolesByName(Collection<String> roleNames);

    Collection<Role> getAllRoles();
}
