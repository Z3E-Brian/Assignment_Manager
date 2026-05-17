/**
 * Data Seeder — auto-populates the database with sample data for development/testing.
 *
 * Runs only when the users table is empty (see {@code run()} guard).
 *
 * Seed data overview:
 *
 * ── Hierarchy ──
 * 1 University → 3 Faculties → 5 Departments → 5 Careers
 *
 * ── Users (all passwords: "123456") ──
 * | Role      | Email                          | Permissions (key ones)               |
 * |-----------|--------------------------------|--------------------------------------|
 * | Admin     | admin@test.com                  | GLOBAL_MAINTENANCE, MANAGE_USERS, …  |
 * | Professor | carlos.mendoza@test.com         | TEACH_CLASSES, CREATE_ASSIGNMENTS,…  |
 * | Professor | maria.vargas@test.com           | TEACH_CLASSES, CREATE_ASSIGNMENTS,…  |
 * | Student   | juan.perez@test.com             | TAKE_CLASSES, SUBMIT_ASSIGNMENTS,…   |
 * | Student   | ana.rodriguez@test.com          | TAKE_CLASSES, SUBMIT_ASSIGNMENTS,…   |
 * | Student   | luis.sandoval@test.com          | TAKE_CLASSES, SUBMIT_ASSIGNMENTS,…   |
 * | Student   | sofia.ramirez@test.com          | TAKE_CLASSES, SUBMIT_ASSIGNMENTS,…   |
 *
 * ── Courses ──
 * | Course              | Professor     | Students              |
 * |--------------------|---------------|-----------------------|
 * | Programación III   | C. Mendoza    | Juan, Ana, Luis, Sofía|
 * | Bases de Datos II  | M. Vargas     | Juan, Ana             |
 * | Desarrollo Web     | C. Mendoza    | Luis, Sofía           |
 *
 * ── Assignments ──
 * - Programación III: TASK (lista enlazada), PROJECT (sistema gestión), EXAM (parcial 1)
 * - Bases de Datos II: TASK (modelo relacional), QUIZ (normalización)
 *
 * ── Submissions ──
 * 8 submissions across assignments; some graded, some pending.
 */
