package app;

import java.util.List;

public class UserTodoLists {
    private List<TodoList> userTodoLists;

    public UserTodoLists(List<TodoList> userTodoLists) {
        this.userTodoLists = userTodoLists;
    }

    public UserTodoLists() {
    }

    public List<TodoList> getUserTodoLists() {
        return userTodoLists;
    }

    public void setUserTodoLists(List<TodoList> userTodoLists) {
        this.userTodoLists = userTodoLists;
    }
}
