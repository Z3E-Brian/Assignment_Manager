package org.una.programmingIII.Assignment_Manager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.una.programmingIII.Assignment_Manager.Model.Permission;
import org.una.programmingIII.Assignment_Manager.Model.PermissionType;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Permission findByName(PermissionType name);
}