package listfiles;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.h2.tools.Server;

import java.io.File;
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

        Server server = Server.createTcpServer("-tcpPort","9092","-tcpAllowOthers").start();

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

        ImageView addim = new ImageView(new Image(new File("C:\\Users\\Dell\\Desktop\\ \\fileplus.png").toURI().toString()));
        Button add = new Button();
        addim.setFitWidth(45);
        addim.setFitHeight(45);
        add.setGraphic(addim);
        add.setStyle("-fx-background-color: transparent");
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // ADDFUNCTION

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
                //newTodoList(ntabfield.getText());

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


        Scene scene = new Scene(borderPane, 400, 500, Color.SNOW);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drop");
        primaryStage.show();









        try {
            System.out.println();

        } finally {
            dbc.conn.close();
            server.stop();
        }


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
