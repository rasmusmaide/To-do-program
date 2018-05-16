package listfiles;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TodoApp extends Application {
    static int server = 1337;
    private String selectedTodo = "0";
    private String userID;
    private String selectedTask = "0"; // hetkel pole neid selectedT-sid vaja, aga addTaskButtonMethodiga on vist

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        BorderPane borderPane = new BorderPane();

        ///// [LISTAREA] ///////////////////////////////////////////////////////////////////////////////////////////////

        TabPane tabPane = new TabPane();

        ///// [SIDEBAR} ////////////////////////////////////////////////////////////////////////////////////////////////

        Button addListButton = new Button("New to-do");
        addListButton.setOnAction(event -> {
            TodoList ntodo = new TodoList(new ArrayList<>(), "New To-do list");

            String[] command = {"addlist", userID};

            try {
                ntodo.setTodoListID((String) commandHandler(command));
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
                List<TodoList> allUserTodoLists = new ArrayList<>();
                String[] command2 = {"login", username, password};

                try {
                    userID = (String) commandHandler(command2);
                    String[] command3 = {"get lists", userID};
                    allUserTodoLists = (List<TodoList>) commandHandler(command3);
                    System.out.println("läks korda" + userID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(allUserTodoLists);
                for (TodoList todo_list : allUserTodoLists) {
                    tabPane.getTabs().add(tabAdder(todo_list)); // TODO
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
        loginStage.setOnCloseRequest(loginStageCloseEvent -> {
            Platform.exit();
        });
        loginStage.show();


    }

    private Tab tabAdder(TodoList todoList) {
        BorderPane todoTabPane = new BorderPane();
        Tab todolistTab = new Tab();

        todolistTab.setText(todoList.getDescription());

        List<Task> tasks = todoList.getTasks();

        GridPane tasksPane = new GridPane();

        for (int i = 0; i < tasks.size(); i++) {
            tasksPane.add(taskPaneAdder(tasks.get(i)), 0, i);
        }

        Button renameTodo = new Button(todoList.getDescription());
        renameTodo.setOnAction(event -> {
            TextField renamefield = new TextField("Insert new name");
            renamefield.setOnAction(event1 -> {

                String fieldtext = renamefield.getText();
                todolistTab.setText(fieldtext);
                todoList.setDescription(fieldtext);
                renameTodo.setText(fieldtext);

                String[] command = {"renametodo", todoList.getTodoListID(), fieldtext};
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

            DatePicker datePicker = new DatePicker();

            Button addtask = new Button("Add");
            addtask.setOnAction(event1 -> {
                String duedate = datePicker.getValue() + " " + duedateHoursSpinner.getValue() + ":" + duedateMinutesSpinner.getValue() + ":00";

                if (datePicker.getValue() == null) {
                    duedate = "2020-01-01";
                }


                String[] command = {"addtask", duedate, headlinefield.getText(), descriptionfield.getText(), todoList.getTodoListID()};
                String taskID = "0";

                try {
                    System.out.println("läks korda");
                    taskID = (String) commandHandler(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Task ntask = new Task(duedate, duedate, headlinefield.getText(), descriptionfield.getText(), false);
                ntask.setTaskID(taskID);

                todoList.getTasks().add(ntask);

                tasksPane.add(taskPaneAdder(ntask), 0, todoList.getTasks().size());
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
                this.selectedTodo = todoList.getTodoListID(); // kas nüüd üldse on vaja seda?
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

    private static Object commandHandler(String[] command) throws Exception {

        System.out.println("connecting to server: " + server);
        Object o = null;
        //Stage errorStage = new ErrorStage().getError();

        while (true) {
            try (
                    Socket socket = new Socket("localhost", server);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream())

            ) {
                System.out.println("connected; sending data");
                //errorStage.hide();

                out.writeInt(command.length);

                for (int i = 0; i < command.length; i++) {
                    out.writeUTF(command[i]);
                    System.out.println("sent " + command[i]);
                }

                //String commandtype = command[0];
                //System.out.println(commandtype);
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
                        UserTodoLists userTodoLists = new Gson().fromJson(userListsString, UserTodoLists.class);
                        List<TodoList> userLists = userTodoLists.getUserTodoLists();
                        o = userLists;
                        break;
                    case TypeId.EMPTY:
                        System.out.println("ei tule siit midagi, meelega");
                        break;
                    case TypeId.ERROR:
                        System.out.println("Midagi läks valesti");
                        // siin võiks vist mingi exceptioni visata või öelda kasutajale, et midagi läks valesti
                        // errorStage(String errorMessage)?
                        break;
                    default:
                        System.out.println("ei tule siit midagi");
                }

                break;
            } catch (ConnectException e) {

                System.out.println("Ühendus puudub, proovin uuesti");
                //errorStage.show();
            }
        }


        System.out.println("cleaned up");
        return o;


    }

    public void addTaskButtonEventMethod(ActionEvent addTaskButtonEvent) {
        // Todo, pole prioriteet, aga kunagi võib teha uue classi, mille objektid on tabid, millel on omad tunnused(todoID) ja värki
        // siis saab selle eraldi meetodiks tõsta
    }
}            // TODO tuleb kontrollida ühendust serveriga, muidu viskab errorisse

