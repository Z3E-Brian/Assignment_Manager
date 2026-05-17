package org.una.programmingIII.Assignment_Manager.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.una.programmingIII.Assignment_Manager.Dto.CourseDto;
import org.una.programmingIII.Assignment_Manager.Dto.Input.CourseInput;
import org.una.programmingIII.Assignment_Manager.Exception.BlankInputException;
import org.una.programmingIII.Assignment_Manager.Exception.CustomErrorResponse;
import org.una.programmingIII.Assignment_Manager.Exception.ElementNotFoundException;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapper;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapperFactory;
import org.una.programmingIII.Assignment_Manager.Service.CourseService;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final GenericMapper<CourseInput, CourseDto> courseMapper;
    private CourseService courseService;

    @Autowired
    CourseController(CourseService courseService, GenericMapperFactory mapperFactory) {
        this.courseService = courseService;
        this.courseMapper = mapperFactory.createMapper(CourseInput.class, CourseDto.class);
    }

    @Operation(summary = "Get all courses", description = "Retrieves all courses.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("getAllCourses")
    public ResponseEntity<List<CourseDto>> getAllUniversities() {
        List<CourseDto> universities = courseService.getAllCourses();
        return new ResponseEntity<>(universities, HttpStatus.OK);
    }

    @Operation(summary = "Get courses with pagination", description = "Retrieves courses with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("getMap")
    public ResponseEntity<Map<String, Object>> getCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "5") int limit) {
        Map<String, Object> response = courseService.getCourses(page, size, limit);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get courses pageable", description = "Retrieves courses in a pageable format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("getPageable")
    public Page<CourseDto> getCourses(Pageable pageable) {
        return courseService.getPageCourses(pageable);
    }

    @Operation(summary = "Get course by ID", description = "Retrieves a course by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            Optional<CourseDto> courseDto = courseService.getById(id);
            if (!(courseDto.isPresent())) {
                throw new ElementNotFoundException("Course not found");
            }
            return new ResponseEntity<>(courseDto, HttpStatus.OK);
        } catch (ElementNotFoundException ex) {
            return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create course", description = "Creates a new course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @PostMapping("/")
    public ResponseEntity<?> createCourse(@RequestBody CourseDto courseDto) {
        try {
            CourseDto createdCourseDto = courseService.create(courseDto);
            return new ResponseEntity<>(createdCourseDto, HttpStatus.CREATED);
        } catch (BlankInputException e) {
            return new ResponseEntity<>(new CustomErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Update course", description = "Updates an existing course by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course updated successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody CourseInput courseInput) {
        try {
            Optional<CourseDto> updatedCourseDto = courseService.update(id, courseMapper.convertToDTO(courseInput));
            return updatedCourseDto.map(ResponseEntity::ok)
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (ElementNotFoundException ex) {
            return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete course", description = "Deletes a course by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/getByCareerId/{id}")
    public ResponseEntity<?> getCoursesByCareerId(@PathVariable Long id) {
        try {
            List<CourseDto> courses = courseService.getCoursesByCareerId(id);
            if (courses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get courses enrolled by student ID", description = "Retrieves courses that a student is enrolled in by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("/enrolled/{studentId}")
    public ResponseEntity<List<CourseDto>> getCoursesEnrolledByStudentId(@PathVariable Long studentId) {
        List<CourseDto> courses = courseService.findCoursesEnrolledByStudentId(studentId);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @Operation(summary = "Get available courses by career ID and user ID", description = "Retrieves available courses based on career ID and user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No available courses found",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("/available/career/{careerId}/user/{userId}")
    public ResponseEntity<List<CourseDto>> getAvailableCoursesByCareerIdAndUserId(
            @PathVariable Long careerId,
            @PathVariable Long userId) {
        List<CourseDto> courses = courseService.findAvailableCoursesByCareerIdAndUserId(careerId, userId);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @Operation(summary = "Enroll student in a course", description = "Enrolls a student in a specified course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student enrolled successfully"),
            @ApiResponse(responseCode = "404", description = "Course or user not found",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @PostMapping("/enroll/{courseId}/user/{userId}")
    public ResponseEntity<?> enrollStudent(@PathVariable Long courseId, @PathVariable Long userId) {
        try {
            courseService.enrollStudent(courseId, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElementNotFoundException ex) {
            return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Unenroll student from a course", description = "Unenrolls a student from a specified course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student unenrolled successfully"),
            @ApiResponse(responseCode = "404", description = "Course or user not found",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @DeleteMapping("/unenroll/{courseId}/user/{userId}")
    public ResponseEntity<?> unenrollStudent(@PathVariable Long courseId, @PathVariable Long userId) {
        try {
            courseService.unenrollStudent(courseId, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElementNotFoundException ex) {
            return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available/professor/{professorId}/student/{studentId}")
    public ResponseEntity<?> findAvailableCoursesByCareerIdUserIdAndProfessorId(
            @PathVariable Long professorId,
            @PathVariable Long studentId) {
        try {
            List<CourseDto> courses = courseService.findAvailableCoursesByCareerIdUserIdAndProfessorId(professorId, studentId);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/enrolled/professor/{professorId}/student/{studentId}")
    public ResponseEntity<?> findCoursesEnrolledByStudentIdAAndProfessorIs(
            @PathVariable Long professorId,
            @PathVariable Long studentId) {
        try {
            List<CourseDto> courses = courseService.findCoursesEnrolledByStudentIdAAndProfessorIs(professorId, studentId);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<?> findCoursesByProfessorId(
            @PathVariable Long professorId){
        try {
            List<CourseDto> courses = courseService.findCoursesByProfessorId(professorId);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
