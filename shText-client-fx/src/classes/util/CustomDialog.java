package classes.util;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CustomDialog {
    private String title, message;
    private JFXButton negativeBtn = null;
    private JFXButton positiveBtn = null;
    private Window window;
    private JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
    private Paint negativeBtnPaint = Paint.valueOf("#747474");//否定按钮文字颜色，默认灰色
    private Paint positiveBtnPaint = Paint.valueOf("#0099ff");
    private JFXAlert alert;

    onFinishListener l;

    public CustomDialog(String title,Node node,onFinishListener listener){
        l=listener;
        jfxDialogLayout=new JFXDialogLayout();
        jfxDialogLayout.setHeading(new Label(title));
        jfxDialogLayout.setBody(new VBox(node));
        positiveBtn = new JFXButton("确认");
        positiveBtn.setDefaultButton(true);
        positiveBtn.setTextFill(positiveBtnPaint);
        positiveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                l.onConfirm(node);
                alert.close();
            }
        });
        negativeBtn = new JFXButton("取消");
        negativeBtn.setCancelButton(true);
        negativeBtn.setTextFill(negativeBtnPaint);
        negativeBtn.setButtonType(JFXButton.ButtonType.FLAT);
        negativeBtn.setOnAction(event -> {l.onCancel();alert.close();});
    }

    public JFXAlert create(){
        alert = new JFXAlert((Stage)(window));
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);

        jfxDialogLayout.setActions(negativeBtn,positiveBtn);
        alert.setContent(jfxDialogLayout);
        alert.showAndWait();

        return alert;
    }


    public interface onFinishListener{
        public void onConfirm(Node node);
        public void onCancel();
    }
}
