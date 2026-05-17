package org.una.programmingIII.Assignment_Manager.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.una.programmingIII.Assignment_Manager.Model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query(value = "SELECT u.* FROM users u JOIN user_permissions up ON u.id = up.user_id JOIN permissions p ON up.permission_id = p.id WHERE p.name = :permission", nativeQuery = true)
    List<User> findByPermissionName(@Param("permission") String permission);

    @Query(value = "SELECT u.* FROM users u JOIN user_permissions up " +
            "ON u.id = up.user_id JOIN permissions p ON up.permission_id = p.id " +
            "WHERE p.name = 'TAKE_CLASSES' AND u.career_id=:careerId " +
            "ORDER BY u.last_name ASC", nativeQuery = true)
    Page<User> findStudentsByCareerId(@Param("careerId") Long careerId, Pageable pageable);
}

