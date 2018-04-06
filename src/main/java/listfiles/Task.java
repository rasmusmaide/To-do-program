package listfiles;

public class Task {
    private String deadline;
    private String description;
    private boolean done;

    public Task(String deadline, String description, boolean done) {
        this.deadline = deadline;
        this.description = description;
        this.done = done;
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

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
