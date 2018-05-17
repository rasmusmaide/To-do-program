package listfiles.Connection;

import com.google.gson.Gson;
import listfiles.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Echo implements Runnable {
    private Socket socket;
    private DataBaseCommands dbc;

    public Echo(Socket socket, DataBaseCommands dbc) {
        this.socket = socket;
        this.dbc = dbc;
    }

    //public List<String> infoIn;

    @Override
    public void run() {

        try {

            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                System.out.println("client connected; waiting for a byte");

                //////////////////// command in
                int noOfParametres = in.readInt();
                List<String> infoIn = new ArrayList<>();

                for (int i = 0; i < noOfParametres; i++) {
                    String getInfoIn = in.readUTF();
                    infoIn.add(getInfoIn);
                }

                //////////////////// decisions and calculations

                try {
                    String command = infoIn.get(0);
                    System.out.println(command + " " + infoIn + " @echocommand");
                    int userID = 0;
                    String userIDstring;
                    int index;
                    String indexString;
                    String username;
                    String password;

                    switch (command) {
                        case "get lists": // {"get lists", userID}
                            userIDstring = infoIn.get(1);

                            try {
                                userID = Integer.parseInt(userIDstring);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }

                            List<TodoList> userLists = dbc.getAllUserLists(userID);

                            for (TodoList todo_list : userLists) {
                                System.out.println(todo_list.toString() + " " + todo_list.getTasks());
                            }
                            UserTodoLists userTodoLists = new UserTodoLists(userLists);
                            //userTodoLists.setUserTodoLists(userLists);

                            out.writeInt(TypeId.LISTS);
                            String userListsString = new Gson().toJson(userTodoLists);
                            out.writeUTF(userListsString); // saadab tagasi

                            break;
                        case "addlist": // {"addlist", userID}
                            userIDstring = infoIn.get(1);

                            try {
                                userID = Integer.parseInt(userIDstring);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }

                            String todoID = dbc.newTodo(userID);

                            System.out.println("Added new todo list for user " + userID);
                            out.writeInt(TypeId.STRING);
                            out.writeUTF(todoID);
                            break;
                        case "addtask":
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date currentdate = new Date();

                            String entrydate = df.format(currentdate);
                            String date = infoIn.get(1);
                            String head = infoIn.get(2);
                            String description = infoIn.get(3);
                            String todoIdForTask = infoIn.get(4);


                            Task ntask = new Task(entrydate, date, head, description, false);
                            ntask.setTodoListID(todoIdForTask);
                            System.out.println("Add task: " + ntask);
                            try {
                                String taskID = dbc.addTask(ntask);
                                out.writeInt(TypeId.STRING);
                                out.writeUTF(taskID);

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            System.out.println("added task: " + ntask.toString());

                            break;
                        case "deletetask": // {"deletetask", task.getTaskID()}
                            indexString = infoIn.get(1);

                            try {
                                index = Integer.parseInt(indexString);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }
                            dbc.deleteTask(index);

                            System.out.println("Task deleted successfully.");
                            out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                            break;

                        case "done":

                            indexString = infoIn.get(1);
                            try {
                                index = Integer.parseInt(indexString);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }
                            dbc.markAsDone(index);

                            System.out.println("Task marked as done.");
                            out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst


                            break;
                        case "undone":

                            indexString = infoIn.get(1);
                            try {
                                index = Integer.parseInt(indexString);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }

                            dbc.markAsUnDone(index);

                            System.out.println("Task marked as undone.");
                            out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst


                            break;
                        case "descedit": // {"descedit", task.getTaskID(), fieldtext};

                            indexString = infoIn.get(1);
                            try {
                                index = Integer.parseInt(indexString);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }
                            String taskDescription = infoIn.get(2);
                            dbc.changeDescription(index, taskDescription);

                            System.out.println("Task description changed to: " + taskDescription);
                            out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst


                            break;

                        case "dateedit": // {"dateedit", task.getTaskID(), duedate};

                            indexString = infoIn.get(1);
                            try {
                                index = Integer.parseInt(indexString);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }

                            String taskDuedate = infoIn.get(2);
                            dbc.changeDueDate(index, taskDuedate);

                            System.out.println("Task duedate changed to: " + taskDuedate);
                            out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst


                            break;
                        case "renametask": // {"renametask", task.getTaskID(), fieldtext};

                            indexString = infoIn.get(1);
                            try {
                                index = Integer.parseInt(indexString);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }
                            String taskHeadline = infoIn.get(2);
                            dbc.changeHeadline(index, taskHeadline);

                            System.out.println("Task headline changed to: " + taskHeadline);
                            out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                            break;
                        case "renametodo": // {"renametodo", todo_list.getTodoListID(), fieldtext};

                            indexString = infoIn.get(1);
                            try {
                                index = Integer.parseInt(indexString);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }
                            String todoDescription = infoIn.get(2);
                            dbc.changeTodoDescription(index, todoDescription);

                            System.out.println("Todolist name changed to: " + todoDescription);
                            out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst


                            break;
                        case "checkuserRegister":

                            username = infoIn.get(1);

                            dbc.checkuserRegister(username);

                            System.out.println("User found: " + username); // kui leiab sellise usernameiga useri, siis ei lase regada
                            out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                            break;
                        case "register":

                            username = infoIn.get(1);
                            password = infoIn.get(2);
                            boolean isSuccess = dbc.register(username, password);

                            if (isSuccess) {
                                System.out.println("User signed up: " + username + " " + password);
                                out.writeInt(TypeId.EMPTY); // TODO siin võiks saata TypeId.SUCCESS
                            } else {
                                System.out.println("Username already exists! @echo");
                                out.writeInt(TypeId.ERROR); // TODO siin võiks saata TypeId.ERRORi ja errormessage
                            }


                            break;
                        case "checkuserLogin": // vist ei ole ikka vaja?

                            username = infoIn.get(1);
                            password = infoIn.get(2);
                            boolean userFound = dbc.checkuserLogin(username, password);

                            System.out.println("User found: " + userFound);

                            out.writeInt(TypeId.BOOLEAN);
                            out.writeBoolean(userFound);

                            break;
                        case "login":

                            username = infoIn.get(1);
                            password = infoIn.get(2);
                            String userIDString = dbc.login(username, password);

                            out.writeInt(TypeId.STRING);
                            out.writeUTF(userIDString);

                            System.out.println("User logged in: " + username + " " + password);

                            break;
                        default:
                            System.out.println("Not a command!");

                            break;
                    }
                } catch (SQLException | RuntimeException e) {
                    out.writeInt(TypeId.ERROR);
                    e.printStackTrace();
                }


                System.out.println("cleaned up");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}