package classes.util;

import classes.domain.Chapter;
import classes.domain.Data;
import classes.domain.TUser;
import classes.sample.MainController;
import classes.thread.*;
import com.jfoenix.controls.*;
import classes.domain.Book;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.utils.JFXHighlighter;
import com.jfoenix.validation.NumberValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

/*
* TO-DO:
* 1.完成权限的更新与删除
* 2.完成权限的添加
* 3.完成书本的刷新
* 4.完成书本的添加
* 5.完成章节的添加
*
* */

public class BookTab extends Tab {
    private Book book;
    private List<TUser> ul=new ArrayList<>();
    private JFXScrollPane rootPane;
    private TextField titleField;
    private JFXButton saveButton;
    private JFXMasonryPane masonryPane;
    private JFXListView<Label> list;

    private QueryUserPermissionThread thread=null;
    private QueryChapterThread qcthread=null;

    private UpdatePermissionThread upthread=null;
    private AddChapterThread addChapterThread=null;
    private DeleteChapterThread deleteChapterThread=null;
    private UpdateChapterThread updateChapterThread=null;

    private JFXHighlighter lighter=new JFXHighlighter();



    private Map<Label,TUser> userMap=new HashMap<>();
    private MainController controller=null;


    public BookTab(Book b,MainController c){
        super();
        controller=c;
        book=b;
        setText(book.getBtitle());
        initPane();
        setContent(rootPane);
        setClosable(true);
    }


