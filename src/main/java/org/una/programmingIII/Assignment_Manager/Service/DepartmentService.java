package org.una.programmingIII.Assignment_Manager.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.una.programmingIII.Assignment_Manager.Dto.DepartmentDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DepartmentService {

    List<DepartmentDto> getAllDepartments();

    List<DepartmentDto> getDepartmentsByFacultyId(Long facultyId);

    Map<String, Object> getDepartments(int page, int size, int limit);

    Page<DepartmentDto> getPageDepartments(Pageable pageable);

    Optional<DepartmentDto> getById(Long id);

    DepartmentDto create(DepartmentDto departmentDto);

    Optional<DepartmentDto> update(Long id, DepartmentDto departmentDto);

    void delete(Long id);
}