package org.una.programmingIII.Assignment_Manager.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.una.programmingIII.Assignment_Manager.Model.*;
import org.una.programmingIII.Assignment_Manager.Repository.*;
import org.una.programmingIII.Assignment_Manager.Service.PasswordEncryptionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private PasswordEncryptionService passwordEncryptionService;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private UniversityRepository universityRepository;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private CareerRepository careerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private CourseContentRepository courseContentRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private AnswerAIRepository answerAIRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        List<Permission> allPermissions = permissionRepository.saveAll(
                List.of(createPermission(PermissionType.CREATE_UNIVERSITIES),
                        createPermission(PermissionType.EDIT_UNIVERSITIES),
                        createPermission(PermissionType.DELETE_UNIVERSITIES),
                        createPermission(PermissionType.VIEW_UNIVERSITIES),
                        createPermission(PermissionType.CREATE_FACULTIES),
                        createPermission(PermissionType.EDIT_FACULTIES),
                        createPermission(PermissionType.DELETE_FACULTIES),
                        createPermission(PermissionType.VIEW_FACULTIES),
                        createPermission(PermissionType.CREATE_DEPARTMENTS),
                        createPermission(PermissionType.EDIT_DEPARTMENTS),
                        createPermission(PermissionType.DELETE_DEPARTMENTS),
                        createPermission(PermissionType.VIEW_DEPARTMENTS),
                        createPermission(PermissionType.CREATE_CAREERS),
                        createPermission(PermissionType.EDIT_CAREERS),
                        createPermission(PermissionType.DELETE_CAREERS),
                        createPermission(PermissionType.VIEW_CAREERS),
                        createPermission(PermissionType.CREATE_COURSES),
                        createPermission(PermissionType.EDIT_COURSES),
                        createPermission(PermissionType.DELETE_COURSES),
                        createPermission(PermissionType.VIEW_COURSES),
                        createPermission(PermissionType.CREATE_USERS),
                        createPermission(PermissionType.EDIT_USERS),
                        createPermission(PermissionType.DELETE_USERS),
                        createPermission(PermissionType.VIEW_USERS),
                        createPermission(PermissionType.MANAGE_PERMISSIONS),
                        createPermission(PermissionType.CREATE_ASSIGNMENTS),
                        createPermission(PermissionType.EDIT_ASSIGNMENTS),
                        createPermission(PermissionType.DELETE_ASSIGNMENTS),
                        createPermission(PermissionType.VIEW_ASSIGNMENTS),
                        createPermission(PermissionType.GRADE_ASSIGNMENTS),
                        createPermission(PermissionType.SUBMIT_ASSIGNMENTS),
                        createPermission(PermissionType.VIEW_GRADES),
                        createPermission(PermissionType.EDIT_PROFILE),
                        createPermission(PermissionType.TEACH_CLASSES),
                        createPermission(PermissionType.SUBMIT_FEEDBACK),
                        createPermission(PermissionType.TAKE_CLASSES),
                        createPermission(PermissionType.REGISTER_STUDENT_COURSES))
        );

        University una = universityRepository.save(
                createUniversity("Universidad Nacional (UNA)", "Heredia, Costa Rica")
        );

        Faculty exactas = facultyRepository.save(
                createFaculty("Facultad de Ciencias Exactas y Naturales", una)
        );
        Faculty sociales = facultyRepository.save(
                createFaculty("Facultad de Ciencias Sociales", una)
            );
        Faculty medicina = facultyRepository.save(
                createFaculty("Facultad de Medicina", una)
        );

        Department informatica = departmentRepository.save(
                createDepartment("Escuela de Informática", exactas)
        );
        Department matematica = departmentRepository.save(
                createDepartment("Escuela de Matemática", exactas)
        );
        Department psicologia = departmentRepository.save(
                createDepartment("Escuela de Psicología", sociales)
        );
        Department trabajoSocial = departmentRepository.save(
                createDepartment("Escuela de Trabajo Social", sociales)
        );
        Department enfermeria = departmentRepository.save(
                createDepartment("Escuela de Enfermería", medicina)
        );

        Career sistemas = careerRepository.save(
                createCareer("Bachillerato en Ingeniería en Sistemas de Información",
                        "Formación en desarrollo de software, bases de datos y redes", informatica)
        );
        Career computacion = careerRepository.save(
                createCareer("Licenciatura en Ciencias de la Computación",
                        "Formación avanzada en algoritmos, inteligencia artificial y teoría computacional", informatica)
        );
        Career estadistica = careerRepository.save(
                createCareer("Bachillerato en Estadística",
                        "Formación en análisis de datos, probabilidad y métodos estadísticos", matematica)
        );
        Career psicologiaC = careerRepository.save(
                createCareer("Bachillerato en Psicología",
                        "Formación en psicología clínica, educativa y organizacional", psicologia)
        );
        Career enfermeriaC = careerRepository.save(
                createCareer("Bachillerato en Enfermería",
                        "Formación en cuidados de enfermería y gestión sanitaria", enfermeria)
        );

        Set<Permission> adminPerms = new java.util.HashSet<>(allPermissions);
        Set<Permission> professorPerms = Set.of(
                allPermissions.get(19), allPermissions.get(28), allPermissions.get(25),
                allPermissions.get(26), allPermissions.get(17), allPermissions.get(33),
                allPermissions.get(29), allPermissions.get(31), allPermissions.get(32),
                allPermissions.get(34)
        );
        Set<Permission> studentPerms = Set.of(
                allPermissions.get(19), allPermissions.get(28), allPermissions.get(30),
                allPermissions.get(31), allPermissions.get(32), allPermissions.get(35)
        );

        User admin = userRepository.save(createUser(
                "Admin", "Sistema", "", "admin@test.com", "123456",
                "000000000", true, adminPerms, null
        ));
        User profCarlos = userRepository.save(createUser(
                "Carlos", "Mendoza", "Vargas", "carlos.mendoza@test.com", "123456",
                "100000001", true, professorPerms, sistemas
        ));
        User profMaria = userRepository.save(createUser(
                "María", "Vargas", "López", "maria.vargas@test.com", "123456",
                "100000002", true, professorPerms, sistemas
        ));
        User juan = userRepository.save(createUser(
                "Juan", "Pérez", "García", "juan.perez@test.com", "123456",
                "200000001", true, studentPerms, sistemas
        ));
        User ana = userRepository.save(createUser(
                "Ana", "Rodríguez", "Mora", "ana.rodriguez@test.com", "123456",
                "200000002", true, studentPerms, sistemas
        ));
        User luis = userRepository.save(createUser(
                "Luis", "Sandoval", "Jiménez", "luis.sandoval@test.com", "123456",
                "200000003", true, studentPerms, sistemas
        ));
        User sofia = userRepository.save(createUser(
                "Sofía", "Ramírez", "Chacón", "sofia.ramirez@test.com", "123456",
                "200000004", true, studentPerms, sistemas
        ));

        Course prog3 = courseRepository.save(createCourse(
                "Programación III", "Estructuras de datos avanzadas y algoritmos",
                profCarlos, sistemas, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 6, 30),
                Set.of(juan, ana, luis, sofia)
        ));
        Course bd2 = courseRepository.save(createCourse(
                "Bases de Datos II", "Diseño y administración de bases de datos relacionales",
                profMaria, sistemas, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 6, 30),
                Set.of(juan, ana)
        ));
        Course web = courseRepository.save(createCourse(
                "Desarrollo Web", "Aplicaciones web con frameworks modernos",
                profCarlos, sistemas, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 6, 30),
                Set.of(luis, sofia)
        ));

        CourseContent syllabusProg3 = courseContentRepository.save(
                createCourseContent("Syllabus - Programación III", prog3)
        );
        CourseContent projectGuide = courseContentRepository.save(
                createCourseContent("Guía del Proyecto Final", prog3)
        );
        CourseContent erdExamples = courseContentRepository.save(
                createCourseContent("Ejemplos de Diagramas ER", bd2)
        );

        Assignment taskLista = assignmentRepository.save(createAssignment(
                "Implementar lista enlazada", AssignmentType.TASK,
                "Implementar una lista enlazada genérica con sus operaciones básicas",
                LocalDate.of(2026, 3, 15), prog3
        ));
        Assignment projGestion = assignmentRepository.save(createAssignment(
                "Sistema de Gestión Académica", AssignmentType.PROJECT,
                "Desarrollar un sistema web para gestión de notas y matrícula",
                LocalDate.of(2026, 4, 30), prog3
        ));
        Assignment examParcial1 = assignmentRepository.save(createAssignment(
                "Examen Parcial 1", AssignmentType.EXAM,
                "Contenido: listas enlazadas, pilas, colas y árboles binarios",
                LocalDate.of(2026, 3, 1), prog3
        ));
        Assignment taskModelo = assignmentRepository.save(createAssignment(
                "Diseñar modelo relacional", AssignmentType.TASK,
                "Diseñar el modelo relacional para un sistema de biblioteca",
                LocalDate.of(2026, 3, 20), bd2
        ));
        Assignment quizNormalizacion = assignmentRepository.save(createAssignment(
                "Quiz de Normalización", AssignmentType.QUIZ,
                "Evaluación corta sobre formas normales 1FN, 2FN, 3FN y BCNF",
                LocalDate.of(2026, 2, 28), bd2
        ));

        submissionRepository.save(createSubmission(
                taskLista, juan, profCarlos, 90.0, "Buen trabajo, pero falta validación de nulos"
        ));
        submissionRepository.save(createSubmission(
                taskLista, ana, null, null, null
        ));
        submissionRepository.save(createSubmission(
                examParcial1, juan, profCarlos, 85.0, "Buen desempeño"
        ));
        submissionRepository.save(createSubmission(
                examParcial1, ana, profCarlos, 92.0, "Excelente"
        ));
        submissionRepository.save(createSubmission(
                examParcial1, luis, profCarlos, 78.0, "Puede mejorar en árboles binarios"
        ));
        submissionRepository.save(createSubmission(
                examParcial1, sofia, profCarlos, 95.0, "Excelente trabajo"
        ));
        submissionRepository.save(createSubmission(
                taskModelo, juan, profMaria, 88.0, "Bien estructurado"
        ));
        submissionRepository.save(createSubmission(
                quizNormalizacion, ana, null, null, null
        ));

        System.out.println("Seed data inserted successfully!");
        System.out.println("  Users: admin@test.com, carlos.mendoza@test.com, juan.perez@test.com, etc.");
        System.out.println("  All passwords: 123456");
    }

    private Permission createPermission(PermissionType name) {
        Permission p = new Permission();
        p.setName(name);
        return p;
    }

    private University createUniversity(String name, String location) {
        University u = new University();
        u.setName(name);
        u.setLocation(location);
        return u;
    }

    private Faculty createFaculty(String name, University university) {
        Faculty f = new Faculty();
        f.setName(name);
        f.setUniversity(university);
        return f;
    }

    private Department createDepartment(String name, Faculty faculty) {
        Department d = new Department();
        d.setName(name);
        d.setFaculty(faculty);
        return d;
    }

    private Career createCareer(String name, String description, Department department) {
        Career c = new Career();
        c.setName(name);
        c.setDescription(description);
        c.setDepartment(department);
        return c;
    }

    private User createUser(String name, String lastName, String secondLastName,
                            String email, String password, String identificationNumber,
                            boolean isActive, Set<Permission> permissions, Career career) {
        User u = new User();
        u.setName(name);
        u.setLastName(lastName);
        u.setSecondLastName(secondLastName);
        u.setEmail(email);
        u.setPassword(passwordEncryptionService.encodePassword(password));
        u.setIdentificationNumber(identificationNumber);
        u.setActive(isActive);
        u.setPermissions(permissions);
        u.setCareer(career);
        return u;
    }

    private Course createCourse(String name, String description, User professor,
                                Career career, LocalDate startDate, LocalDate endDate,
                                Set<User> students) {
        Course c = new Course();
        c.setName(name);
        c.setDescription(description);
        c.setProfessor(professor);
        c.setCareer(career);
        c.setStartDate(startDate);
        c.setEndDate(endDate);
        c.setUsers(students);
        return c;
    }

    private Assignment createAssignment(String title, AssignmentType type, String description,
                                        LocalDate dueDate, Course course) {
        Assignment a = new Assignment();
        a.setTitle(title);
        a.setType(type);
        a.setDescription(description);
        a.setDueDate(dueDate);
        a.setCourse(course);
        a.setCreatedAt(LocalDate.now());
        a.setUpdatedAt(LocalDate.now());
        return a;
    }

    private CourseContent createCourseContent(String address, Course course) {
        CourseContent cc = new CourseContent();
        cc.setAddress(address);
        cc.setCourse(course);
        cc.setCreatedAt(LocalDate.now());
        cc.setUpdatedAt(LocalDate.now());
        return cc;
    }

    private Submission createSubmission(Assignment assignment, User student,
                                        User reviewedBy, Double grade, String feedback) {
        Submission s = new Submission();
        s.setAssignment(assignment);
        s.setStudent(student);
        s.setCreatedAt(LocalDateTime.now());
        s.setReviewedBy(reviewedBy);
        s.setGrade(grade);
        s.setFeedback(feedback);
        if (reviewedBy != null) {
            s.setReviewedAt(LocalDateTime.now());
        }
        return s;
    }
}
