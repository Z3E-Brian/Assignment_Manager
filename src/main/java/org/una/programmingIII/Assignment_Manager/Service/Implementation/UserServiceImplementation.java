package org.una.programmingIII.Assignment_Manager.Service.Implementation;

import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.una.programmingIII.Assignment_Manager.Dto.Input.UserInput;
import org.una.programmingIII.Assignment_Manager.Dto.PermissionDto;
import org.una.programmingIII.Assignment_Manager.Dto.UserDto;
import org.una.programmingIII.Assignment_Manager.Exception.BlankInputException;
import org.una.programmingIII.Assignment_Manager.Exception.DuplicateEmailException;
import org.una.programmingIII.Assignment_Manager.Exception.ElementNotFoundException;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapper;
import org.una.programmingIII.Assignment_Manager.Mapper.GenericMapperFactory;
import org.una.programmingIII.Assignment_Manager.Model.Career;
import org.una.programmingIII.Assignment_Manager.Model.Permission;
import org.una.programmingIII.Assignment_Manager.Model.User;
import org.una.programmingIII.Assignment_Manager.Repository.CareerRepository;
import org.una.programmingIII.Assignment_Manager.Repository.UserRepository;
import org.una.programmingIII.Assignment_Manager.Service.CareerService;
import org.una.programmingIII.Assignment_Manager.Service.PasswordEncryptionService;
import org.una.programmingIII.Assignment_Manager.Service.UserService;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    private final GenericMapper<User, UserDto> userMapper;
    private final GenericMapper<User, UserInput> userInputMapper;
    private final GenericMapper<Permission, PermissionDto> permissionMapper;
    private final CareerRepository careerRepository;

    @Autowired
    public UserServiceImplementation(GenericMapperFactory mapperFactory, PasswordEncryptionService passwordEncryptionService,
                                     UserRepository userRepository, CareerRepository careerRepository) {
        this.userMapper = mapperFactory.createMapper(User.class, UserDto.class);
        this.userInputMapper = mapperFactory.createMapper(User.class, UserInput.class);
        this.userRepository = userRepository;
        this.careerRepository = careerRepository;
        this.passwordEncryptionService = passwordEncryptionService;
        this.permissionMapper = mapperFactory.createMapper(Permission.class, PermissionDto.class);
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::convertToDTO);
    }


    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getUsers(int page, int size, int limit) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
        Map<String, Object> response = new HashMap<>();
        response.put("universities", userPage.map(this::convertToDto).getContent());
        response.put("totalPages", userPage.getTotalPages());
        response.put("totalElements", userPage.getTotalElements());
        return response;
    }

    @Override
    public Page<UserDto> getPaseUsers(Pageable pageable) {
        Page<User> userDtoPage = userRepository.findAll(pageable);
        return userDtoPage.map(userMapper::convertToDTO);
    }

    @Override
    public Optional<UserDto> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ElementNotFoundException("User with email " + email + " not found");
        }
        return Optional.of(userMapper.convertToDTO(user));
    }

    @Override
    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ElementNotFoundException("User with email " + email + " not found");
        }
        return user;
    }

    @Override
    public UserDto createUser(UserInput userInput) {
        if (checkImportantSpaces(userInput)) {
            throw new BlankInputException("Important spaces have blank inputs or are not included.");
        }

        if (userRepository.findByEmail(userInput.getEmail()) != null) {
            throw new DuplicateEmailException("Email " + userInput.getEmail() + " is already in use.");
        }
        User user = userInputMapper.convertToEntity(userInput);
        if (userInput.getId() != null) {
            Career career = careerRepository.getById(userInput.getCareerId());
            if (career != null) {
                user.setCareer(career);
            } else {
                user.setCareer(null);
            }
        }
        user.setPassword(passwordEncryptionService.encodePassword(userInput.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setLastUpdate(LocalDateTime.now());
        user.setActive(false);
        User savedUser = userRepository.save(user);
        return userMapper.convertToDTO(savedUser);
    }

    @Override
    public Optional<UserDto> updateUser(Long id, UserInput userInput) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (!existingUser.getEmail().equals(userInput.getEmail())) {
                        if (userRepository.findByEmail(userInput.getEmail()) != null) {
                            throw new DuplicateEmailException("Email " + userInput.getEmail() + " is already in use.");
                        }
                    }
                    existingUser.setName(userInput.getName());
                    existingUser.setEmail(userInput.getEmail());
                    existingUser.setLastUpdate(LocalDateTime.now());
                    existingUser.setLastName(userInput.getLastName());
                    existingUser.setSecondLastName(userInput.getSecondLastName());
                    existingUser.setActive(userInput.isActive());
                    Set<Permission> permissions = userInput.getPermissions().stream()
                            .map(permissionMapper::convertToEntity)
                            .collect(Collectors.toSet());
                    existingUser.setPermissions(permissions);

                    if (userInput.getCareerId() != null) {
                        Career career = careerRepository.findById(userInput.getCareerId())
                                .orElseThrow(() -> new ElementNotFoundException("Career with ID " + userInput.getCareerId() + " not found"));
                        existingUser.setCareer(career);
                    } else {
                        existingUser.setCareer(null);
                    }

                    User savedUser = userRepository.save(existingUser);
                    return Optional.of(userMapper.convertToDTO(savedUser));
                })
                .orElseThrow(() -> new ElementNotFoundException("User with ID " + id + " not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new OpenApiResourceNotFoundException("User not found"));
        user.getPermissions().clear();
        user.setCareer(null);
        user.setCourses(null);
        userRepository.save(user);
        userRepository.delete(user);
    }

    @Override
    public UserDto activateUser(Long id) {
        try {
            Optional<User> existingUser = userRepository.findById(id);
            if (existingUser.isPresent()) {
                User usuario = existingUser.get();
                usuario.setActive(true);
                userRepository.save(usuario);
                return userMapper.convertToDTO(usuario);
            } else {
                throw new ElementNotFoundException("User with id:{" + id + "} not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error activating user with id: " + id, e);
        }
    }

    @Override
    public List<UserDto> getUsersByPermission(String permission) {
        List<User> users = (List<User>) userRepository.findByPermissionName(permission);
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(userMapper.convertToDTO(user));
        }
        return userDtos;
    }

    @Override
    public Page<UserDto> findStudentsByCareerId(Long careerId, Pageable pageable) {
        Page<User> studentsPage = userRepository.findStudentsByCareerId(careerId, pageable);
        return studentsPage.map(userMapper::convertToDTO);
    }

    private boolean checkImportantSpaces(UserInput userInput) {
        return userInput.getName().isBlank() || userInput.getEmail().isBlank() || userInput.getPassword().isBlank();
    }

    private <T> List<T> limitListOrDefault(List<T> list, int limit) {
        return list == null ? new ArrayList<>() : limitList(list, limit);
    }

    private <T> List<T> limitList(List<T> list, int limit) {
        return list.stream().limit(limit).collect(Collectors.toList());
    }

    private UserDto convertToDto(User user) {
        return userMapper.convertToDTO(user);
    }

    private Permission convertToEntity(PermissionDto permission) {
        return permissionMapper.convertToEntity(permission);
    }

}