    private void initPane(){
        try {
            rootPane = (JFXScrollPane) FXMLLoader.load(getClass().getResource("../../resources/fxml/bookInfo.fxml"));
            initMenu();
            //初始化底栏
            initBottomBar();

            initMidBar();

            initTopBar();


            initContent();


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void initMenu(){

    }


    private void initMidBar(){
        masonryPane=new JFXMasonryPane();
        masonryPane.setLayoutMode(JFXMasonryPane.LayoutMode.BIN_PACKING);
        masonryPane.setCenterShape(true);
        masonryPane.setCellHeight(30);
        masonryPane.setCellWidth(60);
        masonryPane.setHSpacing(10);
        masonryPane.setVSpacing(5);
        if(thread==null){
            thread=new QueryUserPermissionThread(book, new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    System.out.println("in");
                    ul=(List<TUser>)result;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for(TUser user:ul){
                                Label label=new Label(user.getUname());
                                initLabel(label,user);
                            }
                            Label add=new Label();
                            add.setPrefSize(60,30);
                            Image image;
                            image = new Image(getClass().getResourceAsStream("../../resources/img/add.png"),20,20,false,false);
                            add.setGraphic(new ImageView(image));
                            add.setStyle("-fx-background-color: SNOW;-fx-background-radius: 20;-fx-border-radius: 20;-fx-border-color:#f55064aa;-fx-border-width: 2;-fx-alignment: center");
                            add.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    startForAdd(book.getBid());
                                }
                            });
                            JFXDepthManager.setDepth(add,1);
                            masonryPane.getChildren().add(add);
                        }
                    });
                }

                @Override
                public void onFailed(String reason) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            new DialogBuilder(titleField).setTitle("错误").setMessage(reason).setNegativeBtn("确认").create();
                        }
                    });
                }
            });
            thread.start();
        }
        rootPane.getMidBar().getChildren().add(masonryPane);
        StackPane.setAlignment(masonryPane,Pos.TOP_LEFT);
        StackPane.setMargin(masonryPane,new Insets(1,2,2,2));
    }

    private void initBottomBar(){
        titleField=new TextField(book.getBtitle());
        titleField.setPrefHeight(30);
        titleField.setStyle("-fx-max-width: 300;-fx-text-fill: DARKGRAY;-fx-border-radius: 20;-fx-background-radius: 20;-fx-font-size: 13pt");
        saveButton=new JFXButton("搜索");
        saveButton.setButtonType(JFXButton.ButtonType.RAISED);
        saveButton.setPrefSize(80,40);
        saveButton.setStyle("-fx-border-radius: 20;-fx-background-radius: 20;-fx-background-color: #f55064;-fx-text-fill: #fffdf8;");
        saveButton.setRipplerFill(null);

        rootPane.getBottomBar().getChildren().add(titleField);
        rootPane.getBottomBar().getChildren().add(saveButton);

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lighter.highlight(list,titleField.getText());
            }
        });
        StackPane.setMargin(titleField,new Insets(1,0,1,50));
        StackPane.setMargin(saveButton,new Insets(1,20,1,1));
        StackPane.setAlignment(saveButton,Pos.CENTER_RIGHT);
        StackPane.setAlignment(titleField, Pos.CENTER_LEFT);
    }

    private void initTopBar(){
        Label icon=new Label();
        Image image;
        image = new Image(getClass().getResourceAsStream("../../resources/img/book.png"),20,20,false,false);
        icon.setGraphic(new ImageView(image));
        icon.setStyle("-fx-alignment: center;-fx-background-color:#262a3b88;-fx-border-radius: 50;-fx-background-radius: 50;");
        icon.setPrefSize(30,30);
        rootPane.getTopBar().getChildren().add(icon);
        StackPane.setAlignment(icon,Pos.CENTER_LEFT);
        StackPane.setMargin(icon,new Insets(1,2,1,3));
    }


    private void startForDeleteChapter(Label label){
        int cid=(int)label.getUserData();
        new DialogBuilder(titleField).setTitle("提示").setMessage("是否确认删除？").setPositiveBtn("确认", new DialogBuilder.OnClickListener() {
            @Override
            public void onClick() {
                toggledDeleteChapter(cid);
            }
        }).setNegativeBtn("取消").create();
    }

    private void startForUpdateChapter(Label label){
        int cid=(int)label.getUserData();
        HBox box=new HBox();
        JFXTextField title=new JFXTextField(label.getText().substring(label.getText().indexOf(".")).substring(1));
        title.setPromptText("章节名");
        title.setPickOnBounds(true);
        title.setLabelFloat(true);
        JFXTextField seq=new JFXTextField(label.getText().substring(0,label.getText().indexOf(".")));
        seq.setPromptText("章节号");
        seq.setLabelFloat(true);
        seq.setValidators(new NumberValidator());

        box.getChildren().addAll(title,seq);

        HBox.setMargin(title,new Insets(1,5,1,5));
        HBox.setMargin(seq,new Insets(1,2,1,2));
        CustomDialog dialog=new CustomDialog("更新章节", box, new CustomDialog.onFinishListener() {
            @Override
            public void onConfirm(Node node) {
                if(title.getText().trim().isEmpty()||seq.getText().trim().isEmpty()){
                    new DialogBuilder(saveButton).setTitle("错误").setMessage("不能有项为空").setNegativeBtn("确认").create();
                }
                else{
                    toggledUpdateChapter(cid,title.getText().trim(),Integer.valueOf(seq.getText()),book.getBid());
                }
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.create();
    }

    private void startForAddChapter(){
        HBox box=new HBox();
        JFXTextField title=new JFXTextField();
        title.setPromptText("章节名");
        title.setPickOnBounds(true);
        title.setLabelFloat(true);
        JFXTextField seq=new JFXTextField();
        seq.setPromptText("章节号");
        seq.setLabelFloat(true);
        seq.setValidators(new NumberValidator());

        box.getChildren().addAll(title,seq);

        HBox.setMargin(title,new Insets(1,5,1,5));
        HBox.setMargin(seq,new Insets(1,2,1,2));
        CustomDialog dialog=new CustomDialog("添加章节", box, new CustomDialog.onFinishListener() {
            @Override
            public void onConfirm(Node node) {
                if(title.getText().trim().isEmpty()||seq.getText().trim().isEmpty()){
                    new DialogBuilder(saveButton).setTitle("错误").setMessage("不能有项为空").setNegativeBtn("确认").create();
                }
                else{
                    toggledAddChapter(title.getText().trim(),book.getBid(),Integer.valueOf(seq.getText()));
                }
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.create();
    }

    private void toggledUpdateChapter(int cid,String title,int cseq,int bid){
        if(updateChapterThread==null){
            updateChapterThread=new UpdateChapterThread(cid, cseq, bid, title, new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    updateChapterThread=null;
                    Chapter chapter=(Chapter)result;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for(Label l:list.getItems()){
                                if((int)l.getUserData()==cid){
                                    l.setText(chapter.getCseq()+"."+chapter.getCtitle());
                                }
                            }
                            new DialogBuilder(saveButton).setTitle("成功").setMessage("章节更新成功").setNegativeBtn("确认").create();
                        }
                    });
                }

                @Override
                public void onFailed(String reason) {
                    updateChapterThread=null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            new DialogBuilder(saveButton).setTitle("错误").setMessage(reason).setNegativeBtn("确认").create();
                        }
                    });
                }
            });
            updateChapterThread.start();
        }
    }

    private void toggledDeleteChapter(int cid){
        if(deleteChapterThread==null){
            deleteChapterThread=new DeleteChapterThread(cid, new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    deleteChapterThread=null;
                    Chapter chapter=(Chapter)result;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for(Label l:list.getItems()){
                                if((int)l.getUserData()==cid){
                                    list.getItems().remove(l);
                                    break;
                                }
                            }
                            new DialogBuilder(saveButton).setTitle("成功").setMessage("章节删除成功").setNegativeBtn("确认").create();
                        }
                    });
                }

                @Override
                public void onFailed(String reason) {
                    deleteChapterThread=null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            new DialogBuilder(saveButton).setTitle("错误").setMessage(reason).setNegativeBtn("确认").create();
                        }
                    });
                }
            });
            deleteChapterThread.start();
        }
    }

    private void toggledAddChapter(String title,int bid,int cseq){
        if(addChapterThread==null){
            addChapterThread=new AddChapterThread(title, bid, cseq, new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    addChapterThread=null;
                    Chapter chapter=(Chapter)result;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Label label=new Label(chapter.getCseq()+"."+chapter.getCtitle());
                            label.setUserData(chapter.getCid());
                            list.getItems().add(chapter.getCseq()-1,label);
                            new DialogBuilder(saveButton).setTitle("成功").setMessage("章节创建成功").setNegativeBtn("确认").create();
                        }
                    });
                }

                @Override
                public void onFailed(String reason) {
                    addChapterThread=null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            new DialogBuilder(saveButton).setTitle("错误").setMessage(reason).setNegativeBtn("确认").create();
                        }
                    });
                }
            });
            addChapterThread.start();
        }
    }

    private void initContent(){

        ContextMenu emptyMenu=new ContextMenu();
        MenuItem insertItem=new MenuItem("添加");
        insertItem.setOnAction(event -> {
            startForAddChapter();
        });
        emptyMenu.getItems().add(insertItem);
        list=new JFXListView<>();
        //list.setCellFactory(this::CellFactory);
        if(qcthread==null){
            qcthread=new QueryChapterThread(book, new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    List<Chapter> chapters=(List<Chapter>)result;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for(Chapter c:chapters){
                                Label label=new Label(c.getCseq()+"."+c.getCtitle());
                                label.setUserData(c.getCid());

                                list.getItems().add(label);
                            }
                            list.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    Node node=mouseEvent.getPickResult().getIntersectedNode();
                                    if(!(node instanceof ListCell)){
                                        if(mouseEvent.getButton()== MouseButton.SECONDARY)
                                            emptyMenu.show(titleField.getScene().getWindow(),mouseEvent.getScreenX(),mouseEvent.getScreenY());
                                    }
                                    else{
                                        ListCell cell=(ListCell)node;
                                        Label label=(Label)cell.getItem();
                                        if(mouseEvent.getButton()==MouseButton.PRIMARY&&mouseEvent.getClickCount()==2){
                                            startChapterWindow((int)label.getUserData());
                                        }
                                        if(mouseEvent.getButton()==MouseButton.SECONDARY)
                                        {
                                            ContextMenu normalMenu = new ContextMenu();
                                            MenuItem updateItem=new MenuItem("更新");
                                            updateItem.setOnAction(event -> {
                                                startForUpdateChapter(label);
                                            });
                                            MenuItem deleteItem = new MenuItem("删除");
                                            deleteItem.setOnAction(event -> {
                                                startForDeleteChapter(label);
                                            });
                                            normalMenu.getItems().addAll(updateItem, deleteItem);
                                            normalMenu.show(titleField.getScene().getWindow(),mouseEvent.getScreenX(),mouseEvent.getScreenY());
                                        }
                                    }
                                }
                            });
                        }
                    });
                }

                @Override
                public void onFailed(String reason) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            new DialogBuilder(titleField).setTitle("错误").setMessage(reason).setNegativeBtn("确认").create();
                        }
                    });
                }
            });
            qcthread.start();
        }
        list.setMaxHeight(Double.POSITIVE_INFINITY);
        list.setPrefHeight(-1);

        StackPane pane=new StackPane(list);
        pane.setPadding(new Insets(24));

        rootPane.setContent(pane);

    }

    private void startChapterWindow(int c){
        if(controller.userPermissionThread==null){
            controller.userPermissionThread=new QueryUserPermissionThread(book, new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    controller.userPermissionThread=null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(controller.editWindow==null){
                                int bid=book.getBid();
                                int cid=c;
                                controller.initChapterWindow(cid,bid);
                            }
                            //存在窗口时
                            else{
                                try {
                                    controller.editWindow.stop();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                int bid=book.getBid();
                                int cid=c;
                                controller.initChapterWindow(cid,bid);
                            }
                        }
                    });
                }

                @Override
                public void onFailed(String reason) {
                    controller.userPermissionThread=null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            new DialogBuilder(saveButton).setTitle("错误").setMessage(reason).setNegativeBtn("确认").create();
                        }
                    });
                }
            });
            controller.userPermissionThread.start();
        }
    }

