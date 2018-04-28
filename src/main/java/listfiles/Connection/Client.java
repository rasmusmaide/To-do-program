package listfiles.Connection;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import listfiles.Task;
import listfiles.dataBaseCommands;
import org.h2.tools.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static javafx.application.Application.launch;

public class Client extends Application{

    static int server = 1337;



    public static void main(String[] args) throws Exception {

        String[] command = {"show list", "3", "heading", "text"};

        launch(args);

        //  commandHandler(command);

    }



    public void start(Stage primaryStage) throws Exception {




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

                Button addtask = new Button("Add");
                addtask.setOnAction(event1 -> {

                    String[] command = {"add", "2005-01-12 08:02:00", headlinefield.getText(), descriptionfield.getText()};

                    try {
                        commandHandler(command);
                        System.out.println("läks korda");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // TODO duedatel on vaja kellaaega, hetkel asendatud entrydatega



                        //dbc.addTask(ntask); // TODO ei tööta, hetkel ei jõudnud vaadata, miks.



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
//        tab1.setText(dbc.getAllTasks().getDescription());
//
//        List<Task> tasks = dbc.getAllTasks().getTasks();
//        ObservableList<Task> observableList = FXCollections.observableArrayList(tasks);
//        ListView<Task> taskView = new ListView<>(observableList);
//        tab1.setContent(taskView);

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

                //dbc.conn.close();


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




    public static void commandHandler(String[] Command) throws Exception {



        //formaat: 2005-01-12 08:02:00;juust;kapsas
        System.out.println("connecting to server: " + server);

        try (
                Socket socket = new Socket("localhost", server);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("connected; sending data");


            out.writeInt(Command.length);

            for (int i = 0; i < Command.length; i++) {
                out.writeUTF(Command[i]);
                System.out.println("sent " + Command[i]);
            }


            System.out.println("cleaned up");
        }


    }
}