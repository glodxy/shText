package classes.sample;

import classes.thread.*;
import classes.util.CustomDialog;
import com.jfoenix.controls.*;
import classes.domain.Book;
import classes.domain.Chapter;
import classes.domain.Data;
import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import classes.util.BookTab;
import classes.util.DialogBuilder;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MainController implements Initializable {
    @FXML private JFXTreeView treeView;
    @FXML private TextField searchField;
    @FXML private JFXTabPane tab;


    private Map<TreeItem,Book> bookMap=new HashMap<>();

    private Map<TreeItem,Tab> tabMap=new HashMap<>();

    private Thread refreshThread=null;
    private UpdateBookThread updateBookThread=null;
    private DeleteBookThread deleteBookThread=null;
    private AddBookThread addBookThread=null;
    public QueryUserPermissionThread userPermissionThread=null;

    public ChapterWindow editWindow=null;




    private EventHandler<MouseEvent> me=new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Node node=mouseEvent.getPickResult().getIntersectedNode();
            if(node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)){
                TreeItem item=((TreeItem)treeView.getSelectionModel().getSelectedItem());
                if(mouseEvent.getButton()==MouseButton.PRIMARY)
                {
                    if(mouseEvent.getClickCount()==1)
                        onTreeItemActioned(item);
                    else if(mouseEvent.getClickCount()==2)
                        onChapterItemActioned(item);
                }
                else if(mouseEvent.getButton()==MouseButton.SECONDARY&&item.getParent()==treeView.getRoot())
                    onTreeItemToggled(item,mouseEvent.getScreenX(),mouseEvent.getScreenY());
            }
            else{
                if(mouseEvent.getButton()==MouseButton.SECONDARY)
                    onTreeViewToggled(mouseEvent.getScreenX(),mouseEvent.getScreenY());
            }
        }
    };




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTreeView();
        initAction();
    }

    private void initAction(){
        treeView.addEventFilter(MouseEvent.MOUSE_CLICKED,me);
    }

    private void initTreeView(){
        tab.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Data.available){
                    try{
                        Thread.sleep(1);
                    }catch(InterruptedException e){
                        System.out.println("not start init interrupt");
                    }
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if(Data.failed){
                            new DialogBuilder(searchField).setTitle("??????").setMessage(Data.errorReason).setNegativeBtn("??????").create();
                        }
                        else {
                            treeView.setShowRoot(false);
                            treeView.setRoot(new TreeItem());
                            for (Book book : Data.getBookList()) {
                                TreeItem<String> item = new TreeItem<>(book.getBtitle());
                                bookMap.put(item,book);
                                for (Chapter chapter : Data.getChapterList(book)) {
                                    TreeItem<String> c = new TreeItem<>(chapter.getCseq() + "." + chapter.getCtitle());
                                    item.getChildren().add(c);
                                }
                                treeView.getRoot().getChildren().add(item);
                            }
                        }
                    }
                });
            }
        });
        thread.start();

    }

 //*////////////////////////////////////////////////////////////////////////
    private void onChapterItemActioned(TreeItem t1){
        //????????????
        if(!t1.getParent().equals(treeView.getRoot())){
            if(userPermissionThread==null){
                userPermissionThread=new QueryUserPermissionThread(bookMap.get(t1.getParent()), new OnFinishListener() {
                    @Override
                    public void onSuccess(Object result) {
                        userPermissionThread=null;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if(editWindow==null){
                                    int bid=bookMap.get(t1.getParent()).getBid();
                                    int cid=Data.getCidByValue(bid,(String)t1.getValue());
                                    initChapterWindow(cid,bid);
                                }
                                //???????????????
                                else{
                                    try {
                                        editWindow.stop();
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                    int bid=bookMap.get(t1.getParent()).getBid();
                                    int cid=Data.getCidByValue(bid,(String)t1.getValue());
                                    initChapterWindow(cid,bid);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailed(String reason) {
                        userPermissionThread=null;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                new DialogBuilder(searchField).setTitle("??????").setMessage(reason).setNegativeBtn("??????").create();
                            }
                        });
                    }
                });
                userPermissionThread.start();
            }
        }
    }

    public void initChapterWindow(int cid,int bid){
        if(cid==0)
            new DialogBuilder(tab).setTitle("??????").setMessage("???????????????").setNegativeBtn("??????").create();
        else {
            try {
                Stage stage = new Stage();
                editWindow = new ChapterWindow(cid,bid);
                editWindow.start(stage);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
//*//////////////////////////////////////////////////////////////////////////////
    private void onTreeItemActioned(TreeItem t1){
        //????????????
        if(t1.getParent().equals(treeView.getRoot())) {
            Tab t;
            //?????????????????????
            if((t=tabMap.get(t1))!=null)
            {
                tab.getSelectionModel().select(t);
            }
            //??????????????????
            else {
                t = new BookTab(bookMap.get(t1),this);
                t.setOnClosed(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                       tabMap.remove(t1);
                    }
                });
                tab.getTabs().add(t);
                tab.getSelectionModel().select(t);
                tabMap.put(t1,t);
            }
            StackPane pane = (StackPane) t.getContent();
            ScrollPane sp = (ScrollPane) pane.getChildren().get(0);
            sp.requestLayout();
        }
    }

    private void onTreeViewToggled(double x,double y){
        ContextMenu menu=new ContextMenu();
        MenuItem itemAdd=new MenuItem("??????");
        itemAdd.setOnAction(event -> {
            startForAddBook();
        });
        MenuItem itemRefresh=new MenuItem("??????");
        itemRefresh.setOnAction(event -> {
            refreshTree();
        });
        menu.getItems().addAll(itemAdd,itemRefresh);
        menu.show(searchField.getScene().getWindow(),x,y);
    }

    private void startForAddBook(){
        HBox box=new HBox();
        JFXTextField utf=new JFXTextField();
        utf.setPromptText("??????");
        utf.setPickOnBounds(true);
        utf.setLabelFloat(true);

        box.getChildren().addAll(utf);
        HBox.setMargin(utf,new Insets(1,5,1,5));
        CustomDialog dialog=new CustomDialog("????????????", box, new CustomDialog.onFinishListener() {
            @Override
            public void onConfirm(Node node) {
                if(!utf.getText().trim().isEmpty()){
                    String title=utf.getText().trim();
                    toggledAddBook(title);
                }else{
                    new DialogBuilder(searchField).setTitle("??????").setMessage("????????????").setNegativeBtn("??????").create();
                }
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.create();
    }

    private void toggledAddBook(String title){
        if(addBookThread==null){
            addBookThread=new AddBookThread(title, new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    addBookThread=null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Book b=(Book)result;
                            addBook(b);
                            new DialogBuilder(searchField).setTitle("??????").setMessage("?????? "+b.getBtitle()).setNegativeBtn("??????").create();
                        }
                    });

                }

                @Override
                public void onFailed(String reason) {
                    addBookThread=null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            new DialogBuilder(searchField).setTitle("??????").setMessage(reason).setNegativeBtn("??????").create();
                        }
                    });
                }
            });
            addBookThread.start();
        }
    }


