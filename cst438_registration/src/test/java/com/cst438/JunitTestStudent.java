package com.cst438;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Cst4380wRegistrationApplication.class)
@AutoConfigureMockMvc
public class JunitTestStudent {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StudentRepository repo;

    @Test
    public void addStudent() throws Exception {
        Student s = new Student();
        s.setEmail("test@example.com");
        s.setName("Test User");

        when(repo.save(any(Student.class))).thenReturn(s);

        mvc.perform(MockMvcRequestBuilders
            .post("/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Test User\",\"email\":\"test@example.com\"}"))
            .andExpect(status().isOk());

        verify(repo, times(1)).save(any(Student.class));
    }

    @Test
    public void getStudentById() throws Exception {
        Student s = new Student();
        s.setStudent_id(1);
        s.setName("Test User");
        s.setEmail("test@example.com");

        when(repo.findById(1)).thenReturn(Optional.of(s));

        mvc.perform(MockMvcRequestBuilders
            .get("/students/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(repo, times(1)).findById(1);
    }

    @Test
    public void getAllStudents() throws Exception {
        Student s1 = new Student();
        s1.setStudent_id(1);
        s1.setName("Test User 1");
        s1.setEmail("test1@example.com");

        Student s2 = new Student();
        s2.setStudent_id(2);
        s2.setName("Test User 2");
        s2.setEmail("test2@example.com");

        when(repo.findAll()).thenReturn(Arrays.asList(s1, s2));

        mvc.perform(MockMvcRequestBuilders
            .get("/students")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(repo, times(1)).findAll();
    }

    @Test
    public void updateStudent() throws Exception {
        Student s = new Student();
        s.setStudent_id(1);
        s.setName("Updated User");
        s.setEmail("updated@example.com");

        when(repo.existsById(1)).thenReturn(true);
        when(repo.save(any(Student.class))).thenReturn(s);

        mvc.perform(MockMvcRequestBuilders
            .put("/students/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Updated User\",\"email\":\"updated@example.com\"}"))
            .andExpect(status().isOk());

        verify(repo, times(1)).save(any(Student.class));
    }

    @Test
    public void deleteStudent() throws Exception {
        Student s = new Student();
        s.setStudent_id(1);
        s.setName("Test User");
        s.setEmail("test@example.com");

        when(repo.existsById(1)).thenReturn(true);
        when(repo.findById(1)).thenReturn(Optional.of(s));

        mvc.perform(MockMvcRequestBuilders
            .delete("/students/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(repo, times(1)).deleteById(1);
    }
}
