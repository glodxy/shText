package classes.sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import classes.thread.OnFinishListener;
import classes.thread.RegistThread;
import classes.util.DialogBuilder;

public class RegistController {
    @FXML private JFXTextField act,uname;
    @FXML private JFXPasswordField psw,pswRe;

    @FXML protected void onRegistClicked(ActionEvent event){
        String u=uname.getText().trim();
        String a=act.getText().trim();
        String p=psw.getText();
        String pr=pswRe.getText();
        if(pr.isEmpty()||u.isEmpty()||a.isEmpty()||p.isEmpty()){
            new DialogBuilder(act).setTitle("错误").setMessage("不能有项为空").setNegativeBtn("确认").create();
        }
        else{
            if(p.equals(pr)){
                RegistThread thread=new RegistThread(a, p,u, new OnFinishListener() {
                    @Override
                    public void onSuccess(Object result) {
                        new DialogBuilder(act).setTitle("成功").setMessage("登录成功").setNegativeBtn("确定").create();
                    }

                    @Override
                    public void onFailed(String reason) {
                        new DialogBuilder(act).setTitle("错误").setMessage(reason).setNegativeBtn("确定").create();
                    }
                });
                thread.run();
            }else{
                new DialogBuilder(act).setTitle("错误").setMessage("两次密码输入不相同").setNegativeBtn("确认").create();
            }
        }
    }

    @FXML protected void onLoginClicked(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../../resources/fxml/login.fxml"));
            Stage stage = (Stage) psw.getScene().getWindow();
            stage.setTitle("登录");
            stage.setScene(new Scene(root, 600, 400));
            stage.setResizable(false);
            stage.show();
        }catch (Exception e){
            System.out.println("切换失败");
        }
    }
}
