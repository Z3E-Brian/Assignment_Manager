package org.una.programmingIII.Assignment_Manager.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.una.programmingIII.Assignment_Manager.Dto.FacultyDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FacultyService {
    
    List<FacultyDto> getAllFaculties();

    List<FacultyDto> getFacultiesByUniversityId(Long universityId);

    Map<String, Object> getFaculties(int page, int size, int limit);

    Page<FacultyDto> getPageFaculty(Pageable pageable);

    Optional<FacultyDto> getById(Long id);

    FacultyDto create(FacultyDto facultyDto);

    Optional<FacultyDto> update(Long id, FacultyDto facultyDto);

    void delete(Long id);
}
