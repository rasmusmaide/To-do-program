package app;

import java.util.List;

public class TodoList {
    private List<Task> tasks;
    private String description;
    private String todoListID;

    public TodoList(List<Task> tasks, String description) {
        this.tasks = tasks;
        this.description = description;
    }

    public String getTodoListID() {
        return todoListID;
    }

    public void setTodoListID(String todoListID) {
        this.todoListID = todoListID;
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

    @Override
    public String toString() {
        String taskstr = "";
        for (int i = 0; i < tasks.size(); i++) {
            int index = i + 1;
            taskstr += '\n' + "- " + index + ". " + tasks.get(i).toString();
            /*taskstr += '\n' + i + ". Task: " + tasks.get(i).getHeadline()
                    + "Description: " + tasks.get(i).getDescription()
                    + "Deadline: " + tasks.get(i).getDeadline();*/
        }
        return description + tasks;
    }
}