//*//////////////////////////////////////////////////////////////////////////////////////
    private void onTreeItemToggled(TreeItem item,double x,double y){
        ContextMenu menu=new ContextMenu();
        MenuItem itemUpdate=new MenuItem("??????");
        itemUpdate.setOnAction(event -> {
            startForUpdateBook(bookMap.get(item));
        });
        MenuItem itemDelete=new MenuItem("??????");
        itemDelete.setOnAction(event -> {
            startForDeleteBook(bookMap.get(item));
        });
        menu.getItems().addAll(itemDelete,itemUpdate);
        menu.show(searchField.getScene().getWindow(),x,y);
    }

    private void startForUpdateBook(Book book){
        HBox box=new HBox();
        JFXTextField utf=new JFXTextField();
        utf.setPromptText("??????");
        utf.setPickOnBounds(true);
        utf.setLabelFloat(true);

        box.getChildren().addAll(utf);
        HBox.setMargin(utf,new Insets(1,5,1,5));
        CustomDialog dialog=new CustomDialog("??????????????????,", box, new CustomDialog.onFinishListener() {
            @Override
            public void onConfirm(Node node) {
                if(updateBookThread==null){
                    //?????????????????????
                    if(!utf.getText().trim().isEmpty()) {
                        book.setBtitle(utf.getText().trim());
                        updateBookThread = new UpdateBookThread(book, new OnFinishListener() {
                            @Override
                            public void onSuccess(Object result) {
                                updateBookThread=null;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (TreeItem item : bookMap.keySet()) {
                                            if (bookMap.get(item).getBid() == book.getBid()) {
                                                item.setValue(book.getBtitle());
                                                tabMap.get(item).setText(book.getBtitle());
                                                break;
                                            }
                                        }

                                        new DialogBuilder(searchField).setTitle("??????").setMessage("????????????").setNegativeBtn("??????").create();
                                    }
                                });
                            }

                            @Override
                            public void onFailed(String reason) {
                                updateBookThread=null;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        new DialogBuilder(searchField).setTitle("??????").setMessage(reason).setNegativeBtn("??????").create();
                                    }
                                });
                            }
                        });
                        updateBookThread.start();
                    }
                    else{
                        new DialogBuilder(searchField).setTitle("??????").setMessage("????????????").setNegativeBtn("??????").create();
                    }
                }
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.create();
    }

    private void startForDeleteBook(Book book){
        new DialogBuilder(searchField).setTitle("??????").setMessage("?????????????????????").setPositiveBtn("??????", new DialogBuilder.OnClickListener() {
            @Override
            public void onClick() {
                toggledDeleteBook(book.getBid());
            }
        }).setNegativeBtn("??????").create();
    }

    private void toggledDeleteBook(int bid){
        if(deleteBookThread==null){
            deleteBookThread=new DeleteBookThread(bid, new OnFinishListener() {
                @Override
                public void onSuccess(Object result) {
                    deleteBookThread=null;
                    Book b=(Book)result;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for(TreeItem item:bookMap.keySet()){
                                if(bookMap.get(item).getBid()==b.getBid()){
                                    //??????treeitem
                                    item.getParent().getChildren().remove(item);
                                    if(tabMap.get(item)!=null) {
                                        tab.getTabs().remove(tabMap.get(item));
                                    }
                                    bookMap.remove(item);
                                    tabMap.remove(item);
                                }
                            }
                            new DialogBuilder(searchField).setTitle("??????").setMessage(b.getBtitle()+" ????????????").setNegativeBtn("??????").create();
                        }
                    });
                }

                @Override
                public void onFailed(String reason) {
                    deleteBookThread=null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            new DialogBuilder(searchField).setTitle("??????").setMessage(reason).setNegativeBtn("??????").create();
                        }
                    });
                }
            });
            deleteBookThread.start();
        }
    }

