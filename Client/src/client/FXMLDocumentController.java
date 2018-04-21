package client;

import Server.Message;
import Server.UserDTO;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.healthmarketscience.rmiio.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javax.imageio.ImageIO;

public class FXMLDocumentController implements Initializable {

    private int stageId, firstMsgToSendFlag = 1, firstMsgToRecFlag = 1;
    String name;
    boolean flage = false;
    @FXML
    private Label friendNameLbl;
    @FXML
    private ImageView mini;
    @FXML
    private ImageView cancel;
    @FXML
    private TextField TextField;
    @FXML
    private VBox vbox;
    @FXML
    private ImageView imgv;
    @FXML
    private Button sendFileBtn;
    @FXML
    private ColorPicker color;

    private File fileToSend, fileToRec;
    @FXML
    private ComboBox<Integer> fontSize;
    @FXML
    private ComboBox<String> fontFamily;
    private Text textName;
    private UserDTO friend;
    private byte[] fileBytes;
    private String fileExtension, receivedFileExtension;
    private String fileName, receivedFileName;
    private int extensionIndex;
    private Button acceptFileBtn, rejectFileBtn;
    static String colorF;
    static int sizeF;
    static String familyF;
    private Message currMsg = new Message();
    private RemoteInputStream receivedData;
    private int sentFileFlag = 0;
    private long receivingTimes = 1, counterToReceivingTimes = 0, packetNo = 1;

    public FXMLDocumentController(UserDTO friend) {
        colorF = "BLACK";
        familyF = "Arial";
        sizeF = 12;
        stageId = friend.getId();
        this.friend = friend;
        acceptFileBtn = new Button("Save");
        rejectFileBtn = new Button("Reject");

    }

    public int getStageId() {
        return stageId;
    }

    @FXML
    private void mini(MouseEvent e) {

        Stage stage = (Stage) ((Node) (e.getSource())).getScene().getWindow();
        stage.setIconified(true);

    }

