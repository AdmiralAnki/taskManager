package com.example.taskManager.controller;

import com.example.taskManager.dto.TaskRequest;
import com.example.taskManager.dto.TaskResponse;
import com.example.taskManager.entity.Task;
import com.example.taskManager.entity.TaskPriority;
import com.example.taskManager.entity.TaskStatus;
import com.example.taskManager.exceptions.TaskNotFoundException;
import com.example.taskManager.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class WebController {

    private final TaskService taskService;

    @GetMapping
    public String listTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String filter,
            Model model) {

        try {
            List<TaskResponse> tasks;

            if (status != null) {
                tasks = taskService.getTaskByStatus(status);
                model.addAttribute("filter", status.name());
            } else if (priority != null) {
                tasks = taskService.getTaskByPriority(priority);
                model.addAttribute("filter", priority.name());
            } else {
                tasks = taskService.getAllTasks();
            }

            model.addAttribute("tasks", tasks);
        } catch (Exception e) {
            log.error("Error loading tasks", e);
            model.addAttribute("error", "Error loading tasks: " + e.getMessage());
        }

        return "tasks";
    }

    @GetMapping("/search")
    public String searchTasks(@RequestParam String title, Model model) {
        try {
            List<TaskResponse> tasks = taskService.searchTaskByTitle(title);
            model.addAttribute("tasks", tasks);
            model.addAttribute("searchTitle", title);
        } catch (Exception e) {
            log.error("Error searching tasks", e);
            model.addAttribute("error", "Error searching tasks: " + e.getMessage());
        }
        return "tasks";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new TaskRequest());
        return "create-task";
    }

    @PostMapping
    public String createTask(@ModelAttribute("task") TaskRequest taskRequest,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "create-task";
        }

        try {
            taskService.createTask(taskRequest);
            return "redirect:/tasks";
        } catch (Exception e) {
            log.error("Error creating task", e);
            redirectAttributes.addFlashAttribute("error", "Error creating task: " + e.getMessage());
            return "redirect:/tasks/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TaskResponse task = taskService.getTaskById(id);
            model.addAttribute("task", task);
            return "edit-task";
        } catch (TaskNotFoundException e) {
            log.error("Task not found: {}", id);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tasks";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute("task") TaskRequest taskRequest,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "edit-task";
        }

        try {
            taskService.updateTask(id, taskRequest);
            return "redirect:/tasks";
        } catch (TaskNotFoundException e) {
            log.error("Task not found: {}", id);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tasks";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            taskService.deleteTask(id);
        } catch (TaskNotFoundException e) {
            log.error("Task not found: {}", id);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tasks";
    }
}
