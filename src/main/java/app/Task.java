package app;

public class Task {
    private String creationDate;
    private String deadline;
    private String headline;
    private String description;
    private boolean done;
    private int taskID;
    private int todoListID;

    public Task(String creationDate, String deadline, String headline, String description, boolean done) {
        this.creationDate = creationDate;
        this.deadline = deadline;
        this.headline = headline;
        this.description = description;
        this.done = done;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getTodoListID() {
        return todoListID;
    }

    public void setTodoListID(int todoListID) {
        this.todoListID = todoListID;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Task{" +
                "created=" + creationDate + '\'' +
                ", deadline='" + deadline + '\'' +
                ", headline='" + headline + '\'' +
                ", description='" + description + '\'' +
                ", done=" + done + ", todoid=" + todoListID + ", taskid=" + taskID +
        '}';
    }
}
