package org.una.programmingIII.Assignment_Manager.Service.Implementation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.una.programmingIII.Assignment_Manager.Dto.DepartmentDto;
import org.una.programmingIII.Assignment_Manager.Model.Department;
import org.una.programmingIII.Assignment_Manager.Repository.DepartmentRepository;
import org.una.programmingIII.Assignment_Manager.Service.DepartmentService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapper;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapperFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImplementation implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final GenericMapper<Department, DepartmentDto> departmentMapper;

    @Autowired
    public DepartmentServiceImplementation(GenericMapperFactory mapperFactory, DepartmentRepository departmentRepository) {
        this.departmentMapper = mapperFactory.createMapper(Department.class, DepartmentDto.class);
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<DepartmentDto> getDepartmentsByFacultyId(Long facultyId) {
        return departmentRepository.findByFacultyId(facultyId).stream()
                .map(departmentMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDto create(DepartmentDto departmentDto) {
        Department department = departmentMapper.convertToEntity(departmentDto);
        department = departmentRepository.save(department);
        return departmentMapper.convertToDTO(department);
    }

    @Override
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getDepartments(int page, int size, int limit) {
        Page<Department> departmentPage = departmentRepository.findAll(PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("departments", departmentPage.map(this::convertToDto).getContent());
        response.put("totalPages", departmentPage.getTotalPages());
        response.put("totalElements", departmentPage.getTotalElements());
        return response;
    }

    @Override
    public Page<DepartmentDto> getPageDepartments(Pageable pageable) {
        Page<Department> departmentPage = departmentRepository.findAll(pageable);
        return departmentPage.map(departmentMapper::convertToDTO);
    }


    @Override
    public Optional<DepartmentDto> getById(Long id) {
        return departmentRepository.findById(id)
                .map(departmentMapper::convertToDTO);
    }

    @Override
    public Optional<DepartmentDto> update(Long id, DepartmentDto departmentDto) {
        return departmentRepository.findById(id)
                .map(existingDepartment -> {
                    Department updatedDepartment = departmentMapper.convertToEntity(departmentDto);
                    updatedDepartment.setId(id);
                    Department savedDepartment = departmentRepository.save(updatedDepartment);
                    return Optional.of(departmentMapper.convertToDTO(savedDepartment));
                })
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id " + id));
    }

    @Override
    public void delete(Long id) {
        if (departmentRepository.existsById(id)) {
            departmentRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Department not found with id " + id);
        }
    }

    private <T> List<T> limitListOrDefault(List<T> list, int limit) {
        return list == null ? new ArrayList<>() : limitList(list, limit);
    }

    private <T> List<T> limitList(List<T> list, int limit) {
        return list.stream().limit(limit).collect(Collectors.toList());
    }

    private DepartmentDto convertToDto(Department department) {
        return departmentMapper.convertToDTO(department);
    }
}
