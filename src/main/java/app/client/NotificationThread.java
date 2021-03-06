package app.client;

import app.Task;
import app.TodoList;
import app.TypeId;
import app.UserTodoLists;
import com.google.gson.Gson;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotificationThread extends Thread {
    private int server;
    private int userID;
    private boolean isClosed = false;

    public NotificationThread(int server, int userID) {
        this.server = server;
        this.userID = userID;
    }

    @Override
    public void run() {
        while (isClosed == false) {



            //int userID = 1;
            List<TodoList> allUserTodoLists = new ArrayList<>();
            String[] getListsCommand = {"get lists", String.valueOf(userID)};
            try {
                allUserTodoLists = (List<TodoList>) commandHandler(getListsCommand);
                System.out.println("got lists? @NotificationThread");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println(allUserTodoLists);

            for (TodoList taskList : allUserTodoLists) {
                //System.out.println(taskList.getTasks());
                for (Task task : taskList.getTasks()) {

                    if (task.getDone() == false) {
                        //System.out.println(task);
                        //System.out.println(task.getDeadline().getClass().getName());


                        // Convert input string into a date
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date date = null;
                        Date minusDay = null;


                        //String toDayString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(Calendar.getInstance().getTime());
                        String minusOneDay = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(minusOneDay));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.DATE, 1);  // number of days to add
                        minusOneDay = sdf.format(c.getTime());  // dt is now the new date


                        try {
                            date = inputFormat.parse(task.getDeadline());
                            minusDay = inputFormat.parse(minusOneDay);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

// Format date into output format
                        //System.out.println(date);
                        //System.out.println(minusDay);

                        if (date.compareTo(minusDay) == 0) {
                            System.out.println("JAPP");
                            //isClosed = true;
//                            try {
//
//                                final Popup popup = createPopup(new Task("2018-05-20 03:22:50.0", "2018-05-20 03:22:50.0", "pealkiri", "sisuasdjfklsdjklasdfjklesdrtfyguhkijloökgydfsghjasdjfklasdjklfasdöljfasdölkjfasdöklfjasdklöfjasdklfjasdklöfjasdkölasdfkljasdfkljfasdökljdfkladsöl", false));
//                                final HBox layout = new HBox(10);
//                                popup.show(layout, 0, 0);
//
//
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }





                        }


                    }
                }
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("on kinni");

    }

    private Object commandHandler(String[] command) throws Exception {

        //System.out.println("connecting to server: " + server);
        Object o = null;
        int noOfTries = 0;

        while (noOfTries < 3) {
            try (
                    Socket socket = new Socket("localhost", server);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream())

            ) {
                //System.out.println("connected; sending data");
                //errorStage.hide();

                out.writeInt(command.length);

                for (int i = 0; i < command.length; i++) {
                    out.writeUTF(command[i]);
                    //System.out.println("sent " + command[i]);
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

                System.out.println("Ühendus puudub, proovin uuesti @notificationThread");
                //errorStage.show();
            }
        }


        //System.out.println("cleaned up");
        return o;


    }

    static Popup createPopup(Task task) throws ParseException {


        final Popup popup = new Popup();
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        Rectangle rectangle = new Rectangle(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 400, primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 150, 400, 150);
        rectangle.setFill(Color.AQUAMARINE);
        popup.getContent().addAll(rectangle);

//ADD////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Label label = new Label("New Task: " + task.getHeadline());

        label.setStyle("-fx-background-color: #738000; -fx-padding: 14px;");

        label.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 400);
        label.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 150);

        popup.getContent().addAll(label);

        ///Label2/////

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date dueDate = null;
        Date creationDate = null;

        dueDate = inputFormat.parse(task.getDeadline());
        creationDate = inputFormat.parse(task.getCreationDate());

        String dueDateString = inputFormat.format(dueDate);
        String creationDateString = inputFormat.format(creationDate);

        Label label2 = new Label("Due Date: " + dueDateString + "    " + "Creation Date: " + creationDateString);

        label2.setStyle("-fx-background-color: #738000; -fx-padding: 14px;");

        label2.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 400);
        label2.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 120);

        popup.getContent().addAll(label2);

///Label3////////////


        Label label3 = null;

        if (task.getDescription().length() > 60) {

            StringBuilder lineOne = new StringBuilder();

            String[] toPieces = task.getDescription().split("(?!^)");

            for (int i = 0; i < 60; i++) {
                lineOne.append(toPieces[i]);
            }

            label3 = new Label("Description: " + lineOne);

            Label label4 = null;

            StringBuilder lineTwo = new StringBuilder();

            if (task.getDescription().length() > 130) {


                for (int i = 61; i < 129; i++) {
                    lineTwo.append(toPieces[i]);
                }

                label4 = new Label("--> " + lineTwo + "...");


            } else {

                for (int i = 61; i < task.getDescription().length(); i++) {
                    lineTwo.append(toPieces[i]);
                }

                label4 = new Label("--> " + lineTwo);
            }

            label4.setStyle("-fx-background-color: #738000; -fx-padding: 14px;");

            label4.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 400);
            label4.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 60);


            popup.getContent().addAll(label4);


        } else {
            label3 = new Label("Description: " + task.getDescription());
        }


        label3.setStyle("-fx-background-color: #738000; -fx-padding: 14px;");

        label3.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 400);
        label3.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 90);

        popup.getContent().addAll(label3);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Button closePopup = new Button();
        closePopup.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 33);
        closePopup.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 149);


        Image image = new Image("fileclose.png", 20.0, 20.0, true, true);
        closePopup.setGraphic(new ImageView(image));

        closePopup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                popup.hide();
            }
        });


        popup.getContent().addAll(closePopup);


        PauseTransition pause = new PauseTransition(Duration.seconds(30));
        pause.setOnFinished(e -> popup.hide());
        pause.play();


        return popup;
    }


}


