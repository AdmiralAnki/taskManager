# Project Memory - Spring Projects

## Quick Links

### Documentation
- **[Thymeleaf UI Guide](THYMELEAF_UI_GUIDE.md)** - Complete guide to Thymeleaf implementation in Task Manager
- **[Spring Boot Best Practices](/home/ankith/Documents/SpringProjects/SPRING_BOOT_BEST_PRACTICES.md)** - Spring Boot development best practices
- **[Spring Boot Components](/home/ankith/Documents/SpringProjects/springbootComponents.md)** - Understanding Spring Boot components

### Active Projects
- **taskManager** - Task Management System with REST API and Thymeleaf UI
  - Location: `/home/ankith/Documents/SpringProjects/taskManager`
  - Branch: `feature/thymeleaf-ui`
  - Database: PostgreSQL (taskmanager_db)
  - Current Status: ✅ Running on port 8080

### Project Structure
```
taskManager/
├── src/main/java/com/example/taskManager/
│   ├── entity/          # JPA Entities
│   ├── repository/      # Spring Data JPA repositories
│   ├── service/         # Business logic
│   ├── controller/      # REST and Web controllers
│   ├── dto/             # Data Transfer Objects
│   └── exceptions/      # Exception handlers
└── src/main/resources/
    ├── templates/       # Thymeleaf templates
    └── application.yaml # Configuration
```

### Key Commands

**Start Application:**
```bash
cd /home/ankith/Documents/SpringProjects/taskManager
./gradlew bootRun
```

**Build without tests:**
```bash
./gradlew build -x test
```

**Database Operations:**
```bash
# Create database
sudo -u postgres psql -c "CREATE DATABASE taskmanager_db;"

# Create user
sudo -u postgres psql -c "CREATE USER tmuser WITH PASSWORD 'tmuser_password';"

# Grant privileges
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE taskmanager_db TO tmuser;"
```

### Access Points
- **Web UI:** http://localhost:8080/tasks
- **REST API:** http://localhost:8080/api/tasks
- **Logs:** /tmp/taskmanager.log

### Organization Preferences
- Prefers **Thymeleaf** for UI development due to manpower constraints
- Single deployment artifact (JAR file)
- Minimal JavaScript footprint
- Server-side rendering approach

---

*Last updated: April 10, 2026*
