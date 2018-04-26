package listfiles;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.h2.tools.Server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TodoApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //primaryStage.initStyle(StageStyle.UNDECORATED);

        Server server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();

        Class.forName("org.h2.Driver");
        dataBaseCommands dbc;
        //Scanner scanner = new Scanner(System.in);
        //*System.out.println("Enter database username: ");
        //String username = scanner.nextLine();
        //System.out.println("Enter database password: ");
        //String password = scanner.nextLine();*/

        dbc = new dataBaseCommands("jdbc:h2:tcp://localhost/~/todoBase");//, username, password);
        //dbc.initialize(); //esmakordsel käivitamisel

        System.out.println(server.getURL());
        System.out.println(server.getPort());

        BorderPane borderPane = new BorderPane();


        //////////////////////////////////////////////////////////////////////////////////////////////////////////

        //ImageView addim = new ImageView(new Image(new File("fileplus.png").toURI().toString()));
        Button add = new Button("Add task");
        //addim.setFitWidth(45);
        //addim.setFitHeight(45);
        //add.setGraphic(addim);
        //add.setStyle("-fx-background-color: transparent");
        add.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                //try {
                Stage addstage = new Stage();
                addstage.setTitle("New task");

                Label headlabel = new Label("Title");
                TextField headlinefield = new TextField();
                Label desclabel = new Label("Description");
                TextField descriptionfield = new TextField();

                DatePicker datePicker = new DatePicker();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //String duedate = df.format();
                Date currentdate = new Date();
                //String currentDateString =
                String creationdate = df.format(currentdate);


                Button addtask = new Button("Add");
                addtask.setOnAction(event1 -> {

                    Task ntask = new Task(creationdate, "2005-01-12 08:02:00", headlinefield.getText(), descriptionfield.getText(), false); // TODO duedatel on vaja kellaaega, hetkel asendatud entrydatega

                    try {

                        dbc.addTask(ntask); // TODO ei tööta, hetkel ei jõudnud vaadata, miks.
                        System.out.println("jep");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }); // ADD
                Button canceladd = new Button("Cancel");
                canceladd.setOnAction(event1 -> addstage.close()); // CLOSE

                GridPane addtaskpane = new GridPane();
                addtaskpane.add(addtask, 3, 4);
                addtaskpane.add(canceladd, 3, 5);
                addtaskpane.add(headlabel, 0, 0);
                addtaskpane.add(headlinefield, 1, 0);
                addtaskpane.add(desclabel, 0, 1);
                addtaskpane.add(descriptionfield, 1, 1);
                addtaskpane.add(datePicker, 1, 2);


                addstage.setScene(new Scene(addtaskpane, 300, 160));
                addstage.show();

                /*} catch (SQLException e) {
                    throw new RuntimeException(e);
                }*/
            }
        }); // TODO ADDFUNCTION


        Region region2 = new Region();
        VBox.setVgrow(region2, Priority.ALWAYS);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(region2, add);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////

        TabPane tabPane = new TabPane();
        Tab tab1 = new Tab(); // TAB FOR MAIN TO-DO LIST
        tab1.setText(dbc.getAllTasks().getDescription());

        List<Task> tasks = dbc.getAllTasks().getTasks();
        ObservableList<Task> observableList = FXCollections.observableArrayList(tasks);
        ListView<Task> taskView = new ListView<>(observableList);
        tab1.setContent(taskView);

        tab1.setClosable(false);
        tabPane.getTabs().add(tab1);

        Tab nlisttab = new Tab(); // TAB FOR NEW TO-DO LIST
        nlisttab.setText("+");
        nlisttab.setClosable(false);


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
        }); // CREATE NEW TO-DO LIST = TODO new DB

        VBox newtabcontent = new VBox();
        newtabcontent.getChildren().addAll(ntabbutton, ntabfield);
        nlisttab.setContent(newtabcontent);
        tabPane.getTabs().add(nlisttab);
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
            try {
                dbc.conn.close();
            } catch (SQLException r) {
                throw new RuntimeException(r);
            }
            server.stop();
        });

        Scene scene = new Scene(borderPane, 400, 500, Color.SNOW);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drop");
        primaryStage.show();


        /*try {
            System.out.println();

        } finally {
            dbc.conn.close();
            server.stop();
        }*/


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
