package com.example.taskManager.service;

import com.example.taskManager.dto.TaskRequest;
import com.example.taskManager.dto.TaskResponse;
import com.example.taskManager.entity.Task;
import com.example.taskManager.entity.TaskPriority;
import com.example.taskManager.entity.TaskStatus;
import com.example.taskManager.exceptions.TaskNotFoundException;
import com.example.taskManager.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest){

        Task task = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .taskStatus(taskRequest.getStatus())
                .taskPriority(taskRequest.getPriority())
                .dueDate(taskRequest.getDueDate())
                .build();

        Task savedTask = taskRepository.saveAndFlush(task);
        return TaskResponse.from(savedTask);
    }

    @Transactional
    public List<TaskResponse> getAllTasks(){
       List<Task> tasks = taskRepository.findAll();
       return TaskResponse.fromList(tasks);
    }

    @Transactional
    public TaskResponse getTaskById(Long id)  {
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> new TaskNotFoundException("Task with ID " + id + " not found"));

        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest taskRequest){
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> new TaskNotFoundException("Task with ID " + id + " not found"));
        task.setId(id);
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setTaskStatus(taskRequest.getStatus());
        task.setTaskPriority(taskRequest.getPriority());
        task.setDueDate(taskRequest.getDueDate());

        Task savedTask = taskRepository.saveAndFlush(task);

        return TaskResponse.from(savedTask);
    }

    @Transactional
    public void deleteTask(Long id){
        if(!taskRepository.existsById(id)){
          throw new TaskNotFoundException("Task with ID " + id + " not found");
        }

        taskRepository.deleteById(id);
    }

    @Transactional
    public List<TaskResponse> getTaskByStatus(TaskStatus status){
        List<Task> tasks = taskRepository.findByTaskStatus(status);
        return TaskResponse.fromList(tasks);
    }

    @Transactional
    public List<TaskResponse> getTaskByPriority(TaskPriority priority){
        List<Task> tasks = taskRepository.findByTaskPriority(priority);
        return TaskResponse.fromList(tasks);
    }

    @Transactional
    public List<TaskResponse> searchTaskByTitle(String title){
        List<Task> tasks = taskRepository.findByTitleContainingIgnoreCase(title);
        return TaskResponse.fromList(tasks);
    }
}
