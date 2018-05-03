package listfiles.Connection;

import com.google.gson.Gson;
import listfiles.DataBaseCommands;
import listfiles.Task;
import listfiles.Todo_list;
import listfiles.TypeId;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
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
                    System.out.println(command + " " + infoIn);
                    int userID;
                    String userIDstring;

                    switch (command) {
                        case "get lists": // {"get lists", userID}
                            userIDstring = infoIn.get(1);
                            userID = Integer.parseInt(userIDstring);

                            List<Todo_list> userLists = dbc.getAllUserLists(userID);

                            for (Todo_list todo_list : userLists) {
                                System.out.println(todo_list.toString()+ " " + todo_list.getTasks());
                            }

                            out.writeInt(TypeId.LISTS);
                            String userListsString = new Gson().toJson(userLists);
                            out.writeUTF(userListsString); // saadab tagasi

                            break;
                        case "addlist": // {"addlist", userID}
                            userIDstring = infoIn.get(1);
                            userID = Integer.parseInt(userIDstring);

                            String todoID = dbc.newTodo(userID);

                            System.out.println("Added new todo list");
                            out.writeInt(TypeId.STRING);
                            out.writeUTF(todoID);
                            break;
                        case "addtask":
                            System.out.println("Add task: ");

                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date currentdate = new Date();

                            String entrydate = df.format(currentdate);
                            String date = infoIn.get(1);
                            String head = infoIn.get(2);
                            String text = infoIn.get(3);


                            Task ntask = new Task(entrydate, date, head, text, false);
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
                            //while (true) { // TODO miks while tsykkel?
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);
                                dbc.deleteTask(index);

                                System.out.println("Task deleted successfully.");
                                out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            //}
                            break;
                        case "done":
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);
                                dbc.markAsDone(index);

                                System.out.println("Task marked as done.");
                                out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "undone":
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring); // TODO try ainult parse ümber
                                dbc.markAsUnDone(index);

                                System.out.println("Task marked as undone.");
                                out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "descedit": // {"descedit", task.getTaskID(), fieldtext};
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);

                                String taskDescription = infoIn.get(2);
                                dbc.changeText(index, taskDescription);

                                System.out.println("Task description changed to: " + taskDescription);
                                out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;

                        case "dateedit": // {"dateedit", task.getTaskID(), duedate};
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);

                                String taskDuedate = infoIn.get(2);
                                dbc.changeDueDate(index, taskDuedate);

                                System.out.println("Task duedate changed to: " + taskDuedate);
                                out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "renametask": // {"renametask", task.getTaskID(), fieldtext};
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);

                                String taskHeadline = infoIn.get(2);
                                dbc.changeHeadline(index, taskHeadline);

                                System.out.println("Task headline changed to: " + taskHeadline);
                                out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "renametodo": // {"renametodo", todo_list.getTodo_listID(), fieldtext};
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);

                                String todoDescription = infoIn.get(2);
                                dbc.changeTodoDescription(index, todoDescription);

                                System.out.println("Todolist name changed to: " + todoDescription);
                                out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "checkuserRegister":
                            try {
                                String username = infoIn.get(1);

                                dbc.checkuserRegister(username);

                                System.out.println("User found: " + username); // kui leiab sellise usernameiga useri, siis ei lase regada
                                out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst


                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }

                            break;
                        case "register":
                            try {
                                String username = infoIn.get(1);
                                String password = infoIn.get(2);
                                dbc.register(username, password);

                                System.out.println("User signed up: " + username + " " + password);
                                out.writeInt(TypeId.EMPTY); // saadab kinnituse, et midagi ei ole vaja tagastada pmst

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "checkuserLogin": // vist ei ole ikka vaja?
                            try {
                                String username = infoIn.get(1);
                                String password = infoIn.get(2);
                                boolean userFound = dbc.checkuserLogin(username, password);

                                System.out.println("User found: " + userFound);

                                out.writeInt(TypeId.BOOLEAN);
                                out.writeBoolean(userFound);

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "login":
                            try {
                                String username = infoIn.get(1);
                                String password = infoIn.get(2);
                                String userIDString = dbc.login(username, password);

                                out.writeInt(TypeId.STRING);
                                out.writeUTF(userIDString);

                                System.out.println("User logged in: " + username + " " + password);

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        default:
                            System.out.println("Not a command!");

                            break;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                System.out.println("cleaned up");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*public List<String> getInfo() {
        return infoIn;
    }*/

}