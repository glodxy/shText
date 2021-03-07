package classes.sample;

import classes.domain.Data;
import classes.domain.TUser;
import classes.domain.UserCursor;
import classes.net.ChapterSocketClient;
import classes.net.JsonSender;
import classes.thread.OnFinishListener;
import classes.thread.QueryUserPermissionThread;
import com.jfoenix.utils.JFXHighlighter;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChapterWindow extends Application {
    private int cid,bid;

    private final static String url="ws://localhost:8080/chapter/update_content";

    ChapterSocketClient client;
    ChapterController controller;

    JFXHighlighter highLighter=new JFXHighlighter();

    private QueryUserPermissionThread quThread=null;



    public ChapterWindow(int c,int b){
        super();
        cid=c;
        bid=b;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../resources/fxml/chapter.fxml"));
        Parent root = loader.load();
        controller=loader.getController();
        stage.setTitle("章节编辑");
        stage.setScene(new Scene(root, 800,600));
        stage.show();
        Map<String,String> head=new HashMap<>();
        head.put("Cookie", JsonSender.getSession());

        if(quThread==null){
            quThread=new QueryUserPermissionThread(Data.getBookByBid(bid), new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    List<TUser> p=(List<TUser>) result;
                    quThread=null;
                    for(TUser u:p){
                        if(u.getUid()==Data.uid&&u.getPermission()>1){
                            controller.initTextarea();
                            break;
                        }
                    }
                }

                @Override
                public void onFailed(String reason) {
                    quThread=null;
                }
            });
            quThread.start();
        }
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if(client.getReadyState().equals(ReadyState.OPEN)) {
                    client.end();
                    client.close();
                }
            }
        });
        client=new ChapterSocketClient(new URI(url), head, cid, new ChapterSocketClient.SocketListener() {
            @Override
            public void onMessageUpdated(StringBuffer newText, List<UserCursor> cursorPosition) {
                controller.onTextUpdated(newText,cursorPosition);
            }

            @Override
            public void onConnectionStart(StringBuffer text) {
                controller.onConnectionStart(text);
            }

            @Override
            public void onOpen() {
                controller.onConnectionOpen();
            }

            @Override
            public void onClose() {
                controller.onConnectionClose();
            }

            @Override
            public void onError(String reason) {
                controller.onConnectionError(reason);
            }
        });

        client.addUpdateListener(new ChapterSocketClient.UpdateListener() {
            @Override
            public void beforeUpdate() {
                controller.beforeUpdate();
            }

            @Override
            public void afterUpdate() {

            }
        });

        controller.setHighLighter(highLighter);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

                client.connect();
                while(client.getConnection().getReadyState()!= ReadyState.OPEN){
                    try{
                        Thread.sleep(1);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                client.start();
                controller.setSocketClient(client);
            }
        });
        thread.start();

    }




    @Override
    public void stop() throws Exception {

        super.stop();
    }
}