    @FXML
    private void exit(MouseEvent e) {

        try {
            ((Node) (e.getSource())).getScene().getWindow().hide();
            Client.currClient.removeFromChatControllers(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     currMsg.setMsg(txtMsg.getText());
                            currMsg.setMsgColor(toRgbString(color.getValue()));
                            currMsg.setMsgFontFamily(fontFamily.getValue());
                            currMsg.setMsgFontSize(fontSize.getValue());
                            serObj.tellOthers(clientName, currMsg);
                            txtMsg.setText("");
    
    
     */
    @FXML
    private void handleEnter(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            try {
                if (!TextField.getText().trim().isEmpty()) {
                    currMsg.setMsg(TextField.getText());
                    currMsg.setMsgColor(toRgbString(color.getValue()));
                    currMsg.setMsgFontFamily(fontFamily.getValue());
                    currMsg.setMsgFontSize(fontSize.getValue());
                    Client.ref.tellUser(currMsg, Client.currClientDTO, stageId);
//                        
                    TextField.setText("");

                }

            } catch (RemoteException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }

            Label label1 = new Label(currMsg.getMsg());
            label1.setStyle("-fx-background-color:#DCC6E0;-fx-border-radius: 15 15 15 15; -fx-background-radius: 10 10 10 10;-fx-padding:5 5 5 5;");
            HBox hbox = new HBox(label1);
            textName = new Text(Client.currClientDTO.getFirstname());
            HBox nameHBox = new HBox(textName);
            hbox.setAlignment(Pos.BASELINE_RIGHT);
            nameHBox.setAlignment(Pos.BASELINE_RIGHT);
            label1.setTextFill(Paint.valueOf(currMsg.getMsgColor()));
            label1.setFont(new Font(currMsg.getMsgFontFamily(), currMsg.getMsgFontSize()));

            if (firstMsgToSendFlag == 0) {
                vbox.getChildren().add(hbox);
                vbox.setSpacing(10);
                TextField.clear();
                firstMsgToRecFlag = 1;
            } else {
                vbox.getChildren().add(nameHBox);
                vbox.getChildren().add(hbox);
                vbox.setSpacing(10);
                TextField.clear();
                firstMsgToSendFlag = 0;
                firstMsgToRecFlag = 1;

            }
        }
    }

    public void sendFile() {
        long timesOfSending = 1;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileToSend = fileChooser.showOpenDialog((Stage) friendNameLbl.getScene().getWindow());
        try {
            if (fileToSend != null) {
                System.out.println(fileToSend.length());
                //{
                fileBytes = new byte[(int) fileToSend.length()];
                fileBytes = Files.readAllBytes(fileToSend.toPath());
                System.out.println(fileBytes);
                ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBytes);
                RemoteInputStreamServer data = new SimpleRemoteInputStream(fileStream);
                extensionIndex = getExtensionIndex(fileToSend);
                fileExtension = fileToSend.getName().substring(extensionIndex + 1);
                fileName = fileToSend.getName().substring(0, extensionIndex);
                Client.ref.tellUser(fileName, Client.currClientDTO, friend.getId(), data.export(), fileExtension, timesOfSending, 1);
                System.out.println("Sent Successfully");
                sentFileFlag = 1;
                showSentMsg();
            }
            /*else{
                    timesOfSending = fileToSend.length()/147266558;
                    if(fileToSend.length()%147266558!=0)
                        timesOfSending++;
                    for(long i =0 ; i<timesOfSending ; i++){
                        fileBytes = new byte[(int) fileToSend.length()];
                        fileBytes = Files.readAllBytes(fileToSend.toPath());
                        ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBytes);
                        RemoteInputStreamServer data = new SimpleRemoteInputStream(fileStream);
                        extensionIndex = getExtensionIndex(fileToSend);
                        fileExtension = fileToSend.getName().substring(extensionIndex + 1);
                        fileName = fileToSend.getName().substring(0, extensionIndex);
                        Client.ref.tellUser(fileName, Client.currClientDTO, friend.getId(), data.export(), fileExtension, timesOfSending, i+1);
                        sentFileFlag = 1;
                    }
                }*/
            //}
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File Size");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("File is too large!");
            alert.showAndWait();
        }
    }

    public int getExtensionIndex(File file) {
        int index = 0;
        String extension = "";
        for (int i = 0; i < file.getName().length(); i++) {
            if (file.getName().charAt(i) == '.') {
                index = i;
            }
        }
        return index;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            System.out.println("try to add client to chatvector");
            Client.currClient.addChatController(this);
            System.out.println("should be added");
            friendNameLbl.setText(friend.getFirstname());

            Client.currClient.setName(name);
//            imgv.setImage(new Image(getClass().getResourceAsStream("smile.png")));
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(friend.getImg()));
            Image userImage = SwingFXUtils.toFXImage(img, null);
            imgv.setImage(userImage);

        } catch (RemoteException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        color.setStyle("-fx-text-fill: #FAA00F");
        color.setValue(Color.BLACK);
        fontSize.setValue(12);
        fontFamily.setValue("Arial");
        fontSize.getItems().addAll(2, 4, 6, 8, 10, 12, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34);
        fontFamily.getItems().addAll("Arial", "Black", "Impact", "arial", "Georgia");
//        cancel.setImage(new Image(getClass().getResourceAsStream("cancel.png")));
//        cancel.setFitHeight(28);
//        cancel.setFitWidth(28);
//        mini.setImage(new Image(getClass().getResourceAsStream("mini.png")));
//        mini.setFitHeight(28);
//        mini.setFitWidth(28);

        acceptFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                BufferedWriter bw = null;
                FileWriter fw = null;
                try {
                    InputStream stream = RemoteInputStreamClient.wrap(receivedData);
                    FileOutputStream output = new FileOutputStream(receivedFileName + "." + receivedFileExtension);
                    int chunk = 4096;
                    byte[] result = new byte[chunk];
                    int readBytes = 0;
                    do {
                        readBytes = stream.read(result);
                        if (readBytes != -1) {
                            output.write(result, 0, readBytes);
                        }
                    } while (readBytes != -1);
                    stream.close();
                    output.close();
                    System.out.println("Received Successfully");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("File Received");
                    alert.setHeaderText(null);
                    alert.setGraphic(null);
                    alert.setContentText("File Received Successfully!");
                    alert.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //For the sender
    public void showSentMsg() {
        HBox nameHBox = new HBox();
        HBox hbox = new HBox();
        Label label1;
        textName = new Text(Client.currClientDTO.getFirstname());
        nameHBox = new HBox(textName);

        if (sentFileFlag == 0) {
            label1 = new Label(TextField.getText());
            label1.setStyle("-fx-background-color:#DCC6E0;-fx-border-radius: 15 15 15 15; -fx-background-radius: 10 10 10 10;-fx-padding:5 5 5 5;");
            label1.wrapTextProperty().setValue(true);
            hbox = new HBox(label1);
        } else {
            label1 = new Label(fileName + "." + fileExtension + " sent successfully!");
            label1.setStyle("-fx-background-color:#DCC6E0;-fx-border-radius: 15 15 15 15; -fx-background-radius: 10 10 10 10;-fx-padding:5 5 5 5;");
            label1.wrapTextProperty().setValue(true);
            hbox = new HBox(label1);
            sentFileFlag = 0;
        }

        hbox.setAlignment(Pos.BASELINE_RIGHT);
        nameHBox.setAlignment(Pos.BASELINE_RIGHT);
        if (firstMsgToSendFlag == 0) {
            vbox.getChildren().add(hbox);
            vbox.setSpacing(10);
            TextField.clear();
            firstMsgToRecFlag = 1;
        } else {
            vbox.getChildren().add(nameHBox);
            vbox.getChildren().add(hbox);
            vbox.setSpacing(10);
            TextField.clear();
            firstMsgToSendFlag = 0;
            firstMsgToRecFlag = 1;
        }
    }

    public void display(String fileName, UserDTO sender, RemoteInputStream data, String extension, long timesOfReceiving, long packetNum) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                receivedData = data;
                receivedFileExtension = extension;
                receivedFileName = fileName;
                receivingTimes = timesOfReceiving;
                packetNo = packetNum;
                VBox fileBox = new VBox();

                if (vbox.getChildren().size() > 0) {
                    HBox b = (HBox) vbox.getChildren().get(vbox.getChildren().size() - 1);
                    if (b.getChildren().get(0) instanceof ImageView) {
                        b.getChildren().remove(0);
                    }
                }
                ImageView imgView = new ImageView(new Image(getClass().getResourceAsStream("images.jpg")));
                imgView.setFitHeight(20);
                imgView.setFitWidth(20);
                Rectangle clip = new Rectangle(
                        imgView.getFitWidth(), imgView.getFitHeight()
                );
                clip.setArcWidth(20);
                clip.setArcHeight(20);
                imgView.setClip(clip);
                Label labelName = new Label(sender.getFirstname());
                Text msg = new Text();

                HBox hboxName = new HBox(labelName);
                Label fileNameLbl = new Label(receivedFileName + "." + receivedFileExtension);
                fileNameLbl.wrapTextProperty().setValue(true);
                fileBox.getChildren().addAll(fileNameLbl, acceptFileBtn, rejectFileBtn);
                fileBox.setStyle("-fx-background-color:#AEA8D3;-fx-border-radius: 15 15 15 15; -fx-background-radius: 10 10 10 10;-fx-padding:5 5 5 5; -fx");
                HBox hbox = new HBox(imgView, fileBox);

                hbox.setAlignment(Pos.BASELINE_LEFT);
                hboxName.setAlignment(Pos.BASELINE_LEFT);

                if (firstMsgToRecFlag == 1) {
                    textName = new Text(sender.getFirstname());
                    vbox.getChildren().add(textName);
                    vbox.getChildren().add(hbox);                    //vbox.getChildren().add(hboxName, hbox);
                    vbox.setSpacing(10);
                    firstMsgToRecFlag = 0;
                    firstMsgToSendFlag = 1;

                } else {
                    vbox.getChildren().add(hbox);
                    vbox.setSpacing(10);
                    firstMsgToSendFlag = 1;
                }
            }
        }
        );
    }

    public void display(String fileName, UserDTO sender, RemoteInputStream data, String extension) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            InputStream stream = RemoteInputStreamClient.wrap(data);
            FileOutputStream output = new FileOutputStream("data.dat");
            int chunk = 4096;
            byte[] result = new byte[chunk];
            int readBytes = 0;
            do {
                readBytes = stream.read(result);
                if (readBytes != -1) {
                    output.write(result, 0, readBytes);
                }
            } while (readBytes != -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void display(Message msg, UserDTO sender) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (vbox.getChildren().size() > 0) {
                        
                        HBox b = (HBox) vbox.getChildren().get(vbox.getChildren().size() - 1);
                        
                        if (b.getChildren().get(0) instanceof ImageView) {
                            
                            b.getChildren().remove(0);
                        }
                        
                    }
                ImageView imgView=new ImageView();
//                imgView.setFitHeight(20);
//                imgView.setFitWidth(20);
BufferedImage img = ImageIO.read(new ByteArrayInputStream(friend.getImg()));
Image userImage = SwingFXUtils.toFXImage(img, null);
imgView.setImage(userImage);
imgView.setFitHeight(20);
imgView.setFitWidth(20);
Rectangle clip = new Rectangle(
        imgView.getFitWidth(), imgView.getFitHeight()
);
clip.setArcWidth(20);
clip.setArcHeight(20);
imgView.setClip(clip);
Label labelName = new Label(sender.getFirstname());
Label label = new Label();
label.setText(msg.getMsg());
label.wrapTextProperty().setValue(true);
label.setStyle("-fx-background-color:#AEA8D3;-fx-border-radius: 15 15 15 15; -fx-background-radius: 10 10 10 10;-fx-padding:5 5 5 5;");

//labelName.setStyle("-fx-background-color:#AEA8D3;-fx-border-radius: 15 15 15 15; -fx-background-radius: 10 10 10 10;-fx-padding:5 5 5 5;");
HBox hboxName = new HBox(labelName);
HBox hbox = new HBox(imgView, label);

hbox.setAlignment(Pos.BASELINE_LEFT);
hboxName.setAlignment(Pos.BASELINE_LEFT);
label.setTextFill(Paint.valueOf(msg.getMsgColor()));
label.setFont(new Font(msg.getMsgFontFamily(), msg.getMsgFontSize()));
// msgsSent.add(msgLbl);
if (firstMsgToRecFlag == 1) {
    textName = new Text(sender.getFirstname());
    vbox.getChildren().add(textName);
    vbox.getChildren().add(hbox);                    //vbox.getChildren().add(hboxName, hbox);
    vbox.setSpacing(10);
    firstMsgToRecFlag = 0;
    firstMsgToSendFlag = 1;
    
} else {
    vbox.getChildren().add(hbox);
    vbox.setSpacing(10);
    firstMsgToSendFlag = 1;
}
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );
    }

    @FXML
    private void getSelectedColor() {
        colorF = toRgbString(color.getValue());

        // colorF= toRgbString(color.getValue());
        //txtMsg.setStyle("-fx-font-family: " + fontFamily.getValue() + ";");
        // txtMsg.setStyle(" -fx-font-size:" + fontSize.getValue() + ";");
        changeTextField(colorF, sizeF, familyF);
    }

    @FXML
    private void getSelectedSize() {

        //txtMsg.setStyle("-fx-text-fill: " + toRgbString(color.getValue()) + ";");
        //txtMsg.setStyle("-fx-font-family: " + fontFamily.getValue() + ";");
//       sizeF= fontSize.getValue();
//       changeTextField(colorF,sizeF,familyF);
        sizeF = fontSize.getValue();
        System.out.println("Entered" + sizeF);

        changeTextField(colorF, sizeF, familyF);

//       
    }

    @FXML
    private void getfontFamily() {

        //txtMsg.setStyle("-fx-text-fill: " + toRgbString(color.getValue()) + ";");
//            familyF=fontFamily.getValue();
//             changeTextField(colorF,sizeF,familyF);
        //txtMsg.setStyle(" -fx-font-size:" + fontSize.getValue() + ";");
        familyF = fontFamily.getValue();
        changeTextField(colorF, sizeF, familyF);

    }

    private void changeTextField(String colorF, int sizeF, String familyF) {
        System.out.println(sizeF);
        TextField.setStyle("-fx-font-family: " + familyF + "; -fx-font-size: " + sizeF + "; -fx-text-fill: " + colorF + ";");
        //txtMsg.setStyle("" + 80 + ";");
        //txtMsg.setStyle("" + colorF + ";");

    }

    private String toRgbString(Color c) {
        return "rgb("
                + to255Int(c.getRed())
                + "," + to255Int(c.getGreen())
                + "," + to255Int(c.getBlue())
                + ")";
    }

    private int to255Int(double d) {
        return (int) (d * 255);
    }
}
