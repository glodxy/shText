package classes.sample;

import classes.domain.Data;
import classes.domain.Operation;
import classes.domain.UserCursor;
import classes.net.ChapterSocketClient;
import classes.thread.QueryUserPermissionThread;
import classes.util.DialogBuilder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.utils.JFXHighlighter;
import com.jfoenix.utils.JFXNodeUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChapterController implements Initializable {
    private ChapterSocketClient client=null;

    int lastSize=0;

    @FXML private JFXTextArea textArea;
    @FXML private JFXColorPicker colorPicker;
    @FXML private Label cstate;
    @FXML private Label charCount;
    @FXML private TextField searchField;
    @FXML private JFXButton searchButton;

    private JFXHighlighter highlighter;



    //记录更新前的滚屏位置
    double lastScrollV=0;

    public void setHighLighter(JFXHighlighter h){
        highlighter=h;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        textArea.setOnInputMethodTextChanged(new EventHandler<InputMethodEvent>() {
//            @Override
//            public void handle(InputMethodEvent inputMethodEvent) {
//
//            }
//        });
        textArea.setDisable(true);

        colorPicker.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observableValue, Color color, Color t1) {
                textArea.setStyle("-fx-text-fill: #"+t1.toString().replace("0x",""));
            }
        });

        JFXDepthManager.setDepth(textArea,1);

        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                highlighter.highlight(textArea,searchField.getText());
                            }
                        });

                    }
                });
            }
        });

    }


    //只可在其他线程调用
    public void initTextarea(){
        while(client==null){
            try{
                Thread.sleep(1);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        client.setSendOperationDisabled(false);
        textArea.caretPositionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                client.move(t1.intValue());
            }
        });

        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(!client.content().equals(t1)) {
                    charCount.setText("字数:"+t1.length());
                    Operation opt = new Operation();
                    opt.setUid(Data.uid);
                    if (t1.length() > client.content().length() && t1.contains(client.content())) {
                        //尾部修改
                        if(t1.indexOf(client.content())==0){
                            opt.setPosition(client.content().length());
                        }
                        else{
                            opt.setPosition(0);
                        }
                        opt.setOpt(1);
                        opt.setOpc(t1.replace(client.content(),""));
                        client.addOperation(opt);
                        //System.out.println("add:"+opt.getOpc());
                    } else if (t1.length() < client.content().length() && client.content().contains(t1)) {
                        opt.setOpt(2);
                        opt.setOpc(client.content().replace(t1,""));
                        if(client.content().indexOf(t1)==0){
                            opt.setPosition(client.content().length());
                        }
                        else{
                            opt.setPosition(opt.getOpc().length());
                        }
                        client.addOperation(opt);
                        //System.out.println("delete:"+opt.getOpc());
                    } else {
                        List<Operation> opts = stringCompare(client.content(), t1);
                        for (int i=0;i<opts.size();++i) {
                            client.addOperation(opts.get(i));
                        }
                    }
//                    System.out.println("all text:"+observableValue+"##"+s+"##"+t1);
//                    System.out.println("cursor:"+textArea.getCaretPosition()+",length:"+textArea.getText().length());
                }
            }
        });
        textArea.setDisable(false);
    }


    public List<Operation> stringCompare(String s,String t){
        int startIdx=0;
        for(;s.charAt(startIdx)==t.charAt(startIdx);++startIdx);

        boolean flag=false;
        int endIdxS=s.length()-1,endIdxT=t.length()-1;
        for(;s.charAt(endIdxS)==t.charAt(endIdxT);){
            if(endIdxS==startIdx||endIdxT==startIdx){
                flag=true;
                break;
            }
            --endIdxS;
            --endIdxT;
        }

        List<Operation> opts = new ArrayList<>();
        if(endIdxS!=startIdx) {
            Operation delete = new Operation();
            delete.setUid(Data.uid);
            delete.setOpt(2);
            delete.setOpc(s.substring(startIdx, flag?endIdxS:endIdxS + 1));
            delete.setPosition(flag?endIdxS:endIdxS + 1);
            //System.out.println("delete:"+delete.getOpc());
            opts.add(delete);
        }

        if(endIdxT!=startIdx) {
            Operation add = new Operation();
            add.setUid(Data.uid);
            add.setOpt(1);
            add.setOpc(t.substring(startIdx, flag?endIdxT:endIdxT + 1));
            add.setPosition(startIdx);
            //System.out.println("add:"+add.getOpc());
            opts.add(add);
        }


        return opts;
    }


    public void onConnectionStart(StringBuffer sb){
        textArea.setText(sb.toString());
        textArea.positionCaret(sb.length());
        lastSize=textArea.getText().length();
    }

    public void onTextUpdated(StringBuffer sb, List<UserCursor>ucl){
        textArea.setText(sb.toString());

        for(UserCursor uc:ucl){
            if(uc.getUid()== Data.uid){
                textArea.positionCaret(uc.getCposition());
                break;
            }
        }
        textArea.setScrollTop(lastScrollV);


    }

    public void setSocketClient(ChapterSocketClient c){
        client=c;
    }

    public void onConnectionOpen(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cstate.setText("已连接");
                cstate.setTextFill(Paint.valueOf("#8ab839"));
            }
        });

    }

    public void onConnectionClose(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cstate.setText("已断开");
                cstate.setTextFill(Paint.valueOf("#f55064"));
            }
        });

    }

    public void onConnectionError(String reason){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cstate.setText(reason);
                cstate.setTextFill(Paint.valueOf("#ff0000"));
                new DialogBuilder(textArea).setTitle("错误").setMessage("数据传输出错！").setPositiveBtn("确认", new DialogBuilder.OnClickListener() {
                    @Override
                    public void onClick() {
                        Stage stage=(Stage)textArea.getScene().getWindow();
                        stage.close();
                    }
                }).create();
            }
        });

    }

    public void beforeUpdate(){
        lastScrollV=textArea.getScrollTop();
    }
}