//*//初始化label
    private void initLabel(Label label,TUser user){
        label.setPrefSize(60,30);
        if(user.getUid()== Data.uid)
        {
            label.setText("你");
        }//非本人则可进行更新操作
        else{
            //添加右键菜单栏
            ContextMenu menu=new ContextMenu();
            MenuItem delete=new MenuItem("删除");
            delete.setOnAction(event -> {
                startForDelete(book.getBid(),user.getUid());
            });
            MenuItem update=new MenuItem("更新");
            update.setOnAction(event -> {
                startForUpdate(book.getBid(),user.getUid());
            });
            menu.getItems().addAll(delete,update);
            label.setContextMenu(menu);
        }
        setLabelColor(label,user.getPermission());
        JFXDepthManager.setDepth(label,1);
        masonryPane.getChildren().add(0,label);
        userMap.put(label,user);
    }


    private void setLabelColor(Label chip,int permission){
        final Tooltip tt=new Tooltip();
        tt.setShowDelay(Duration.millis(100));
        if(permission==0){
            masonryPane.getChildren().remove(chip);
            userMap.remove(chip);
        }
        if(permission==1){
            tt.setText("访问者");
            chip.setTooltip(tt);
            chip.setStyle("-fx-border-radius:20;-fx-background-radius:20;-fx-background-color: WHITE;-fx-text-fill:#26243b; -fx-alignment: center;");
        }
        else if(permission==2){
            tt.setText("编辑者");
            chip.setTooltip(tt);
            chip.setStyle("-fx-border-radius:20;-fx-background-radius:20;-fx-background-color: #f2572d;-fx-text-fill: SNOW; -fx-alignment: center");
        }
        else{
            tt.setText("管理者");
            chip.setTooltip(tt);
            chip.setStyle("-fx-border-radius:20;-fx-background-radius:20;-fx-background-color: #262a3b;-fx-text-fill: WHITE; -fx-alignment: center");
        }
    }

