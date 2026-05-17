package org.una.programmingIII.Assignment_Manager.Service.Implementation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.una.programmingIII.Assignment_Manager.Dto.FacultyDto;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapper;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapperFactory;
import org.una.programmingIII.Assignment_Manager.Model.Faculty;
import org.una.programmingIII.Assignment_Manager.Repository.FacultyRepository;
import org.una.programmingIII.Assignment_Manager.Service.FacultyService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyServiceImplementation implements FacultyService {
    @Autowired
    private FacultyRepository facultyRepository;
    private final GenericMapper<Faculty, FacultyDto> facultyMapper;


    public FacultyServiceImplementation(GenericMapperFactory mapperFactory) {
        this.facultyMapper = mapperFactory.createMapper(Faculty.class, FacultyDto.class);
    }

    @Override
    public List<FacultyDto> getFacultiesByUniversityId(Long universityId) {
        return facultyRepository.findByUniversityId(universityId).stream()
                .map(facultyMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FacultyDto create(FacultyDto facultyDto) {
        Faculty faculty = facultyMapper.convertToEntity(facultyDto);
        faculty = facultyRepository.save(faculty);
        return facultyMapper.convertToDTO(faculty);
    }

    @Override
    public List<FacultyDto> getAllFaculties() {
        return facultyRepository.findAll().stream()
                .map(facultyMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getFaculties(int page, int size, int limit) {
        Page<Faculty> facultyPage = facultyRepository.findAll(PageRequest.of(page, size));
        facultyPage.forEach(faculty -> {
            faculty.setDepartments(limitListOrDefault(faculty.getDepartments(), limit));
        });

        Map<String, Object> response = new HashMap<>();
        response.put("faculties", facultyPage.map(this::convertToDto).getContent());
        response.put("totalPages", facultyPage.getTotalPages());
        response.put("totalElements", facultyPage.getTotalElements());
        return response;
    }

    @Override
    public Page<FacultyDto> getPageFaculty(Pageable pageable) {
        Page<Faculty> facultyPage = facultyRepository.findAll(pageable);
        return facultyPage.map(facultyMapper::convertToDTO);
    }

    @Override
    public Optional<FacultyDto> getById(Long id) {
        return facultyRepository.findById(id)
                .map(facultyMapper::convertToDTO);
    }

    @Override
    public Optional<FacultyDto> update(Long id, FacultyDto facultyDto) {
        return facultyRepository.findById(id)
                .map(existingFaculty -> {
                    Faculty updatedFaculty = facultyMapper.convertToEntity(facultyDto);
                    updatedFaculty.setId(id);
                    Faculty savedFaculty = facultyRepository.save(updatedFaculty);
                    return Optional.of(facultyMapper.convertToDTO(savedFaculty));
                })
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id " + id));
    }

    @Override
    public void delete(Long id) {
        if (facultyRepository.existsById(id)) {
            facultyRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Faculty not found with id " + id);
        }
    }

    private <T> List<T> limitListOrDefault(List<T> list, int limit) {
        return list == null ? new ArrayList<>() : limitList(list, limit);
    }

    private <T> List<T> limitList(List<T> list, int limit) {
        return list.stream().limit(limit).collect(Collectors.toList());
    }

    private FacultyDto convertToDto(Faculty faculty) {
        return facultyMapper.convertToDTO(faculty);
    }
}
