package listfiles;

import java.util.ArrayList;
import java.util.List;

public class Todo_list {
    private List<Task> tasks;
    private String description;

    public Todo_list(List<Task> tasks, String description) {
        this.tasks = tasks;
        this.description = description;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