//*/////////////////////////////////////////////////////////////////用户权限操作
    private void startForDelete(int bid,int uid){
        new DialogBuilder(titleField).setTitle("提示").setMessage("是否确认删除？").setPositiveBtn("确认", new DialogBuilder.OnClickListener() {
            @Override
            public void onClick() {
                updateUserPermission(uid,bid,0);
            }
        }).setNegativeBtn("取消").create();
    }


    private void startForUpdate(int bid,int uid){
        JFXComboBox<String> cb=new JFXComboBox<>();
        cb.getItems().addAll("可查看","可编辑","所有权限");
        int value=1;
        for(TUser user:userMap.values()){
            if(user.getUid()==uid){
                value=user.getPermission();
                break;
            }
        }
        cb.setValue(PermissionConvert.getNameByPermission(value));
        CustomDialog dialog=new CustomDialog("更新权限", cb, new CustomDialog.onFinishListener() {
            @Override
            public void onConfirm(Node node) {
                updateUserPermission(uid,bid,PermissionConvert.getPermissionByName(cb.getValue()));
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.create();
    }

    private void updateUserPermission(int uid,int bid,int permission){
        if(upthread==null) {
            upthread = new UpdatePermissionThread(bid, uid, permission, new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    upthread = null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            boolean in = false;
                            for (Label label : userMap.keySet()) {
                                if (userMap.get(label).getUid() == uid) {
                                    setLabelColor(label, permission);
                                    in = true;
                                    break;
                                }
                            }
                            //代表是新加入的
                            if (!in) {
                                TUser user = (TUser) result;
                                Label label = new Label(user.getUname());
                                initLabel(label, user);
                            }
                            new DialogBuilder(titleField).setTitle("成功").setMessage("修改成功").setNegativeBtn("确定").create();
                        }
                    });
                }

                @Override
                public void onFailed(String reason) {
                    upthread = null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            new DialogBuilder(titleField).setTitle("失败").setMessage(reason).setNegativeBtn("确定").create();
                        }
                    });
                }
            });
            upthread.start();
        }
    }


