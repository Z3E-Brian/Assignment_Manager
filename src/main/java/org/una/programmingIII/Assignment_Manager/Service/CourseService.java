
package org.una.programmingIII.Assignment_Manager.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.una.programmingIII.Assignment_Manager.Dto.CourseDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseService {
    List<CourseDto> getAllCourses();

    Map<String, Object> getCourses(int page, int size, int limit);

    Page<CourseDto> getPageCourses(Pageable pageable);

    Optional<CourseDto> getById(Long id);

    CourseDto create(CourseDto courseDto);

    Optional<CourseDto> update(Long id, CourseDto courseDto);

    List<CourseDto> getCoursesByCareerId(Long careerId);

    void delete(Long id);

    List<CourseDto> findCoursesEnrolledByStudentId(Long studentId);

    List<CourseDto> findAvailableCoursesByCareerIdAndUserId(Long careerId, Long userId);

    void enrollStudent(Long courseId, Long userId);
    void unenrollStudent(Long courseId, Long userId);

    List<CourseDto> findAvailableCoursesByCareerIdUserIdAndProfessorId (Long professorId, Long studentId);

    List<CourseDto> findCoursesEnrolledByStudentIdAAndProfessorIs (Long professorId, Long studentId);

    List<CourseDto> findCoursesByProfessorId(Long professorId);

}
