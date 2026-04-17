# Thymeleaf Quick Reference Card

## Expressions

| Expression | Description | Example |
|------------|-------------|---------|
| `${...}` | Variable expression | `${task.title}` |
| `*{...}` | Selection expression | `*{title}` (with th:object) |
| `#{...}` | Message expression | `#{messages.welcome}` |
| `@{...}` | Link URL expression | `@{/tasks/{id}(id=${task.id})}` |
| `~{...}` | Fragment expression | `~{fragments :: card}` |

## Common Attributes

| Attribute | Description | Example |
|-----------|-------------|---------|
| `th:text` | Set text content | `<span th:text="${title}">` |
| `th:utext` | Set unescaped text (use carefully!) | `<div th:utext="${html}">` |
| `th:if` | Conditional rendering | `<div th:if="${task.status == 'PENDING'}">` |
| `th:unless` | Negative condition | `<div th:unless="${task.completed}">` |
| `th:each` | Loop | `<li th:each="task : ${tasks}">` |
| `th:href` | Set href attribute | `<a th:href="@{/tasks/{id}(id=${task.id})}">` |
| `th:action` | Set form action | `<form th:action="@{/tasks}">` |
| `th:method` | Set form method | `<form th:method="post">` |
| `th:field` | Form field binding | `<input th:field="*{title}">` |
| `th:value` | Set value attribute | `<input th:value="${task.title}">` |
| `th:class` | Set class (with append) | `<div th:class="'badge-' + ${status}">` |
| `th:object` | Selection object | `<form th:object="${task}">` |
| `th:replace` | Replace entire fragment | `<div th:replace="fragments :: card">` |
| `th:insert` | Insert fragment content | `<div th:insert="fragments :: card">` |

## Conditional Rendering

```thymeleaf
<!-- Simple if -->
<div th:if="${user.isAdmin}">Admin content</div>

<!-- If/else equivalent -->
<div th:if="${user.isAdmin}">Admin</div>
<div th:unless="${user.isAdmin}">User</div>

<!-- Switch/case -->
<div th:switch="${user.role}">
  <p th:case="'admin'">Administrator</p>
  <p th:case="'user'">User</p>
  <p th:case="*">Unknown</p>
</div>

<!-- Null check -->
<div th:if="${task.description != null and !task.description.isEmpty()}">
  <span th:text="${task.description}"></span>
</div>

<!-- Empty check -->
<div th:if="${tasks == null or tasks.empty}">
  No tasks found
</div>
```

## Loops

```thymeleaf
<!-- Basic loop -->
<div th:each="task : ${tasks}">
  <span th:text="${task.title}">Task Title</span>
</div>

<!-- Loop with index -->
<li th:each="task, iterStat : ${tasks}">
  <span th:text="${iterStat.index} + ': ' + ${task.title}"></span>
</li>

<!-- Loop with condition -->
<div th:each="task : ${tasks}" th:if="${task.priority == 'HIGH'}">
  High priority task
</div>
```

## Form Binding

```thymeleaf
<!-- Form with object -->
<form th:action="@{/tasks}" th:object="${task}" th:method="post">
  <!-- Text input -->
  <input type="text" th:field="*{title}" />

  <!-- Textarea -->
  <textarea th:field="*{description}"></textarea>

  <!-- Select dropdown -->
  <select th:field="*{status}">
    <option th:each="status : ${allStatuses}"
            th:value="${status}"
            th:text="${status}">PENDING</option>
  </select>

  <!-- Radio buttons -->
  <input type="radio" th:field="*{priority}" value="HIGH" />
  <input type="radio" th:field="*{priority}" value="LOW" />

  <!-- Checkbox -->
  <input type="checkbox" th:field="*{completed}" />

  <!-- Submit button -->
  <button type="submit">Save</button>
</form>
```

## URLs

```thymeleaf
<!-- Simple URL -->
<a th:href="@{/tasks}">All Tasks</a>

<!-- With path variable -->
<a th:href="@{/tasks/{id}(id=${task.id})}">View Task</a>

<!-- Multiple path variables -->
<a th:href="@{/tasks/{id}/comments/{commentId}(id=${task.id}, commentId=${comment.id})}">
  View Comment
</a>

<!-- With query parameters -->
<a th:href="@{/tasks(status='PENDING', priority='HIGH')}">
  Filter Tasks
</a>

<!-- Dynamic query parameters -->
<a th:href="@{/tasks/search(title=${searchTerm})}">
  Search
</a>

<!-- With fragments -->
<a th:href="@{/tasks(page=${currentPage}, size=${pageSize})}">
  Next Page
</a>
```

## Dynamic Classes/Attributes

```thymeleaf
<!-- Dynamic class based on condition -->
<div th:class="${task.priority == 'URGENT' ? 'urgent-card' : 'normal-card'}">
  Content
</div>

<!-- Appending to existing class -->
<div class="card" th:classappend="${task.completed} ? 'completed' : ''">
  Task
</div>

<!-- Multiple classes with concatenation -->
<span class="badge"
      th:class="'badge-status-' + ${task.status} + ' ' + 'badge-priority-' + ${task.priority}">
  Badge
</span>

<!-- Conditional style -->
<div th:style="'background-color:' + (${task.urgent} ? 'red' : 'white')">
  Content
</div>
```

## Built-in Utilities