//*////////////////////////////////////////////////////////?????????
    private void refreshTree(){
        Data.update();
        if(refreshThread==null){
            refreshThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!Data.available){
                        try{
                            Thread.sleep(1);
                        }catch(InterruptedException e){
                            System.out.println("not start refresh interrupt");
                        }
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(Data.failed){
                                new DialogBuilder(searchField).setTitle("??????").setMessage(Data.errorReason).setNegativeBtn("??????").create();
                            }
                            else {
                                List<TreeItem> tl=new ArrayList<>(bookMap.keySet());
                                for (TreeItem item : tl) {
                                    //??????treeitem?????????
                                    if (!Data.getBookList().contains(bookMap.get(item))) {
                                        treeView.getRoot().getChildren().remove(item);
                                        bookMap.remove(item);
                                        if (tabMap.get(item) != null) {
                                            tab.getTabs().remove(tabMap.get(item));
                                            tabMap.remove(item);
                                        }
                                    }
                                }
                                for (Book book : Data.getBookList()) {
                                    if (!bookMap.values().contains(book)) {
                                        addBook(book);
                                    } else {
                                        updateBook(book);
                                    }
                                }
                            }
                            refreshThread=null;
                        }
                    });
                }
            });
            refreshThread.start();
        }
    }

    private void updateBook(Book b){
        for(TreeItem<String> item:bookMap.keySet()){
            if(bookMap.get(item).equals(b)){
                List<TreeItem<String>> existList=new ArrayList<>();
                //?????????????????????????????????
                for(Chapter c:Data.getChapterList(b)){
                    boolean exist=false;
                    for(TreeItem<String> i:item.getChildren()){
                        if(i.getValue().equals(c.getCseq()+"."+c.getCtitle())){
                            exist=true;
                            existList.add(i);
                            System.out.println("title:"+i.getValue());
                            break;
                        }
                    }
                    //????????????????????????
                    if(!exist){
                        TreeItem<String> temp=new TreeItem<>(c.getCseq()+"."+c.getCtitle());
                        item.getChildren().add(c.getCseq()-1,temp);
                        existList.add(temp);
                    }
                }
                //????????????treeitem,???????????????
                for(TreeItem<String> i:item.getChildren()){
                    if(!existList.contains(i)){
                        item.getChildren().remove(i);
                    }
                }

            }
        }
    }

    private void addBook(Book b){
        TreeItem item=new TreeItem(b.getBtitle());
        if(Data.getChapterList(b)!=null) {
            for (Chapter c : Data.getChapterList(b)) {
                TreeItem i = new TreeItem(c.getCseq() + "." + c.getCtitle());
                item.getChildren().add(i);
            }
        }
        bookMap.put(item,b);
        treeView.getRoot().getChildren().add(0,item);
    }

    public void addChapter(Chapter c){

    }
//*//////////////////////////////////////////////////////////////////
    @FXML
    protected void onQuitActioned(ActionEvent event){
        Platform.exit();
    }

    @FXML protected void onCloseActioned(ActionEvent event){
    }

    @FXML protected void onRefreshActioned(ActionEvent event){
        refreshTree();
    }



}
