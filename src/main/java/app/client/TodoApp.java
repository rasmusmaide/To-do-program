package app.client;

import app.Task;
import app.TodoList;
import app.TypeId;
import app.UserTodoLists;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TodoApp extends Application {
    static int server = 1337;
    private int userID;

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
            Stage promptstage = new Stage();
            TextField renamefield = new TextField();
            renamefield.setPromptText("Insert new name");
            promptstage.setScene(new Scene(renamefield));
            promptstage.show();
            renamefield.setOnAction(setTodoNameEvent -> {
                String todoDescription = renamefield.getText();
                if (todoDescription.trim().length() == 0) {
                    Stage errorstage = errorStageMethod("List name can't be empty", setTodoNameEvent);
                    errorstage.show();
                } else {
                    ntodo.setDescription(todoDescription);

                    ((Node) (setTodoNameEvent.getSource())).getScene().getWindow().hide();

                    String[] addTodoCommand = {"addlist", todoDescription, String.valueOf(userID)};
                    boolean delete = false;

                    try {
                        ntodo.setTodoListID((int) commandHandler(addTodoCommand));
                        System.out.println("läks korda");
                    } catch (ConnectException ce) {
                        Stage errorstage = errorStageMethod("No connection", setTodoNameEvent);
                        errorstage.show();
                        delete = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!delete) {
                        tabPane.getTabs().add(tabAdder(ntodo));
                        tabPane.getSelectionModel().selectLast();
                    }
                }
            });


        }); // CREATE NEW TO-DO LIST
        addListButton.setTooltip(new Tooltip("Add new To-do list"));


        Region region2 = new Region();
        VBox.setVgrow(region2, Priority.ALWAYS);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(addListButton, region2);


        ///// [primaryStage setup] /////////////////////////////////////////////////////////////////////////////////////


        borderPane.setPadding(new Insets(5, 5, 5, 5)); // BORDERPANE SETUP
        //borderPane.setTop(hBox);
        //borderPane.setBottom(hBox2);
        borderPane.setCenter(tabPane);
        borderPane.setRight(vBox);
        borderPane.setBackground(new Background(new BackgroundFill(Color.SNOW, CornerRadii.EMPTY, Insets.EMPTY)));


        //primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("fileplus.png"))); // TODO ükski ei tööta, aga pole nii oluline
        //primaryStage.getIcons().add(new Image(getClass().getResource("resources/Drop.png").toExternalForm()));
        //primaryStage.getIcons().add(new Image("/resources/Drop.png"));
        //ImageView programicon = new ImageView(new Image(new File("resources/Drop.png").toURI().toString()));
        //primaryStage.getIcons().add(new Image(new File("resources/Drop.png").toURI().toString()));

        primaryStage.getIcons().add(new Image("Drop-icon.png"));




        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Bye!");

        });

        Scene scene = new Scene(borderPane, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drop");
        primaryStage.show();

        ///// [LOGIN] //////////////////////////////////////////////////////////////////////////////////////////////////

        BorderPane loginPane = new BorderPane();
        GridPane loginDataPane = new GridPane();

        Label usernameLabel = new Label("Username: ");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password: ");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        loginButton.setOnAction(loginEvent -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            List<TodoList> allUserTodoLists = new ArrayList<>();
            String[] loginCommand = {"login", username, password};

            try {
                userID = (Integer) commandHandler(loginCommand); // siit peab tulema kas ID või null kui error
            } catch (ConnectException e) {
                userID = -1;
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(userID + " userID @loginbutton");
            if (userID == TypeId.EMPTY || userID == TypeId.ERROR) {
                Stage loginerror = errorStageMethod("Invalid username or password", loginEvent);

                loginerror.show();
            } else if (userID == -1) {
                Stage connectionError = errorStageMethod("No connection", loginEvent);
                connectionError.show();
            } else {
                //Thread notificationThread = new NotificationThread(server, userID);
                //notificationThread.start();
                // otsib andmebaasist need todo_listid, millele on kasutajal juurdepääs
                String[] getListsCommand = {"get lists", String.valueOf(userID)};
                try {
                    allUserTodoLists = (List<TodoList>) commandHandler(getListsCommand);
                } catch (ConnectException e) {
                    Stage connectionError = errorStageMethod("No connection", loginEvent);

                    connectionError.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("läks korda " + userID + " @getListsCommand");

                System.out.println(allUserTodoLists);
                for (TodoList todo_list : allUserTodoLists) {
                    tabPane.getTabs().add(tabAdder(todo_list));
                }
                ((Node) (loginEvent.getSource())).getScene().getWindow().hide();
            }


        });
        Button registerButton = new Button("Sign up");
        registerButton.setOnAction(registerEvent -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String[] registerCommand = new String[3];
            if (username.length() != username.replace(" ", "").length()) {
                Stage errorstage = errorStageMethod("Username can't contain spaces", registerEvent);
                errorstage.show();
            } else if (username.length() < 3) {
                Stage errorstage = errorStageMethod("Username too short", registerEvent);
                errorstage.show();
            } else if (password.length() < 3) {
                Stage errorstage = errorStageMethod("Password too short", registerEvent);
                errorstage.show();
            } else {
                registerCommand[0] = "register";
                registerCommand[1] = username;
                registerCommand[2] = password;
            }

            if (registerCommand[2] != null) {

                try {
                    if ((Integer) commandHandler(registerCommand) == TypeId.ERROR) {
                        Stage registererror = errorStageMethod("Username already taken", registerEvent);

                        registererror.show();
                    } else {
                        System.out.println("läks korda");
                        Stage registersuccess = new Stage();

                        registersuccess.setScene(new Scene(new Label("User registered")));
                        registersuccess.setAlwaysOnTop(true);
                        registersuccess.show();
                    }
                } catch (ConnectException ce) {
                    Stage errorstage = errorStageMethod("No connection",registerEvent);
                    errorstage.show();
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
        loginDataPane.setPadding(new Insets(5, 5, 5, 5));
        loginDataPane.setBackground(new Background(new BackgroundFill(Color.SNOW, CornerRadii.EMPTY, Insets.EMPTY)));

        loginPane.setCenter(loginDataPane);

        Scene loginScene = new Scene(loginPane);
        Stage loginStage = new Stage();

        loginStage.initOwner(primaryStage);
        loginStage.initModality(Modality.WINDOW_MODAL);
        loginStage.requestFocus();

        loginStage.setScene(loginScene);
        //loginStage.setAlwaysOnTop(true);
        loginStage.setOnCloseRequest(loginStageCloseEvent -> Platform.exit());
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

        Button renameTodo = new Button();//"Rename list"
        ImageView renameTodoim = new ImageView(new Image("renametodo.png"));
        renameTodoim.setFitWidth(35);
        renameTodoim.setFitHeight(35);
        renameTodo.setGraphic(renameTodoim);
        renameTodo.setStyle("-fx-background-color: transparent");
        renameTodo.setOnAction(event -> {
            TextField renamefield = new TextField();
            renamefield.setPromptText("Insert list name");
            renamefield.setOnAction(event1 -> {

                String fieldtext = renamefield.getText();
                if (fieldtext.trim().length() == 0) {
                    Stage errorstage = errorStageMethod("List name can't be empty", event);
                    errorstage.show();
                } else {
                    todoList.setDescription(fieldtext);

                    String[] command = {"renametodo", String.valueOf(todoList.getTodoListID()), fieldtext};
                    try {
                        commandHandler(command);
                        todolistTab.setText(fieldtext);
                        System.out.println("läks korda");

                    } catch (ConnectException ce) {
                        Stage errorstage = errorStageMethod("No connection",event);
                        errorstage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ((Node) (event1.getSource())).getScene().getWindow().hide();
                }
            });


            Scene renamelistScene = new Scene(renamefield);
            Stage renamelistStage = new Stage();

            renamelistStage.initOwner(((Node) (event.getSource())).getScene().getWindow());
            renamelistStage.initModality(Modality.WINDOW_MODAL);
            renamelistStage.requestFocus();

            renamelistStage.setTitle("Rename list");
            renamelistStage.setScene(renamelistScene);
            renamelistStage.show();

        });
        renameTodo.setTooltip(new Tooltip("Rename To-do list"));




        ImageView addim = new ImageView(new Image("fileplus2.png"));
        ImageView addimActive = new ImageView(new Image("fileplusActive.png"));
        Button addTaskButton = new Button();
        addim.setFitWidth(35);
        addim.setFitHeight(35);
        addimActive.setFitWidth(35);
        addimActive.setFitHeight(35);
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

            timeLists(hours, minutes);

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
                    Stage dateErrorStage = errorStageMethod("Invalid date", addEvent);
                    dateErrorStage.show();
                } else if (headlinefield.getText().trim().length() == 0) {
                    Stage headlineErrorStage = errorStageMethod("Title can't be empty", addEvent);
                    headlineErrorStage.show();
                } else if (descriptionfield.getText().trim().length() == 0) {
                    Stage descriptionErrorStage = errorStageMethod("Description can't be empty", addEvent);
                    descriptionErrorStage.show();
                } else {


                    String[] command = {"addtask", duedate, headlinefield.getText(), descriptionfield.getText(), String.valueOf(todoList.getTodoListID())};
                    int taskID = 0;

                    boolean delete = false;
                    try {
                        System.out.println("läks korda");
                        taskID = (int) commandHandler(command);
                    } catch (ConnectException ce) {
                        Stage errorstage = errorStageMethod("No connection", addEvent);
                        errorstage.show();
                        delete = true;
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!delete) {
                        Task ntask = new Task(duedate, duedate, headlinefield.getText(), descriptionfield.getText(), false);
                        ntask.setTaskID(taskID);

                        todoList.getTasks().add(ntask);

                        tasksPane.add(taskPaneAdder(ntask), 0, todoList.getTasks().size());
                    }
                    //taskPaneAdder(ntask);

                    //System.out.println(ntask);
                    addstage.close(); // kui additud, siis paneb kinni akna
                }

            }); // ADD

            todoTabPane.setCenter(tasksPane);
            HBox bottomToolbar = new HBox();
            bottomToolbar.getChildren().addAll(addTaskButton, renameTodo);
            todoTabPane.setBottom(bottomToolbar);


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

            addstage.initOwner(((Node) (addEvent.getSource())).getScene().getWindow());
            addstage.initModality(Modality.WINDOW_MODAL);
            addstage.requestFocus();

            addstage.show();

        });
        addTaskButton.setOnMouseEntered(event -> addTaskButton.setGraphic(addimActive));
        addTaskButton.setOnMouseExited(event -> addTaskButton.setGraphic(addim));
        addTaskButton.setTooltip(new Tooltip("Add new Task"));


        todoTabPane.setCenter(tasksPane);
        HBox bottomToolbar = new HBox();
        bottomToolbar.getChildren().addAll(addTaskButton, renameTodo);
        todoTabPane.setBottom(bottomToolbar);

        todolistTab.setContent(todoTabPane);


        todolistTab.setOnCloseRequest(event -> {
            Stage confirmClose = new Stage();
            Label label = new Label("Do you want to delete the list?");
            Button yesButton = new Button("Yes");
            Button cancelButton = new Button("Cancel");
            FlowPane pane = new FlowPane(10, 10);
            pane.getChildren().addAll(yesButton, cancelButton);
            VBox vBox = new VBox(10);
            vBox.getChildren().addAll(label, pane);
            Scene confirmscene = new Scene(vBox);
            confirmClose.setScene(confirmscene);
            confirmClose.show();
            AtomicBoolean clicked = new AtomicBoolean(false);
            yesButton.setOnMouseClicked(confirmevent -> {
                String[] removeListsCommand = {"removelist", String.valueOf(todoList.getTodoListID())};
                boolean delete = false;
                try {
                    commandHandler(removeListsCommand);
                    System.out.println("läks korda");
                } catch (ConnectException ce){
                    Stage errorstage = errorStageMethod("No connection",confirmevent);
                    errorstage.show();
                    delete = true;
                }catch (Exception e) {
                    System.out.println("Viga listi eemaldamisel");
                    e.printStackTrace();
                }
                confirmClose.hide();
                clicked.set(true);
                if (!delete) {
                    todolistTab.getTabPane().getTabs().remove(todolistTab);
                }
            });
            cancelButton.setOnMouseClicked(cancelevent -> {
                event.consume();
                confirmClose.hide();
                clicked.set(true);

            });
            if (!clicked.get()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    //Pole õrna aimugi mis siin teha
                    e.printStackTrace();
                }
            }
        });



        return todolistTab;
    }

    private void timeLists(ObservableList<String> hours, ObservableList<String> minutes) {
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
    }

    private BorderPane taskPaneAdder(Task task) {
        BorderPane taskBorderPane = new BorderPane();
        taskBorderPane.setPrefWidth(500);
        taskBorderPane.setPadding(new Insets(5, 5, 5, 5));

        Label headlineLabel = new Label(task.getHeadline());
        headlineLabel.setOnMouseClicked(headEditEvent -> {
            TextField headEditField = new TextField();
            headEditField.setPromptText("Insert new headline");
            headEditField.setOnAction(headEditFieldEditEvent -> {

                String fieldtext = headEditField.getText();
                System.out.println(fieldtext);


                String[] command = {"renametask", String.valueOf(task.getTaskID()), fieldtext};

                boolean delete = false;

                try {
                    commandHandler(command);
                    System.out.println("läks korda");
                }catch (ConnectException ce){
                    Stage errorstage = errorStageMethod("No connection",headEditEvent);
                    errorstage.show();
                    delete = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!delete){
                    headlineLabel.setText(fieldtext);

                    task.setHeadline(fieldtext);
                }

                ((Node) (headEditFieldEditEvent.getSource())).getScene().getWindow().hide();
            });

            Scene headScene = new Scene(headEditField);
            Stage headStage = new Stage();
            headStage.setScene(headScene);

            headStage.initOwner(((Node) (headEditEvent.getSource())).getScene().getWindow());
            headStage.initModality(Modality.WINDOW_MODAL);
            headStage.requestFocus();

            headStage.setAlwaysOnTop(true);
            headStage.show();
        });
        Label duedateLabel = new Label(task.getDeadline());
        duedateLabel.setOnMouseClicked(dateEditEvent -> {

            ObservableList<String> hours = FXCollections.observableArrayList(new ArrayList<>());
            ObservableList<String> minutes = FXCollections.observableArrayList(new ArrayList<>());

            timeLists(hours, minutes);

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
                if (datePicker.getValue() == null) {
                    Stage dateerror = errorStageMethod("Invalid date", dateEditButtonEvent);
                    dateerror.show();
                } else {

                    String[] command = {"dateedit", String.valueOf(task.getTaskID()), duedate};
                    boolean delete=false;

                    try {
                        commandHandler(command);
                        System.out.println("läks korda");
                    } catch(ConnectException ce){
                        Stage errorstage = errorStageMethod("No connection",dateEditEvent);
                        errorstage.show();
                        delete = true;
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!delete){
                        task.setDeadline(duedate);
                        duedateLabel.setText(duedate);
                    }

                    ((Node) (dateEditButtonEvent.getSource())).getScene().getWindow().hide();
                }

            });

            VBox dateTimeEditBox = new VBox();
            dateTimeEditBox.getChildren().addAll(datePicker, timePickerBox, dateEditButton);


            Scene dateScene = new Scene(dateTimeEditBox);
            Stage dateStage = new Stage();

            dateStage.initOwner(((Node) (dateEditEvent.getSource())).getScene().getWindow());
            dateStage.initModality(Modality.WINDOW_MODAL);
            dateStage.requestFocus();

            dateStage.setScene(dateScene);
            dateStage.setAlwaysOnTop(true);

            dateStage.setTitle("New duedate");
            dateStage.show();
        });
        Label descriptionLabel = new Label(task.getDescription());
        descriptionLabel.setOnMouseClicked(descEditEvent -> {
            TextField descEditField = new TextField();
            descEditField.setPromptText("Insert new description");
            descEditField.setOnAction(descEditFieldEditEvent -> {

                String fieldtext = descEditField.getText();

                String[] command = {"descedit", String.valueOf(task.getTaskID()), fieldtext};
                boolean delete= false;

                try {
                    commandHandler(command);
                    System.out.println("läks korda");
                }catch(ConnectException ce){
                    Stage errorstage = errorStageMethod("No connection",descEditEvent);
                    errorstage.show();
                    delete = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!delete){
                    descriptionLabel.setText(fieldtext);

                    task.setDescription(fieldtext);
                }

                ((Node) (descEditFieldEditEvent.getSource())).getScene().getWindow().hide();
            });

            Scene descScene = new Scene(descEditField);
            Stage descStage = new Stage();

            descStage.initOwner(((Node) (descEditEvent.getSource())).getScene().getWindow());
            descStage.initModality(Modality.WINDOW_MODAL);
            descStage.requestFocus();

            descStage.setScene(descScene);
            descStage.setAlwaysOnTop(true);
            descStage.show();
        });

        headlineLabel.setTooltip(new Tooltip("Click to edit headline"));
        duedateLabel.setTooltip(new Tooltip("Click to edit due date"));
        descriptionLabel.setTooltip(new Tooltip("Click to edit description"));


        HBox taskHead = new HBox();
        Region taskHeadRegion = new Region();
        HBox.setHgrow(taskHeadRegion, Priority.ALWAYS);
        taskHead.getChildren().addAll(headlineLabel, taskHeadRegion, duedateLabel);

        Button deleteTaskButton = new Button("Delete");
        deleteTaskButton.setTooltip(new Tooltip("Delete this task")); /////////////////////////////////////////////////////
        deleteTaskButton.setOnAction(editEvent -> {
            Stage confirmClose = new Stage();
            Label label = new Label("Do you want to delete the task?");
            Button yesButton = new Button("Yes");
            Button cancelButton = new Button("Cancel");
            FlowPane pane = new FlowPane(10, 10);
            pane.getChildren().addAll(yesButton, cancelButton);
            VBox vBox = new VBox(10);
            vBox.getChildren().addAll(label, pane);
            Scene confirmscene = new Scene(vBox);
            confirmClose.setScene(confirmscene);
            confirmClose.show();
            AtomicBoolean clicked = new AtomicBoolean(false);
            yesButton.setOnMouseClicked(confirmevent -> {

                String[] command = {"deletetask", String.valueOf(task.getTaskID())};

                try {
                    commandHandler(command);
                    System.out.println("läks korda");
                    taskBorderPane.setManaged(false);
                    taskBorderPane.setVisible(false);

                }catch(ConnectException ce){
                    Stage errorstage = errorStageMethod("No connection",editEvent);
                    errorstage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                confirmClose.hide();
                clicked.set(true);
            });
            cancelButton.setOnMouseClicked(cancelevent -> {
                editEvent.consume();
                confirmClose.hide();
                clicked.set(true);

            });
            if (!clicked.get()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    //Pole õrna aimugi mis siin teha
                    e.printStackTrace();
                }
            }

        });
        CheckBox doneCheckBox = new CheckBox("Done");
        doneCheckBox.setSelected(task.getDone());
        doneCheckBox.setOnAction(doneActionEvent -> {
            if (doneCheckBox.isSelected()) {
                String[] command = {"done", String.valueOf(task.getTaskID())};

                try {
                    commandHandler(command);
                    headlineLabel.setUnderline(true); // TODO STRIKETHROUGH
                    System.out.println("läks korda");
                }catch(ConnectException ce){
                    Stage errorstage = errorStageMethod("No connection",doneActionEvent);
                    errorstage.show();
                    doneCheckBox.setSelected(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!doneCheckBox.isSelected()) {
                String[] command = {"undone", String.valueOf(task.getTaskID())};

                try {
                    commandHandler(command);
                    headlineLabel.setUnderline(false);
                    System.out.println("läks korda");
                }catch(ConnectException ce){
                    Stage errorstage = errorStageMethod("No connection",doneActionEvent);
                    errorstage.show();
                    doneCheckBox.setSelected(true);
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

        taskBorderPane.setOnMouseEntered(event -> taskBorderPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(10), Insets.EMPTY))));
        taskBorderPane.setOnMouseExited(event -> taskBorderPane.setBackground(new Background(new BackgroundFill(Color.SNOW, CornerRadii.EMPTY, Insets.EMPTY))));

        return taskBorderPane;
    }

    private static Object commandHandler(String[] command) throws Exception {

        System.out.println("connecting to server: " + server);
        Object o = null;
        int noOfTries = 0;
        //Stage errorStage = new ErrorStage().getError();
        while (noOfTries < 3) {
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
                        o = TypeId.EMPTY;
                        break;
                    case TypeId.INT:
                        int receivedInt = in.readInt();
                        o = receivedInt;
                        break;
                    case TypeId.ERROR:
                        System.out.println("Midagi läks valesti");
                        o = TypeId.ERROR;
                        // errorit näidatakse vastavalt kohale
                        break;
                    default:
                        System.out.println("ei tule siit midagi");
                }

                break;
            } catch (ConnectException e) {
                //e.printStackTrace();
                noOfTries += 1;
                if (noOfTries == 3) {
                    throw new ConnectException("Serveriga ei õnnestunud ühendada");
                }

                System.out.println("Ühendus puudub, proovin uuesti");
                //errorStage.show();
            }
        }


        System.out.println("cleaned up");
        return o;


    }

    private Stage errorStageMethod(String errorMessage, Event errorTriggerEvent) {
        Stage errorStage = new Stage();
        errorStage.setScene(new Scene(new Label(errorMessage)));
        errorStage.initOwner(((Node) (errorTriggerEvent.getSource())).getScene().getWindow());
        errorStage.initModality(Modality.WINDOW_MODAL);
        errorStage.requestFocus();

        return errorStage;
    }

    public void addTaskButtonEventMethod(ActionEvent addTaskButtonEvent) {
        // Todo, pole prioriteet, aga kunagi võib teha uue classi, mille objektid on tabid, millel on omad tunnused(todoID) ja värki
        // siis saab selle eraldi meetodiks tõsta
    }
}            // TODO tuleb kontrollida ühendust serveriga, muidu viskab errorisse
