package com.example.service;

import com.example.model.Category;
import com.example.model.Priority;
import com.example.model.Task;
import com.example.model.User;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import com.example.request.TaskRequest;
import com.example.request.TaskUpdateRequest;
import com.example.response.TaskResponse;
import com.example.taskmanagementservice.TaskManagementServiceApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ContextConfiguration(classes = TaskManagementServiceApplication.class)
public class TaskServiceTest {

    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    private final UserRepository userRepository;
    @Autowired
    public TaskServiceTest(TaskService taskService, TaskRepository taskRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }


    @Test
    public void createTaskTest() {
        Optional<User> user = userRepository.findById(1L);

        LocalDateTime dueDate = LocalDateTime.now().withHour(18).withMinute(0).withSecond(0);
        TaskRequest taskRequest = TaskRequest
                .builder()
                .title("OOPs topic")
                .description("do a OOPS revision 6pm")
                .category(Category.PERSONAL)
                .priority(Priority.MEDIUM)
                .dueDate(dueDate)
                .ownerId(user.get())
                .build();

        TaskResponse taskResponse = taskService.createTask( user.get(),taskRequest);
        Assertions.assertEquals(taskResponse.getTitle(),taskRequest.getTitle());
    }


    @Test
    public void deleteTaskByTaskIdTest() {
        long taskId = 13L;
        List<Task> listOfTasks = taskRepository.findAll();
        long sizeBeforeDelete = listOfTasks.size();
        taskService.deleteTask(taskId);
        Assertions.assertEquals(sizeBeforeDelete - 1, taskRepository.findAll().size());
        Assertions.assertTrue(taskRepository.findAll().stream().filter(e -> e.getId() == taskId).toList().size() == 0);
    }

    @Test
    public void updateTaskByTaskIdTest() {
        long taskId = 53L;
        TaskUpdateRequest request = TaskUpdateRequest.builder()
                .id(taskId)
                .title("update service test")
                .description("I am testing test for update service")
                .category(Category.WORK)
                .priority(Priority.HIGH)
                .userId(1L)
                .build();
        taskService.updateTask(request);

        Task task = taskRepository.findById(taskId).orElse(null);
        Assertions.assertEquals(task.getTitle(), request.getTitle());
    }

    public void updateTaskByTaskIdTestShouldThrowIfNotPresent() {
        long taskId = 53L;
        Task task = taskRepository.findById(taskId).orElse(null);
    }

}
