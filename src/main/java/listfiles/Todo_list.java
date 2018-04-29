package listfiles;

import java.util.List;

public class Todo_list {
    private List<Task> tasks;
    private String description;
    private String todo_listID;

    public Todo_list(List<Task> tasks, String description) {
        this.tasks = tasks;
        this.description = description;
    }

    public String getTodo_listID() {
        return todo_listID;
    }

    public void setTodo_listID(String todo_listID) {
        this.todo_listID = todo_listID;
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
            int index = i+1;
            taskstr +=  '\n' + "- " + index + ". " + tasks.get(i).toString();
            /*taskstr += '\n' + i + ". Task: " + tasks.get(i).getHeadline()
                    + "Description: " + tasks.get(i).getDescription()
                    + "Deadline: " + tasks.get(i).getDeadline();*/
        }
        return description + taskstr;
    }
}
