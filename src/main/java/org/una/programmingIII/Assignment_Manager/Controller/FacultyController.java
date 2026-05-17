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
import org.una.programmingIII.Assignment_Manager.Dto.FacultyDto;
import org.una.programmingIII.Assignment_Manager.Exception.BlankInputException;
import org.una.programmingIII.Assignment_Manager.Exception.CustomErrorResponse;
import org.una.programmingIII.Assignment_Manager.Exception.ElementNotFoundException;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapperFactory;
import org.una.programmingIII.Assignment_Manager.Service.FacultyService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {
    private final FacultyService facultyService;

    @Autowired
    public FacultyController(FacultyService facultyService, GenericMapperFactory mapperFactory) {
        this.facultyService = facultyService;
    }

    @Operation(summary = "Get all faculties", description = "Retrieves all faculties.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Faculties retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("getAllFaculties")
    public ResponseEntity<List<FacultyDto>> getFaculties() {
        List<FacultyDto> universities = facultyService.getAllFaculties();
        return new ResponseEntity<>(universities, HttpStatus.OK);
    }

    @Operation(summary = "Get faculties map", description = "Retrieves faculties in a map format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Faculties retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("getMap")
    public ResponseEntity<Map<String, Object>> getFaculties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "5") int limit) {
        Map<String, Object> response = facultyService.getFaculties(page, size, limit);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get faculties pageable", description = "Retrieves faculties in a pageable format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Faculties retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("getByUniversityId/{universityId}")
    public ResponseEntity<List<FacultyDto>> getFacultiesByUniversityId(@PathVariable Long universityId) {
        List<FacultyDto> faculties = facultyService.getFacultiesByUniversityId(universityId);
        return new ResponseEntity<>(faculties, HttpStatus.OK);
    }

    @GetMapping("getPageable")
    public Page<FacultyDto> getAllUniversities(Pageable pageable) {
        return facultyService.getPageFaculty(pageable);
    }

    @Operation(summary = "Get faculty by ID", description = "Retrieves a faculty by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Faculty retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Faculty not found",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @GetMapping("/getById")
    public ResponseEntity<?> getFacultyById(@RequestParam Long id) {
        try {
            Optional<FacultyDto> facultyDto = facultyService.getById(id);
            return new ResponseEntity<>(facultyDto, HttpStatus.OK);
        } catch (ElementNotFoundException ex) {
            return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create faculty", description = "Creates a new faculty.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Faculty created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @PostMapping("/create")
    public ResponseEntity<?> createUniversity(@RequestBody FacultyDto facultyInput) {
        try {
            FacultyDto createdFaculty = facultyService.create(facultyInput);
            return new ResponseEntity<>(createdFaculty, HttpStatus.CREATED);
        } catch (BlankInputException e) {
            return new ResponseEntity<>(new CustomErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Update faculty", description = "Updates an existing faculty by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Faculty updated successfully"),
            @ApiResponse(responseCode = "404", description = "Faculty not found",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUniversity(@PathVariable Long id, @RequestBody FacultyDto facultyInput) {
        try {
            Optional<FacultyDto> updatedFaculty = facultyService.update(id, facultyInput);
            return updatedFaculty.map(ResponseEntity::ok)
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (ElementNotFoundException ex) {
            return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete faculty", description = "Deletes a faculty by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Faculty deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