//*///////////////////////////////////////////////////////

//*//////////////////////////////////////////////////////
    private void startForAdd(int bid){
        HBox box=new HBox();
        JFXTextField utf=new JFXTextField();
        utf.setPromptText("用户账号");
        utf.setPickOnBounds(true);
        utf.setLabelFloat(true);
        JFXComboBox<String> cb=new JFXComboBox<>();
        cb.getItems().addAll("可查看","可编辑","所有权限");
        cb.setValue("可查看");

        box.getChildren().addAll(utf,cb);
        HBox.setMargin(utf,new Insets(1,5,1,5));
        HBox.setMargin(cb,new Insets(1,3,1,2));
        CustomDialog dialog=new CustomDialog("添加用户", box, new CustomDialog.onFinishListener() {
            @Override
            public void onConfirm(Node node) {
                int permission=PermissionConvert.getPermissionByName(cb.getValue());
                addUserPermission(utf.getText().trim(),bid,permission);
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.create();
    }

    private void addUserPermission(String act,int bid,int permission){

        QueryUserThread thread=new QueryUserThread(act, new OnFinishListener() {
            @Override
            public void onSuccess(Object result) {
                TUser user=(TUser)result;
                boolean suc=true;
                for(Label label:userMap.keySet()){
                    if(userMap.get(label).getUid()==user.getUid())
                    {
                        suc=false;
                        break;
                    }
                }
                if(suc)
                    updateUserPermission(user.getUid(),bid,permission);
                else
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            new DialogBuilder(titleField).setTitle("错误").setMessage("对象已存在").setNegativeBtn("确认").create();
                        }
                    });
                }

            }

            @Override
            public void onFailed(String reason) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        new DialogBuilder(titleField).setTitle("错误").setMessage(reason).setNegativeBtn("确认").create();
                    }
                });
            }
        });
        thread.start();

    }
//*////////////////////////////////////////////////////////

//*////////////////////////////////////////////////////////


}
