package classes.sample;

import com.jfoenix.controls.*;
import classes.domain.Data;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import classes.thread.LoginThread;
import classes.thread.OnFinishListener;
import classes.util.DialogBuilder;


public class Controller {
    @FXML public JFXTextField act;
    @FXML private JFXPasswordField psw;
    @FXML private JFXButton rb,lb;


    @FXML protected void onLoginClicked(ActionEvent event){
        String a=act.getText().trim();
        String p=psw.getText();

        if(a.isEmpty()||p.isEmpty()){
            new DialogBuilder(act).setTitle("错误").setMessage("账号密码不能为空").setNegativeBtn("确定").create();
        }else {
            changeState(true);
            LoginThread thread=new LoginThread(a, p, new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
//                    timeline.stop();
                    changeState(false);
                    new DialogBuilder(act).setTitle("成功").setMessage("登录成功").setNegativeBtn("确定").create();
                    Data.uid=(int)result;
                    Data.update();
                    changeWindow(new MainWindow());
                }

                @Override
                public void onFailed(String reason) {
//                    timeline.stop();
                    changeState(false);
                    new DialogBuilder(act).setTitle("错误").setMessage(reason).setNegativeBtn("确定").create();
                }
            });
            thread.run();
        }
    }

    private void changeState(boolean a){
//        jfxBar.setVisible(a);
//        jfxBarInf.setVisible(a);
        act.setDisable(a);
        psw.setDisable(a);
        rb.setDisable(a);
        lb.setDisable(a);
    }

    private void changeWindow(Application a){
        try {
            Stage stage = new Stage();
            a.start(stage);
            Stage login=(Stage)act.getScene().getWindow();
            login.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML protected void onRegistClicked(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../../resources/fxml/regist.fxml"));
            Stage stage = (Stage) psw.getScene().getWindow();
            stage.setTitle("注册");
            stage.setScene(new Scene(root, 600, 400));
            stage.setResizable(false);
            stage.show();
        }catch (Exception e){
            System.out.println("切换失败");
            e.printStackTrace();
        }
    }



}
