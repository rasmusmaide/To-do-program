package listfiles;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TodoApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //primaryStage.initStyle(StageStyle.UNDECORATED);
/*
        Server server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();

        Class.forName("org.h2.Driver");
        dataBaseCommands dbc;
        //Scanner scanner = new Scanner(System.in);
        //System.out.println("Enter database username: ");
        //String username = scanner.nextLine();
        //System.out.println("Enter database password: ");
        //String password = scanner.nextLine();

        dbc = new dataBaseCommands("jdbc:h2:tcp://localhost/~/todoBase");//, username, password);
        //dbc.initialize(); //esmakordsel käivitamisel

        System.out.println(server.getURL());
        System.out.println(server.getPort());*/

        // To-do lists for testing:

        List<Task> testtasklist = new ArrayList<>(Arrays.asList(
                new Task("2005-01-12 08:02:00", "2005-01-12 08:02:00", "head1", "desc1", false),
                new Task("2005-01-12 08:02:00", "2005-01-12 08:02:00", "head2", "desc2", false)
        ));

        List<Task> testtasklist2 = new ArrayList<>();
        testtasklist2.add(new Task("2005-01-12 08:02:00", "2005-01-12 08:02:00", "head2", "desc2", true));


        Todo_list testtodo1 = new Todo_list(testtasklist, "testtodo1");
        Todo_list testtodo2 = new Todo_list(testtasklist2, "testtodo2");

        List<Todo_list> testlist = new ArrayList<>();
        testlist.add(testtodo1);
        testlist.add(testtodo2);

        BorderPane borderPane = new BorderPane();

        ///// [LISTAREA] /////////////////////////////////////////////////////////////////////////////////////////////////////

        TabPane tabPane = new TabPane();

        Tab nlisttab = new Tab(); // TAB FOR NEW TO-DO LIST TODO added task goes to selected tab/todolist
        nlisttab.setText("+");
        nlisttab.setClosable(false);
        nlisttab.setOnSelectionChanged(t -> {
            if (nlisttab.isSelected()) {
                tabPane.getTabs().add(tabAdder(new Todo_list(new ArrayList<>(), "New To-do list")));
            }
        }); // TODO


        TextField ntabfield = new TextField();

        Button ntabbutton = new Button("Create new to-do list");
        ntabbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                while (ntabfield.getText().equals("")) {
                    ntabfield.setText("Insert list name!");
                }
                newTodoList(ntabfield.getText()); // enter list name

            }

            private void newTodoList(String text) {

            }
        }); // CREATE NEW TO-DO LIST

        VBox newtabcontent = new VBox();
        newtabcontent.getChildren().addAll(ntabbutton, ntabfield);
        nlisttab.setContent(newtabcontent);
        tabPane.getTabs().add(nlisttab);

        ///// [SIDEBAR} ////////////////////////////////////////////////////////////////////////////////////////////

        Image imageplus = new Image("fileplus.png");
        ImageView addim = new ImageView(imageplus);
        Button add = new Button();
        addim.setFitWidth(40);
        addim.setFitHeight(40);
        add.setGraphic(addim);
        add.setStyle("-fx-background-color: transparent");

        add.setOnAction(event -> {

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

            //duedateHoursSpinner.maxWidth(20); // ei tööta TODO uuri miks
            //duedateHoursSpinner.maxHeight(20);
            //duedateMinutesSpinner.maxWidth(20);

            timePickerBox.getChildren().addAll(duedateHoursSpinner, timeSeparator, duedateMinutesSpinner);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            DatePicker datePicker = new DatePicker();


            Button addtask = new Button("Add");
            addtask.setOnAction(event1 -> {
                Date currentdate = new Date();
                String creationdate = df.format(currentdate);
                String duedate = datePicker.getValue() + " " + duedateHoursSpinner.getValue() + ":" + duedateMinutesSpinner.getValue() + ":00";

                Task ntask = new Task(creationdate,
                        duedate,
                        headlinefield.getText(),
                        descriptionfield.getText(),
                        false); // Nüüd on duedate ka olemas.

                System.out.println(ntask);
                // saadab Taski serverile.
                // addstage.close(); // kui additud, siis paneb kinni akna

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

        }); // TODO ADDFUNCTION format done, now implementation


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
            for (Todo_list todo_list : testlist) {
                tabPane.getTabs().add(tabAdder(todo_list));
            }
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(region2, new Label("[Sidebar]"), refreshbutton, add);


        //////////////////////////////////////////////////////////////////////////////////////////////////////////


        borderPane.setPadding(new Insets(5, 5, 5, 5)); // BORDERPANE SETUP
        //borderPane.setTop(hBox);
        //borderPane.setBottom(hBox2);
        borderPane.setCenter(tabPane);
        borderPane.setRight(vBox);


        //primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("resources/Drop.png"))); // TODO ükski ei tööta
        //primaryStage.getIcons().add(new Image(getClass().getResource("resources/Drop.png").toExternalForm()));
        //primaryStage.getIcons().add(new Image("/resources/Drop.png"));
        //ImageView programicon = new ImageView(new Image(new File("resources/Drop.png").toURI().toString()));
        //primaryStage.getIcons().add(new Image(new File("resources/Drop.png").toURI().toString()));


        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Bye!");
           /* try {
                dbc.conn.close();
            } catch (SQLException r) {
                throw new RuntimeException(r);
            }
            server.stop();*/
        });

        Scene scene = new Scene(borderPane, 400, 500, Color.SNOW);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drop");
        primaryStage.show();


    }

    private Tab tabAdder(Todo_list todo_list) {
        BorderPane todoTabPane = new BorderPane();
        Tab todolistTab = new Tab();

        todolistTab.setText(todo_list.getDescription());

        List<Task> tasks = todo_list.getTasks();
        ObservableList<Task> observableList = FXCollections.observableArrayList(tasks);
        ListView<Task> taskView = new ListView<>(observableList);

        Button renameTodo = new Button(todo_list.getDescription());
        renameTodo.setOnAction(event -> {
            TextField renamefield = new TextField("Insert new name");
            renamefield.setOnAction(event1 -> {

                String fieldtext = renamefield.getText();
                todolistTab.setText(fieldtext);
                todo_list.setDescription(fieldtext);
                renameTodo.setText(fieldtext);

                //renamelistStage.close(); // ei tööta meetodis?
                ((Node) (event1.getSource())).getScene().getWindow().hide(); // see töötab
                // TODO saadab serverile selle muutuse
            });


            Scene renamelistScene = new Scene(renamefield);
            Stage renamelistStage = new Stage();
            renamelistStage.setTitle("Rename list");
            renamelistStage.setScene(renamelistScene);
            renamelistStage.show();

        });


        todoTabPane.setTop(new HBox(renameTodo));
        todoTabPane.setCenter(taskView);

        todolistTab.setContent(todoTabPane);
        todolistTab.setClosable(false);

        todolistTab.setOnSelectionChanged(t -> {
            if (todolistTab.isSelected()) {
                System.out.println("Add nupp kehtib sellele listile"); // TODO add button affects this list
            }
        });

        return todolistTab; // TODO added task goes to selected tab/todolist
    }
}
/*
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    Label title = new Label("To-do list");
        title.setPadding(new Insets(10,10,10,10));


                ImageView im = new ImageView(new Image(new File("C:\\Users\\Dell\\Desktop\\ \\fileclose.png").toURI().toString()));
                Button exit = new Button(); // EXIT
                im.setFitWidth(25);
                im.setFitHeight(25);
                exit.setGraphic(im);
                exit.setStyle("-fx-background-color: transparent");
                exit.setOnAction(new EventHandler<ActionEvent>() {
@Override
public void handle(ActionEvent event) {
        Platform.exit();
        }
        });

        ImageView im2 = new ImageView(new Image(new File("C:\\Users\\Dell\\Desktop\\ \\fileminimize.png").toURI().toString()));
        Button minimize = new Button(); // MINIMIZE
        im2.setFitWidth(20);
        im2.setFitHeight(20);
        minimize.setGraphic(im2);
        minimize.setStyle("-fx-background-color: transparent");
        minimize.setOnAction(new EventHandler<ActionEvent>() {
@Override
public void handle(ActionEvent event) {
        primaryStage.setIconified(true);

        }
        });

        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);


        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: gray;");
        hBox.getChildren().addAll(title, region1, minimize, exit);

                ImageView saveim = new ImageView(new Image(new File("C:\\Users\\Dell\\Desktop\\ \\filesave.png").toURI().toString()));
        Button save = new Button(); // SAVE
        saveim.setFitWidth(20);
        saveim.setFitHeight(20);
        save.setGraphic(saveim);
        save.setStyle("-fx-background-color: transparent");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // SAVETEXT

            }
        }); ////////////SAVEFUNCTION



        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(save);

//////////////////////////////////////////////////////////////////////////////////////////////////////////*/
