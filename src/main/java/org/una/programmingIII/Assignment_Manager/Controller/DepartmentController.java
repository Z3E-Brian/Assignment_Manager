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
import org.una.programmingIII.Assignment_Manager.Dto.DepartmentDto;
import org.una.programmingIII.Assignment_Manager.Exception.BlankInputException;
import org.una.programmingIII.Assignment_Manager.Exception.CustomErrorResponse;
import org.una.programmingIII.Assignment_Manager.Exception.ElementNotFoundException;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapperFactory;
import org.una.programmingIII.Assignment_Manager.Service.DepartmentService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService, GenericMapperFactory mapperFactory) {
        this.departmentService = departmentService;
    }

    @Operation(summary = "Get all departments", description = "Retrieves all departments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departments retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("getAllDepartments")
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> universities = departmentService.getAllDepartments();
        return new ResponseEntity<>(universities, HttpStatus.OK);
    }

    @Operation(summary = "Get departments map", description = "Retrieves departments in a map format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departments retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("getByFacultyId/{facultyId}")
    public ResponseEntity<List<DepartmentDto>> getDepartmentsByFacultyId(@PathVariable Long facultyId) {
        List<DepartmentDto> departments = departmentService.getDepartmentsByFacultyId(facultyId);
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @GetMapping("getMap")
    public ResponseEntity<Map<String, Object>> getDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "5") int limit) {
        Map<String, Object> response = departmentService.getDepartments(page, size, limit);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get departments pageable", description = "Retrieves departments in a pageable format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departments retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("getPageable")
    public Page<DepartmentDto> getDepartments(Pageable pageable) {
        return departmentService.getPageDepartments(pageable);
    }

    @Operation(summary = "Get department by ID", description = "Retrieves a department by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        try {
            Optional<DepartmentDto> departmentDto = departmentService.getById(id);
            System.out.println(departmentDto);
            return new ResponseEntity<>(departmentDto, HttpStatus.OK);
        } catch (ElementNotFoundException ex) {
            return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create department", description = "Creates a new department.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Department created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @PostMapping("/")
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentDto departmentInput) {
        try {
            DepartmentDto createdUniversity = departmentService.create(departmentInput);
            return new ResponseEntity<>(createdUniversity, HttpStatus.CREATED);
        } catch (BlankInputException e) {
            return new ResponseEntity<>(new CustomErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Update department", description = "Updates an existing department by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department updated successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDto departmentInput) {
        try {
            Optional<DepartmentDto> updatedDepartment = departmentService.update(id, departmentInput);
            return updatedDepartment.map(ResponseEntity::ok)
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (ElementNotFoundException ex) {
            return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete department", description = "Deletes a department by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Department deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
