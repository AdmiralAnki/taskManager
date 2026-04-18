# Thymeleaf UI Implementation Guide

## 📚 Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Thymeleaf Basics](#thymeleaf-basics)
4. [How It Works - Step by Step](#how-it-works---step-by-step)
5. [Web Controller Deep Dive](#web-controller-deep-dive)
6. [Template System](#template-system)
7. [Data Flow](#data-flow)
8. [Key Concepts](#key-concepts)
9. [REST vs Web UI](#rest-vs-web-ui)
10. [Best Practices](#best-practices)

---

## Overview

This guide explains how the Thymeleaf UI works in the Task Manager application. Thymeleaf is a server-side Java template engine that allows you to create dynamic web pages using natural templates - HTML files that can be opened in a browser and still look correct.

### What We Built

```
Browser → HTTP Request → WebController → Service → Repository → Database
                                              ↓
                                        Thymeleaf Template
                                              ↓
                                        Rendered HTML
                                              ↓
                                        Browser Response
```

---

## Architecture

### Layer Structure

```
┌─────────────────────────────────────────────────────────────┐
│                      Browser                                │
│              (http://localhost:8080/tasks)                  │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP GET Request
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                   WebController                              │
│  - Receives HTTP requests                                   │
│  - Calls Service layer                                      │
│  - Adds data to Model                                       │
│  - Returns template name                                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                    TaskService                              │
│  - Business logic                                           │
│  - Calls Repository                                         │
│  - Returns DTOs                                             │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  TaskRepository                             │
│  - JPA/Hibernate operations                                 │
│  - Database queries                                         │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  PostgreSQL Database                         │
│              (stores tasks data)                            │
└─────────────────────────────────────────────────────────────┘
                       │
                       │ Data flows back up through layers
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                Thymeleaf Template Engine                     │
│  - Receives: template name + Model data                     │
│  - Processes template with data                             │
│  - Generates HTML                                           │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTML Response
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                      Browser                                │
│              (renders HTML page)                            │
└─────────────────────────────────────────────────────────────┘
```

---

## Thymeleaf Basics

### What is Thymeleaf?

**Thymeleaf** is a server-side Java template engine for web applications. It processes HTML templates and fills them with dynamic data before sending to the browser.

### Key Features

1. **Natural Templates** - Templates are valid HTML that can be opened in browsers
2. **Server-Side Rendering** - HTML is generated on the server
3. **Spring Integration** - Works seamlessly with Spring Boot
4. **No JavaScript Required** - All logic happens server-side

### Basic Thymeleaf Syntax

#### Standard Expression Syntax

```thymeleaf
<!-- Variable expression: ${...} -->
<span th:text="${user.name}">Default Name</span>

<!-- Selection expression: *{...} -->
<div th:object="${user}">
  <span th:text="*{name}">Name</span>
</div>

<!-- Message expression: #{...} -->
<span th:text="#{messages.welcome}">Welcome</span>

<!-- Link URL expression: @{...} -->
<a th:href="@{/tasks/{id}(id=${task.id})}">View Task</a>

<!-- Conditional rendering -->
<div th:if="${user.isAdmin}">
  Admin content
</div>

<!-- Loop -->
<li th:each="task : ${tasks}">
  <span th:text="${task.title}">Task Title</span>
</li>
```

---

## How It Works - Step by Step

### Example: User Visits `/tasks`

#### Step 1: Browser Makes Request
```
GET http://localhost:8080/tasks
```

#### Step 2: Spring Routes to WebController

```java
@GetMapping
public String listTasks(
    @RequestParam(required = false) TaskStatus status,
    Model model) {  // ← Model will carry data to template

    // Get data from service
    List<TaskResponse> tasks = taskService.getAllTasks();

    // Add data to Model
    model.addAttribute("tasks", tasks);

    // Return template name (Thymeleaf will find tasks.html)
    return "tasks";
}
```

#### Step 3: Controller Gets Data

```java
// In WebController
List<TaskResponse> tasks = taskService.getAllTasks();
// tasks = [
//   TaskResponse(id=1, title="Task 1", status=PENDING),
//   TaskResponse(id=2, title="Task 2", status=IN_PROGRESS)
// ]
```

#### Step 4: Controller Adds Data to Model

```java
model.addAttribute("tasks", tasks);
// Model now contains: {tasks: [...]}
```

#### Step 5: Thymeleaf Processes Template

**Template file:** `src/main/resources/templates/tasks.html`

```html
<!-- Before processing -->
<div th:each="task : ${tasks}">
  <span th:text="${task.title}">Default Title</span>
  <span th:text="${task.status}">PENDING</span>
</div>
```

**After processing (sent to browser):**

```html
<div>
  <span>Task 1</span>
  <span>PENDING</span>
</div>
<div>
  <span>Task 2</span>
  <span>IN_PROGRESS</span>
</div>
```

#### Step 6: Browser Receives HTML

Browser receives complete HTML page and renders it. No JavaScript needed!

---

## Web Controller Deep Dive

### What is a Web Controller?

A **Web Controller** (`@Controller`) handles HTTP requests and returns **view names** (HTML templates), unlike REST Controllers (`@RestController`) which return **data** (JSON).

### Controller vs REST Controller

| Aspect | @Controller | @RestController |
|--------|-------------|-----------------|
| Returns | View names (HTML) | Data (JSON) |
| Response Type | HTML pages | JSON/XML |
| Used for | Web UI | APIs |
| Example | `return "tasks"` | `return ResponseEntity.ok(tasks)` |

### Our WebController Breakdown

```java
@Controller                  // ← Returns view names (not JSON)
@RequestMapping("/tasks")   // ← Base URL for all endpoints
@RequiredArgsConstructor
public class WebController {

    private final TaskService taskService;

    // GET /tasks - Show all tasks
    @GetMapping
    public String listTasks(Model model) {
        // 1. Get data from service
        List<TaskResponse> tasks = taskService.getAllTasks();

        // 2. Add data to Model (like a hashmap)
        model.addAttribute("tasks", tasks);

        // 3. Return template name
        // Thymeleaf will look for: templates/tasks.html
        return "tasks";
    }
}
```

### Handling Parameters

```java
// Query parameters: /tasks?status=PENDING
@GetMapping
public String listTasks(
    @RequestParam(required = false) TaskStatus status,
    Model model) {

    List<TaskResponse> tasks;

    if (status != null) {
        tasks = taskService.getTaskByStatus(status);
        model.addAttribute("filter", status.name()); // For UI highlighting
    } else {
        tasks = taskService.getAllTasks();
    }

    model.addAttribute("tasks", tasks);
    return "tasks";
}
```

### Handling Form Submissions

```java
// Show form
@GetMapping("/create")
public String showCreateForm(Model model) {
    model.addAttribute("task", new TaskRequest());
    return "create-task";
}

// Process form submission
@PostMapping
public String createTask(
    @ModelAttribute("task") TaskRequest taskRequest,  // ← Form data
    BindingResult bindingResult,                       // ← Validation errors
    RedirectAttributes redirectAttributes) {           // ← For flash messages

    if (bindingResult.hasErrors()) {
        return "create-task";  // Stay on form if validation fails
    }

    try {
        taskService.createTask(taskRequest);
        return "redirect:/tasks";  // ← Redirect after successful create
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/tasks/create";
    }
}
```

### Key Controller Concepts

#### Model
```java
public String listTasks(Model model) {
    // Model is a map that carries data to the view
    model.addAttribute("tasks", tasks);
    model.addAttribute("filter", "PENDING");
    model.addAttribute("searchTitle", "work");

    return "tasks";  // Template can access all Model attributes
}
```

#### @ModelAttribute
```java
public String createTask(@ModelAttribute("task") TaskRequest task) {
    // Binds form parameters to object
    // <input name="title"> → task.setTitle("...")
    // <input name="status"> → task.setStatus("...")
}
```

#### Redirect After Post (PRG Pattern)
```java
return "redirect:/tasks";  // ← Prevents duplicate submissions on refresh
```

---

## Template System

### Template Location

```
src/main/resources/templates/
├── tasks.html          ← Main task list
├── create-task.html    ← Create task form
└── edit-task.html      ← Edit task form
```

### Template Structure

#### tasks.html - Task List Template

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">  ← Required namespace
<head>
    <meta charset="UTF-8">
    <title>Task Manager</title>
    <style>/* CSS styles */</style>
</head>
<body>
    <div class="container">
        <h1>📋 Task Manager</h1>

        <!-- Filter buttons -->
        <div class="filters">
            <a href="/tasks" th:class="${filter == null ? 'active' : ''}">
                All Tasks
            </a>
            <a href="/tasks?status=PENDING"
               th:class="${filter == 'PENDING' ? 'active' : ''}">
                Pending
            </a>
        </div>

        <!-- Task list loop -->
        <div class="task-list">
            <div th:each="task : ${tasks}" class="task-card">
                <!-- Task title -->
                <div class="task-title" th:text="${task.title}">
                    Default Title  ← Shown only when viewing template in browser
                </div>

                <!-- Status badge with dynamic class -->
                <span class="badge"
                      th:class="'badge-status-' + ${task.status}"
                      th:text="${task.status}">
                    PENDING  ← Default value
                </span>

                <!-- Conditional description -->
                <div th:if="${task.description != null and !task.description.isEmpty()}"
                     th:text="${task.description}">
                    Default description
                </div>

                <!-- Action links -->
                <a th:href="@{/tasks/edit/{id}(id=${task.id})}">Edit</a>
            </div>
        </div>

        <!-- Empty state when no tasks -->
        <div th:if="${tasks == null or tasks.empty}" class="empty-state">
            <h2>No tasks found</h2>
        </div>
    </div>
</body>
</html>
```

### Template Processing Examples

#### Example 1: Loop with Data

**Model data:**
```java
tasks = [
    TaskResponse(id=1, title="Task 1", status="PENDING"),
    TaskResponse(id=2, title="Task 2", status="COMPLETED")
]
```

**Template:**
```html
<div th:each="task : ${tasks}">
    <span th:text="${task.title}">Default Title</span>
    <span th:text="${task.status}">PENDING</span>
</div>
```

**Rendered HTML:**
```html
<div>
    <span>Task 1</span>
    <span>PENDING</span>
</div>
<div>
    <span>Task 2</span>
    <span>COMPLETED</span>
</div>
```

#### Example 2: Conditional Rendering

```html
<!-- Show priority only if set -->
<span th:if="${task.priority != null}"
      th:text="${task.priority}">
    HIGH
</span>

<!-- Show different content based on status -->
<div th:if="${task.status == 'PENDING'}">
    This task is pending
</div>
<div th:if="${task.status == 'COMPLETED'}">
    This task is completed
</div>
```

#### Example 3: Dynamic Classes

```html
<!-- Add class based on condition -->
<div th:class="${task.priority == 'URGENT' ? 'urgent-card' : 'normal-card'}">
    Task content
</div>

<!-- Multiple classes with condition -->
<span class="badge"
      th:class="'badge-status-' + ${task.status}">
    PENDING
</span>

<!-- Renders to: -->
<span class="badge-status-PENDING">PENDING</span>
```

#### Example 4: URL Building

```html
<!-- Simple link -->
<a th:href="@{/tasks}">All Tasks</a>

<!-- With path variable -->
<a th:href="@{/tasks/edit/{id}(id=${task.id})}">Edit Task</a>

<!-- With query parameter -->
<a th:href="@{/tasks(status='PENDING')}">Pending Tasks</a>

<!-- Multiple parameters -->
<a th:href="@{/tasks/search(title=${searchTerm}, status='PENDING')}">
    Search
</a>
```

---

## Data Flow

### Complete Request-Response Cycle

#### Scenario: User filters tasks by status

```
1. USER ACTION
   └─ User clicks "Pending" filter button
      └─ Browser sends: GET /tasks?status=PENDING

2. SPRING MVC
   └─ DispatcherServlet receives request
   └─ Routes to WebController.listTasks()

3. CONTROLLER
   └─ @RequestParam binds "PENDING" to status parameter
   └─ Calls: taskService.getTaskByStatus(PENDING)
   └─ Receives: List<TaskResponse>
   └─ Adds to Model: model.addAttribute("tasks", tasksList)
   └─ Adds to Model: model.addAttribute("filter", "PENDING")
   └─ Returns: "tasks" (template name)

4. THYMELEAF ENGINE
   └─ Receives: template name "tasks" + Model data
   └─ Loads: src/main/resources/templates/tasks.html
   └─ Processes template with Model data:
      ├─ th:each loops through tasks
      ├─ th:text replaces with actual data
      ├─ th:if evaluates conditions
      └─ th:href builds URLs
   └─ Generates: Complete HTML with data

5. RESPONSE
   └─ Content-Type: text/html
   └─ Browser receives rendered HTML
   └─ Browser displays page to user
```

### Data Transformation Through Layers

```
DATABASE ROW
├─ id: 1
├─ title: "Task 1"
├─ status: "PENDING"
└─ priority: "HIGH"
     ↓
JPA/Hibernate
└─ Maps to Task entity
     ↓
TaskRepository.findAll()
└─ Returns List<Task>
     ↓
TaskService.getAllTasks()
├─ Converts to TaskResponse DTOs
└─ Returns List<TaskResponse>
     ↓
WebController
├─ Adds to Model
└─ Returns "tasks"
     ↓
Thymeleaf Template
├─ Access via ${tasks}
└─ Renders HTML
     ↓
BROWSER
└─ Displays HTML to user
```

---

## Key Concepts

### 1. Model-View-Controller (MVC)

**Model** (Data)
```java
model.addAttribute("tasks", tasks);
```

**View** (Template)
```html
<div th:each="task : ${tasks}">
    <span th:text="${task.title}">Title</span>
</div>
```

**Controller** (Coordinator)
```java
@GetMapping
public String listTasks(Model model) {
    model.addAttribute("tasks", service.getAllTasks());
    return "tasks";  // View name
}
```

### 2. Request Mapping

```java
@Controller
@RequestMapping("/tasks")  // ← Base path
public class WebController {

    @GetMapping           // ← GET /tasks
    public String list() { }

    @GetMapping("/create")  // ← GET /tasks/create
    public String showCreate() { }

    @PostMapping          // ← POST /tasks
    public String create() { }

    @GetMapping("/edit/{id}")  // ← GET /tasks/edit/1
    public String showEdit(@PathVariable Long id) { }
}
```

### 3. Data Binding

**Form HTML:**
```html
<form th:action="@{/tasks}" method="post">
    <input name="title" type="text">
    <select name="status">
        <option value="PENDING">Pending</option>
    </select>
    <button type="submit">Create</button>
</form>
```

**Controller Binding:**
```java
@PostMapping
public String createTask(@ModelAttribute TaskRequest request) {
    // Spring automatically binds form parameters to object:
    // request.getTitle()      → value from <input name="title">
    // request.getStatus()     → value from <select name="status">
}
```

### 4. Validation

**DTO with Validation:**
```java
public class TaskRequest {
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, max = 100)
    private String title;

    @NotNull
    private TaskStatus status;
}
```

**Controller:**
```java
@PostMapping
public String createTask(
    @Valid @ModelAttribute TaskRequest request,  // ← @Valid triggers validation
    BindingResult result) {                       // ← Contains errors

    if (result.hasErrors()) {
        return "create-task";  // Return to form
    }

    // Process valid data
    return "redirect:/tasks";
}
```

**Template Display Errors:**
```html
<input th:field="*{title}">
<div class="error" th:if="${#fields.hasErrors('title')}"
     th:errors="*{title}">Title error</div>
```

### 5. Flash Attributes (Temporary Messages)

```java
// Add flash message (survives redirect)
redirectAttributes.addFlashAttribute("success", "Task created!");
return "redirect:/tasks";

// Access in template
<div th:if="${success}" th:text="${success}"></div>
```

---

## REST vs Web UI

### Same Backend, Two Frontends

Our application has **TWO interfaces** to the same service layer:

```
                    TaskService
                    ↓          ↓
            REST API          Web UI
         (JSON data)       (HTML pages)
```

### Comparison Table

| Aspect | REST API | Web UI (Thymeleaf) |
|--------|----------|-------------------|
| **Controller Type** | @RestController | @Controller |
| **Returns** | JSON data | HTML view |
| **Content-Type** | application/json | text/html |
| **Consumed By** | JavaScript/mobile apps | Browsers |
| **Example Response** | `{"id":1,"title":"..."}` | `<html>...</html>` |
| **State** | Stateless | Can use sessions |
| **URL Example** | `/api/tasks/1` | `/tasks/edit/1` |

### Code Comparison

**REST Controller (TaskController):**
```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll() {
        return ResponseEntity.ok(taskService.getAllTasks());
        // Returns JSON: [{"id":1,"title":"..."}]
    }
}
```

**Web Controller (WebController):**
```java
@Controller
@RequestMapping("/tasks")
public class WebController {
    @GetMapping
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks";
        // Returns HTML page
    }
}
```

### When to Use Which?

**Use REST API when:**
- Building mobile apps
- Building SPA with React/Vue/Angular
- Need to serve multiple frontend clients
- Want stateless, cacheable API

**Use Thymeleaf when:**
- Building traditional web apps
- Need SEO-friendly pages
- Want simple deployment (single JAR)
- Small team, want to minimize JavaScript

---

## Best Practices

### 1. Separation of Concerns

```java
// ✅ GOOD - Controller delegates to service
@GetMapping
public String listTasks(Model model) {
    model.addAttribute("tasks", taskService.getAllTasks());
    return "tasks";
}

// ❌ BAD - Controller does business logic
@GetMapping
public String listTasks(Model model) {
    List<Task> tasks = taskRepository.findAll();  // Don't do this
    model.addAttribute("tasks", tasks);
    return "tasks";
}
```

### 2. DTO Pattern

```java
// ✅ GOOD - Use DTOs in controller
model.addAttribute("tasks", taskService.getAllTasks());
// Returns List<TaskResponse>

// ❌ BAD - Expose entities directly
model.addAttribute("tasks", taskRepository.findAll());
// Returns List<Task> - exposes internal structure
```

### 3. PRG Pattern (Post-Redirect-Get)

```java
// ✅ GOOD - Redirect after POST
@PostMapping
public String createTask(...) {
    taskService.create(task);
    return "redirect:/tasks";  // ← Prevents duplicate submissions
}

// ❌ BAD - Return view after POST
@PostMapping
public String createTask(...) {
    taskService.create(task);
    return "tasks";  // ← Refresh causes duplicate submission
}
```

### 4. Template Reusability

```html
<!-- ✅ GOOD - Use fragments for common elements -->
<!-- fragments.html -->
<div th:fragment="task-card(task)">
    <div class="card">
        <span th:text="${task.title}">Title</span>
    </div>
</div>

<!-- Use in other templates -->
<div th:replace="fragments :: task-card(${task})"></div>
```

### 5. Security Considerations

```html
<!-- ✅ GOOD - Thymeleaf auto-escapes HTML -->
<div th:text="${userInput}"></div>
<!-- Renders: <script> as &lt;script&gt; -->

<!-- ❌ BAD - Unescaped output (XSS risk) -->
<div th:utext="${userInput}"></div>
<!-- Renders: <script> as <script> - DANGEROUS! -->
```

### 6. Form Handling

```html
<!-- ✅ GOOD - Use th:field for form binding -->
<input th:field="*{title}" />
<select th:field="*{status}">
    <option th:each="s : ${allStatuses}"
            th:value="${s}"
            th:text="${s}">PENDING</option>
</select>

<!-- ❌ BAD - Manual binding -->
<input name="title" th:value="${task.title}" />
<select name="status">
    <!-- No automatic selection of current value -->
</select>
```

---

## Summary

### Key Takeaways

1. **Thymeleaf = Server-Side Rendering**
   - Templates processed on server
   - Complete HTML sent to browser
   - No JavaScript required

2. **Web Controller = View Renderer**
   - Returns template names, not data
   - Uses Model to pass data to templates
   - Handles form submissions

3. **Data Flow**
   ```
   Browser → Controller → Service → Repository → Database
                           ↓
                     Thymeleaf Template
                           ↓
                     Rendered HTML
                           ↓
                        Browser
   ```

4. **Key Concepts**
   - Model: Carries data to view
   - View: Thymeleaf template
   - Controller: Coordinates everything
   - @ModelAttribute: Binds form data
   - PRG Pattern: Redirect after POST

5. **Best Practices**
   - Keep controllers thin
   - Use DTOs, not entities
   - Redirect after form submission
   - Leverage Thymeleaf's auto-escaping
   - Use th:field for forms

---

## Additional Resources

- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Spring MVC Guide](https://spring.io/guides/gs/serving-web-content/)
- [Spring Boot Thymeleaf Starter](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.template-engines)

---

**Created:** April 10, 2026
**Project:** Task Manager - Thymeleaf UI Implementation
