package listfiles;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TodoApp extends Application {
    static int server = 1337;
    private String selectedTodo = "0";
    private String userID = "0";
    private String selectedTask = "0";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //primaryStage.initStyle(StageStyle.UNDECORATED);


        // To-do lists for testing:

        /*List<Task> testtasklist = new ArrayList<>(Arrays.asList(
                new Task("2005-01-12 08:02:00", "2005-01-12 08:02:00", "head1", "desc1", false),
                new Task("2005-01-12 08:02:00", "2005-01-12 08:02:00", "head2", "desc2", false)
        ));
        testtasklist.get(0).setTaskID("111");
        testtasklist.get(1).setTaskID("222");

        List<Task> testtasklist2 = new ArrayList<>();
        testtasklist2.add(new Task("2005-01-12 08:02:00", "2005-01-12 08:02:00", "head3", "desc3", true));
        testtasklist2.get(0).setTaskID("333");

        Todo_list testtodo1 = new Todo_list(testtasklist, "testtodo1");
        Todo_list testtodo2 = new Todo_list(testtasklist2, "testtodo2");

        List<Todo_list> testlist = new ArrayList<>();
        testlist.add(testtodo1);
        testlist.add(testtodo2);*/

        BorderPane borderPane = new BorderPane();

        ///// [LISTAREA] ///////////////////////////////////////////////////////////////////////////////////////////////

        TabPane tabPane = new TabPane();

        ///// [SIDEBAR} ////////////////////////////////////////////////////////////////////////////////////////////////

        Button addListButton = new Button("New to-do");
        addListButton.setOnAction(event -> {
            Todo_list ntodo = new Todo_list(new ArrayList<>(), "New To-do list");

            String[] command = {"addlist", userID};

            try {
                ntodo.setTodo_listID((String) commandHandler(command));
                System.out.println("läks korda");
            } catch (Exception e) {
                e.printStackTrace();
            }


            tabPane.getTabs().add(tabAdder(ntodo));
            tabPane.getSelectionModel().selectLast();

        }); // CREATE NEW TO-DO LIST


        Region region2 = new Region();
        VBox.setVgrow(region2, Priority.ALWAYS);

        Image imagerefresh = new Image("filerefresh.png");
        ImageView refreshimage = new ImageView(imagerefresh);
        refreshimage.setFitHeight(45);
        refreshimage.setFitWidth(45);
        Button refreshbutton = new Button();
        refreshbutton.setGraphic(refreshimage); // natuke väike, aga töötab ja hetkel rohkema aega ei kulutaks
        refreshbutton.setStyle("-fx-background-color: transparent");

        refreshbutton.setOnAction(event -> {
            System.out.println("haha, no refresh for u");
            /*for (Todo_list todo_list : testlist) {
                tabPane.getTabs().add(tabAdder(todo_list));
            }*/
        }); // TODO hetkel lisab ainult juurde

        VBox vBox = new VBox();
        vBox.getChildren().addAll(addListButton, region2, new Label("[Sidebar]"), refreshbutton);


        ///// [primaryStage setup] /////////////////////////////////////////////////////////////////////////////////////


        borderPane.setPadding(new Insets(5, 5, 5, 5)); // BORDERPANE SETUP
        //borderPane.setTop(hBox);
        //borderPane.setBottom(hBox2);
        borderPane.setCenter(tabPane);
        borderPane.setRight(vBox);


        //primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("resources/Drop.png"))); // TODO ükski ei tööta, aga pole nii oluline
        //primaryStage.getIcons().add(new Image(getClass().getResource("resources/Drop.png").toExternalForm()));
        //primaryStage.getIcons().add(new Image("/resources/Drop.png"));
        //ImageView programicon = new ImageView(new Image(new File("resources/Drop.png").toURI().toString()));
        //primaryStage.getIcons().add(new Image(new File("resources/Drop.png").toURI().toString()));


        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Bye!");

        });

        Scene scene = new Scene(borderPane, 400, 500, Color.SNOW);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drop");
        primaryStage.show();

        ///// [LOGIN] //////////////////////////////////////////////////////////////////////////////////////////////////

        BorderPane loginPane = new BorderPane();
        GridPane loginDataPane = new GridPane();

        Label usernameLabel = new Label("Username: ");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password: ");
        TextField passwordField = new TextField();

        Button loginButton = new Button("Login");
        loginButton.setOnAction(loginEvent -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            String[] command = {"checkuserLogin", username, password};
            boolean userexists = true;
            try {
                //userexists = (boolean) commandHandler(command); // TODO
                System.out.println("läks korda");

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (userexists) {
                // otsib andmebaasist need todo_listid, millele on kasutajal juurdepääs
                List<Todo_list> allUserTodoLists = new ArrayList<>();
                String[] command2 = {"login", username, password};

                try {
                    userID = (String) commandHandler(command2);
                    String[] command3 = {"get lists", userID};
                    allUserTodoLists = (List<Todo_list>) commandHandler(command3);
                    System.out.println("läks korda" + userID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (Todo_list todo_list : allUserTodoLists) {
                    tabPane.getTabs().add(tabAdder(todo_list));
                }
                ((Node) (loginEvent.getSource())).getScene().getWindow().hide();
            } else {
                Stage loginerror = new Stage();

                loginerror.setScene(new Scene(new Label("Invalid username or password")));
                loginerror.setAlwaysOnTop(true);
                loginerror.show();
            }
        });
        Button registerButton = new Button("Sign up");
        registerButton.setOnAction(registerEvent -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            String[] command = {"checkuserRegister", username};
            boolean userexists = false;

            try {
                //userexists = (boolean) commandHandler(command); // TODO

                System.out.println("läks korda");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO saab tagasi booleani, et kas on juba sama nimega kedagi

            if (userexists) { // leidub kasutaja sama usernamega
                Stage registererror = new Stage();

                registererror.setScene(new Scene(new Label("Username already taken")));
                registererror.setAlwaysOnTop(true);
                registererror.show();
            } else {
                String[] command2 = {"register", username, password};

                try {
                    commandHandler(command2);
                    System.out.println("läks korda");
                    Stage registersuccess = new Stage();

                    registersuccess.setScene(new Scene(new Label("User registered")));
                    registersuccess.setAlwaysOnTop(true);
                    registersuccess.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        HBox buttonBox = new HBox();
        Region region3 = new Region();
        HBox.setHgrow(region3, Priority.ALWAYS);
        buttonBox.getChildren().addAll(region3, loginButton, registerButton);

        loginDataPane.add(usernameLabel, 0, 0);
        loginDataPane.add(usernameField, 1, 0);
        loginDataPane.add(passwordLabel, 0, 1);
        loginDataPane.add(passwordField, 1, 1);
        loginDataPane.add(buttonBox, 1, 4);

        loginPane.setCenter(loginDataPane);

        Scene loginScene = new Scene(loginPane);
        Stage loginStage = new Stage();
        loginStage.setScene(loginScene);
        loginStage.setAlwaysOnTop(true);
        loginStage.show();


    }

    private Tab tabAdder(Todo_list todo_list) {
        BorderPane todoTabPane = new BorderPane();
        Tab todolistTab = new Tab();

        todolistTab.setText(todo_list.getDescription());

        List<Task> tasks = todo_list.getTasks();
        //ObservableList<Task> observableList = FXCollections.observableArrayList(tasks);
        //ListView<Task> taskView = new ListView<>(observableList);
        GridPane tasksPane = new GridPane();

        for (int i = 0; i < tasks.size(); i++) {
            tasksPane.add(taskPaneAdder(tasks.get(i)), 0, i);
        }

        Button renameTodo = new Button(todo_list.getDescription());
        renameTodo.setOnAction(event -> {
            TextField renamefield = new TextField("Insert new name");
            renamefield.setOnAction(event1 -> {

                String fieldtext = renamefield.getText();
                todolistTab.setText(fieldtext);
                todo_list.setDescription(fieldtext);
                renameTodo.setText(fieldtext);

                String[] command = {"renametodo", todo_list.getTodo_listID(), fieldtext};
                try {
                    commandHandler(command);
                    System.out.println("läks korda");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((Node) (event1.getSource())).getScene().getWindow().hide();
            });


            Scene renamelistScene = new Scene(renamefield);
            Stage renamelistStage = new Stage();
            renamelistStage.setTitle("Rename list");
            renamelistStage.setScene(renamelistScene);
            renamelistStage.show();

        });

        Image imageplus = new Image("fileplus.png");
        ImageView addim = new ImageView(imageplus);
        Button addTaskButton = new Button();
        addim.setFitWidth(30);
        addim.setFitHeight(30);
        addTaskButton.setGraphic(addim);
        addTaskButton.setStyle("-fx-background-color: transparent");

        addTaskButton.setOnAction(addEvent -> {

            Stage addstage = new Stage();
            addstage.setTitle("New task");

            Label headlabel = new Label("Title");
            TextField headlinefield = new TextField();
            Label desclabel = new Label("Description");
            TextField descriptionfield = new TextField();


            //String duedate = df.format();

            ObservableList<String> hours = FXCollections.observableArrayList(new ArrayList<>());
            ObservableList<String> minutes = FXCollections.observableArrayList(new ArrayList<>());

            for (int i = 0; i < 60; i++) {
                String hrs = "";
                String mins = "";
                if (i < 10) {
                    hrs += "0";
                    mins += "0";
                }
                if (i < 24) {
                    hrs += i;
                    hours.add(hrs);
                }
                mins += i;
                minutes.add(mins);
            }

            SpinnerValueFactory<String> duedateHours = new SpinnerValueFactory.ListSpinnerValueFactory(hours);
            Spinner<String> duedateHoursSpinner = new Spinner<>();
            duedateHoursSpinner.setValueFactory(duedateHours);
            SpinnerValueFactory<String> duedateMinutes = new SpinnerValueFactory.ListSpinnerValueFactory(minutes);
            Spinner<String> duedateMinutesSpinner = new Spinner<>();
            duedateMinutesSpinner.setValueFactory(duedateMinutes);
            Label timeSeparator = new Label(":");
            HBox timePickerBox = new HBox();

            //duedateHoursSpinner.maxWidth(20); // ei tööta TODO uuri miks (vähemoluline)
            //duedateHoursSpinner.maxHeight(20);
            //duedateMinutesSpinner.maxWidth(20);

            timePickerBox.getChildren().addAll(duedateHoursSpinner, timeSeparator, duedateMinutesSpinner);

            //DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            DatePicker datePicker = new DatePicker();


            Button addtask = new Button("Add");
            addtask.setOnAction(event1 -> {
                //Date currentdate = new Date();
                //String creationdate = df.format(currentdate);
                String duedate = datePicker.getValue() + " " + duedateHoursSpinner.getValue() + ":" + duedateMinutesSpinner.getValue() + ":00";

                if (datePicker.getValue() == null) {
                    duedate = "2020-01-01";
                }


                String[] command = {"addtask", duedate, headlinefield.getText(), descriptionfield.getText(), todo_list.getTodo_listID()};
                String taskID = "0";

                try {
                    System.out.println("läks korda");
                    taskID = (String) commandHandler(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Task ntask = new Task(duedate, duedate, headlinefield.getText(), descriptionfield.getText(), false);
                ntask.setTaskID(taskID);

                todo_list.getTasks().add(ntask);

                tasksPane.add(taskPaneAdder(ntask), 0, todo_list.getTasks().size());
                //taskPaneAdder(ntask);

                //System.out.println(ntask);
                addstage.close(); // kui additud, siis paneb kinni akna

            }); // ADD


            Button canceladd = new Button("Cancel");
            canceladd.setOnAction(event1 -> addstage.close()); // CLOSE

            GridPane addtaskpane = new GridPane();
            addtaskpane.add(headlabel, 0, 0);
            addtaskpane.add(headlinefield, 1, 0);
            addtaskpane.add(desclabel, 0, 1);
            addtaskpane.add(descriptionfield, 1, 1);
            addtaskpane.add(datePicker, 1, 2);
            addtaskpane.add(timePickerBox, 1, 3);
            addtaskpane.add(addtask, 3, 4);
            addtaskpane.add(canceladd, 3, 5);


            addstage.setScene(new Scene(addtaskpane, 450, 160));
            addstage.show();

        });

        todoTabPane.setTop(new HBox(renameTodo));
        todoTabPane.setCenter(tasksPane);
        todoTabPane.setBottom(addTaskButton);

        todolistTab.setContent(todoTabPane);
        todolistTab.setClosable(false);

        todolistTab.setOnSelectionChanged(t -> {
            if (todolistTab.isSelected()) {
                this.selectedTodo = todo_list.getTodo_listID(); // kas nüüd üldse on vaja seda?
                System.out.println("Add nupp kehtib sellele listile");
            }
        });

        return todolistTab;
    }

    private BorderPane taskPaneAdder(Task task) {
        BorderPane taskBorderPane = new BorderPane();
        taskBorderPane.setPrefWidth(500);

        Label headlineLabel = new Label(task.getHeadline());
        headlineLabel.setOnMouseClicked(headEditEvent -> {
            TextField headEditField = new TextField("Insert new headline");
            headEditField.setOnAction(headEditFieldEditEvent -> {

                String fieldtext = headEditField.getText();
                System.out.println(fieldtext);

                headlineLabel.setText(fieldtext);

                task.setHeadline(fieldtext);

                String[] command = {"renametask", task.getTaskID(), fieldtext};

                try {
                    commandHandler(command);
                    System.out.println("läks korda");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((Node) (headEditFieldEditEvent.getSource())).getScene().getWindow().hide();
            });

            Scene headScene = new Scene(headEditField);
            Stage headStage = new Stage();
            headStage.setScene(headScene);
            headStage.setAlwaysOnTop(true);
            headStage.show();
        });
        Label duedateLabel = new Label(task.getDeadline());
        duedateLabel.setOnMouseClicked(dateEditEvent -> {

            ObservableList<String> hours = FXCollections.observableArrayList(new ArrayList<>());
            ObservableList<String> minutes = FXCollections.observableArrayList(new ArrayList<>());

            for (int i = 0; i < 60; i++) {
                String hrs = "";
                String mins = "";
                if (i < 10) {
                    hrs += "0";
                    mins += "0";
                }
                if (i < 24) {
                    hrs += i;
                    hours.add(hrs);
                }
                mins += i;
                minutes.add(mins);
            }

            SpinnerValueFactory<String> duedateHours = new SpinnerValueFactory.ListSpinnerValueFactory(hours);
            Spinner<String> duedateHoursSpinner = new Spinner<>();
            duedateHoursSpinner.setValueFactory(duedateHours);
            SpinnerValueFactory<String> duedateMinutes = new SpinnerValueFactory.ListSpinnerValueFactory(minutes);
            Spinner<String> duedateMinutesSpinner = new Spinner<>();
            duedateMinutesSpinner.setValueFactory(duedateMinutes);
            Label timeSeparator = new Label(":");
            HBox timePickerBox = new HBox();

            timePickerBox.getChildren().addAll(duedateHoursSpinner, timeSeparator, duedateMinutesSpinner);

            DatePicker datePicker = new DatePicker();

            Button dateEditButton = new Button("Edit");
            dateEditButton.setOnAction(dateEditButtonEvent -> {
                String duedate = datePicker.getValue() + " " + duedateHoursSpinner.getValue() + ":" + duedateMinutesSpinner.getValue() + ":00";
                task.setDeadline(duedate);
                duedateLabel.setText(duedate);

                String[] command = {"dateedit", task.getTaskID(), duedate};

                try {
                    commandHandler(command);
                    System.out.println("läks korda");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((Node) (dateEditButtonEvent.getSource())).getScene().getWindow().hide();

            });

            VBox dateTimeEditBox = new VBox();
            dateTimeEditBox.getChildren().addAll(datePicker, timePickerBox, dateEditButton);


            Scene dateScene = new Scene(dateTimeEditBox);
            Stage dateStage = new Stage();
            dateStage.setScene(dateScene);
            dateStage.setAlwaysOnTop(true);

            dateStage.setTitle("New duedate");
            dateStage.show();
        });
        Label descriptionLabel = new Label(task.getDescription());
        descriptionLabel.setOnMouseClicked(descEditEvent -> {
            TextField descEditField = new TextField("Insert new description");
            descEditField.setOnAction(descEditFieldEditEvent -> {

                String fieldtext = descEditField.getText();

                descriptionLabel.setText(fieldtext);

                task.setDescription(fieldtext);

                String[] command = {"descedit", task.getTaskID(), fieldtext};

                try {
                    commandHandler(command);
                    System.out.println("läks korda");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((Node) (descEditFieldEditEvent.getSource())).getScene().getWindow().hide();
            });

            Scene descScene = new Scene(descEditField);
            Stage descStage = new Stage();
            descStage.setScene(descScene);
            descStage.setAlwaysOnTop(true);
            descStage.show();
        });


        HBox taskHead = new HBox();
        Region taskHeadRegion = new Region();
        HBox.setHgrow(taskHeadRegion, Priority.ALWAYS);
        taskHead.getChildren().addAll(headlineLabel, taskHeadRegion, duedateLabel);

        Button deleteTaskButton = new Button("Delete");
        deleteTaskButton.setOnAction(editEvent -> {
            String[] command = {"deletetask", task.getTaskID()};

            try {
                commandHandler(command);
                System.out.println("läks korda");
                taskBorderPane.setManaged(false);
                taskBorderPane.setVisible(false);

            } catch (Exception e) {
                e.printStackTrace();
            }


        });
        CheckBox doneCheckBox = new CheckBox("Done");
        doneCheckBox.setSelected(task.getDone());
        doneCheckBox.setOnAction(doneActionEvent -> {
            if (doneCheckBox.isSelected()) {
                String[] command = {"done", task.getTaskID()};

                try {
                    commandHandler(command);
                    System.out.println("läks korda");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!doneCheckBox.isSelected()) {
                String[] command = {"undone", task.getTaskID()};

                try {
                    commandHandler(command);
                    System.out.println("läks korda");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        HBox taskRight = new HBox();
        taskRight.getChildren().addAll(doneCheckBox, deleteTaskButton);

        taskBorderPane.setTop(taskHead);
        taskBorderPane.setLeft(descriptionLabel);
        taskBorderPane.setRight(taskRight);

        return taskBorderPane;
    }

    public static Object commandHandler(String[] command) throws Exception {

        //formaat: 2005-01-12 08:02:00;juust;kapsas
        System.out.println("connecting to server: " + server);

        try (
                Socket socket = new Socket("localhost", server);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream())

        ) {
            System.out.println("connected; sending data");


            out.writeInt(command.length);
            System.out.println(command.length);

            for (int i = 0; i < command.length; i++) { // TODO siin tekib mingi nullpointer
                out.writeUTF(command[i]);
                System.out.println("sent " + command[i]);
            }
            Object o = null;
            String commandtype = command[0];
            System.out.println(commandtype);
            int returnType = in.readInt(); // tuleb tagastustyyp

            switch (returnType) {
                case TypeId.BOOLEAN:
                    boolean receivedBoolean = in.readBoolean();
                    o = receivedBoolean;
                    break;
                case TypeId.STRING:
                    String receivedString = in.readUTF();
                    o = receivedString;
                    break;
                case TypeId.LISTS:
                    String userListsString = in.readUTF();
                    List<Todo_list> userLists = new Gson().fromJson(userListsString, ArrayList.class);
                    o = userLists;
                    break;
                case TypeId.EMPTY:
                    System.out.println("ei tule siit midagi, meelega");
                default:
                    System.out.println("ei tule siit midagi");
            }

            System.out.println("cleaned up");
            return o;
        }
    }

    public void addTaskButtonEventMethod(ActionEvent addTaskButtonEvent) {
        // Todo
    }
}            // TODO tuleb kontrollida ühendust serveriga, muidu viskab errorisse



/*    public static Object commandReceiver(String commandtype) throws Exception {
            Object o = new Object();
        try (
                Socket socket = new Socket("localhost", server);
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            switch (commandtype) {
                case "checkuserRegister":
                    o = in.readBoolean();
                    break;
                case "checkuserLogin":
                    o = in.readBoolean();
                    break;
                case "login":
                    o = in.readUTF();
                    break;
                case "addlist":
                    o = in.readUTF();
                    break;
                case "addtask":
                    o = in.readUTF();
                    break;
                case "get lists":
                    o = in.readObject();
                    break;
                default:
                    System.out.println("ei tule siit midagi");
            }

        }
        return o;
    }*/

