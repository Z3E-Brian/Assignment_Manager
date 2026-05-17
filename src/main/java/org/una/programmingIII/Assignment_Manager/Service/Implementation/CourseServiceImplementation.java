package org.una.programmingIII.Assignment_Manager.Service.Implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.una.programmingIII.Assignment_Manager.Dto.CourseDto;
import org.una.programmingIII.Assignment_Manager.Model.Course;
import org.una.programmingIII.Assignment_Manager.Model.User;
import org.una.programmingIII.Assignment_Manager.Repository.CourseRepository;
import org.una.programmingIII.Assignment_Manager.Repository.UserRepository;
import org.una.programmingIII.Assignment_Manager.Service.CourseService;

import jakarta.persistence.EntityNotFoundException;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapper;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapperFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImplementation implements CourseService {


    private final CourseRepository courseRepository;
    private final GenericMapper<Course, CourseDto> courseMapper;
    private final UserRepository userRepository;

    @Autowired
    public CourseServiceImplementation(GenericMapperFactory mapperFactory, UserRepository userRepository, CourseRepository courseRepository) {
        this.courseMapper = mapperFactory.createMapper(Course.class, CourseDto.class);
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public CourseDto create(CourseDto courseDto) {
        Course course = courseMapper.convertToEntity(courseDto);
        course = courseRepository.save(course);
        return courseMapper.convertToDTO(course);
    }

    @Override
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getCourses(int page, int size, int limit) {
        Page<Course> coursePage = courseRepository.findAll(PageRequest.of(page, size));

        coursePage.forEach(course -> {
            course.setUsers(limitSetOrDefault(course.getUsers(), limit));
        });

        Map<String, Object> response = new HashMap<>();
        response.put("courses", coursePage.map(this::convertToDto).getContent());
        response.put("totalPages", coursePage.getTotalPages());
        response.put("totalElements", coursePage.getTotalElements());
        return response;
    }

    @Override
    public Page<CourseDto> getPageCourses(Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAll(pageable);
        return coursePage.map(courseMapper::convertToDTO);
    }

    @Override
    public Optional<CourseDto> getById(Long id) {
        return courseRepository.findById(id)
                .map(courseMapper::convertToDTO);
    }

    @Override
    public Optional<CourseDto> update(Long id, CourseDto courseDto) {
        return courseRepository.findById(id)
                .map(existingCourse -> {
                    Course updatedCourse = courseMapper.convertToEntity(courseDto);
                    updatedCourse.setId(id);
                    Course savedCourse = courseRepository.save(updatedCourse);
                    return Optional.of(courseMapper.convertToDTO(savedCourse));
                })
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id " + id));
    }

    @Override
    public void delete(Long id) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            course.get().setCareer(null);
            course.get().setProfessor(null);
            course.get().getUsers().clear();
            courseRepository.save(course.get());
            courseRepository.delete(course.get());
        } else {
            throw new EntityNotFoundException("Course not found with id " + id);
        }

    }

    @Override
    public List<CourseDto> findCoursesEnrolledByStudentId(Long studentId) {
        return courseRepository.findCoursesEnrolledByStudentId(studentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDto> findAvailableCoursesByCareerIdAndUserId(Long careerId, Long userId) {
        return courseRepository.findAvailableCoursesByCareerIdAndUserId(careerId, userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void enrollStudent(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id " + courseId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

        course.getUsers().add(user); // Añadir el estudiante al curso
        courseRepository.save(course); // Guardar el curso actualizado
    }

    @Override
    public void unenrollStudent(Long courseId, Long userId) {
        courseRepository.unenrollCourseUser(userId, courseId);
    }

    @Override
    public List<CourseDto> findAvailableCoursesByCareerIdUserIdAndProfessorId(Long professorId, Long studentId) {
        return courseRepository.findAvailableCoursesByCareerIdUserIdAndProfessorId(professorId, studentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDto> findCoursesEnrolledByStudentIdAAndProfessorIs(Long professorId, Long studentId) {
        return courseRepository.findCoursesEnrolledByStudentIdAAndProfessorIs(professorId, studentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDto> findCoursesByProfessorId(Long professorId) {
        return courseRepository.findCoursesByProfessorId(professorId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<CourseDto> getCoursesByCareerId(Long careerId) {
        return courseRepository.findByCareerId(careerId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private <T> List<T> limitListOrDefault(List<T> list, int limit) {
        return list == null ? new ArrayList<>() : limitList(list, limit);
    }

    private <T> List<T> limitList(List<T> list, int limit) {
        return list.stream().limit(limit).collect(Collectors.toList());
    }

    private CourseDto convertToDto(Course course) {
        CourseDto courseDto = courseMapper.convertToDTO(course);
        List<Long> usersId = course.getUsers().stream()
                .map(user -> user.getId())
                .collect(Collectors.toList());
        courseDto.setStudentsId(usersId);
        return courseDto;
    }

    private Set<User> limitSetOrDefault(Set<User> originalSet, int limit) {
        if (originalSet.size() <= limit) {
            return originalSet;
        }

        return originalSet.stream()
                .limit(limit)
                .collect(Collectors.toSet());
    }
}