```thymeleaf
<!-- Dates -->
<span th:text="${#dates.format(task.dueDate, 'yyyy-MM-dd')}">2024-04-10</span>
<span th:text="${#dates.day(task.dueDate)}">10</span>

<!-- Strings -->
<span th:text="${#strings.toUpperCase(task.title)}">TITLE</span>
<span th:text="${#strings.length(task.description)}">100</span>

<!-- Numbers -->
<span th:text="${#numbers.formatDecimal(task.price, 1, 2)}">10.99</span>

<!-- Lists/Lists -->
<span th:text="${#lists.size(tasks)}">5</span>
<span th:text="${#lists.isEmpty(tasks)}">false</span>

<!-- Conditionals (ternary) -->
<span th:text="${task.completed ? 'Done' : 'Pending'}">Pending</span>

<!-- Default values -->
<span th:text="${task.title ?: 'No title'}">No title</span>
```

## Validation Error Display

```thymeleaf
<!-- Check if field has errors -->
<input th:field="*{title}" />
<div class="error" th:if="${#fields.hasErrors('title')}"
     th:errors="*{title}">Title error</div>

<!-- All errors for form -->
<div th:if="${#fields.hasGlobalErrors()}">
  <span th:each="err : ${#fields.globalErrors()}"
        th:text="${err}">Global error</span>
</div>

<!-- All errors for specific field -->
<div th:each="err : ${#fields.errors('title')}"
     th:text="${err}">Title error</div>

<!-- Check if field has specific error code -->
<div th:if="${#fields.hasErrors('title')}">
  <span class="error" th:text="${#fields.errors('title')}">Error</span>
</div>
```

## Fragments (Reusable Components)

```thymeleaf
<!-- Define fragment (fragments.html) -->
<div th:fragment="task-card(task)">
  <div class="card">
    <span th:text="${task.title}">Title</span>
    <span th:text="${task.status}">Status</span>
  </div>
</div>

<!-- Use fragment (replace entire div) -->
<div th:replace="fragments :: task-card(${task})"></div>

<!-- Use fragment (insert content only) -->
<div th:insert="fragments :: task-card(${task})"></div>

<!-- Parameterized fragment -->
<div th:fragment="header(title)">
  <h1 th:text="${title}">Header</h1>
</div>

<!-- With multiple parameters -->
<div th:fragment="footer(year, company)">
  <footer>
    <span th:text="${year}">2024</span> - <span th:text="${company}">Company</span>
  </footer>
</div>

<!-- Usage -->
<div th:replace="fragments :: footer(2024, 'My Company')"></div>
```

## Page Layout Inheritance

```thymeleaf
<!-- layout.html -->
<!DOCTYPE html>
<html th:fragment="layout(title, content)">
<head>
    <title th:replace="${title}">Page Title</title>
</head>
<body>
    <header>Common Header</header>
    <div th:replace="${content}">Page Content</div>
    <footer>Common Footer</footer>
</body>
</html>

<!-- tasks.html (using layout) -->
<html th:replace="~{layout :: layout(~{::title}, ~{::content})}">
<head>
    <title>Task List</title>
</head>
<body>
    <div th:fragment="content">
        <h1>Tasks</h1>
        <!-- Page content -->
    </div>
</body>
</html>
```

## Common Patterns

### Display list with empty state

```thymeleaf
<div th:if="${tasks == null or tasks.empty}" class="empty-state">
    <h2>No tasks found</h2>
    <p>Create your first task to get started!</p>
</div>

<div th:each="task : ${tasks}" class="task-card">
    <span th:text="${task.title}">Task Title</span>
</div>
```

### Active link highlighting

```thymeleaf
<a href="/tasks"
   th:class="${filter == null ? 'active' : ''}">All Tasks</a>
<a href="/tasks?status=PENDING"
   th:class="${filter == 'PENDING' ? 'active' : ''}">Pending</a>
```

### Conditional content display

```thymeleaf
<!-- Show optional description -->
<div th:if="${task.description != null and !task.description.isEmpty()}"
     class="description"
     th:text="${task.description}">
    Description text
</div>
```

### CRUD operation links

```thymeleaf
<!-- View -->
<a th:href="@{/tasks/{id}(id=${task.id})}">View</a>

<!-- Edit -->
<a th:href="@{/tasks/edit/{id}(id=${task.id})}">Edit</a>

<!-- Delete (form submission for non-GET) -->
<form th:action="@{/tasks/delete/{id}(id=${task.id})}" method="post"
      onsubmit="return confirm('Are you sure?');">
    <button type="submit">Delete</button>
</form>
```

---

## Controller Cheat Sheet

```java
@Controller
@RequestMapping("/tasks")
public class WebController {

    // Display list
    @GetMapping
    public String list(Model model) {
        model.addAttribute("tasks", service.getAll());
        return "tasks";  // returns template name
    }

    // Display form
    @GetMapping("/create")
    public String showForm(Model model) {
        model.addAttribute("task", new TaskRequest());
        return "create-task";
    }

    // Process form
    @PostMapping
    public String create(@Valid @ModelAttribute TaskRequest task,
                        BindingResult result,
                        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "create-task";  // return to form
        }
        service.create(task);
        redirect.addFlashAttribute("success", "Created!");
        return "redirect:/tasks";  // PRG pattern
    }

    // Edit with path variable
    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable Long id, Model model) {
        model.addAttribute("task", service.getById(id));
        return "edit-task";
    }
}
```

---

**Quick Tips:**
- Use `th:text` instead of `th:utext` to prevent XSS
- Always use `th:field` for form binding
- Implement PRG pattern (redirect after POST)
- Use fragments for reusable components
- Leverage `th:if="${#fields.hasErrors('...')}"` for validation display
