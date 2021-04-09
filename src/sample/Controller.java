package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import sample.connection.Common;
import sample.connection.InitConnectionClient;
import sample.connection.InitConnectionServer;
import sample.connection.callbacks.ConnectionCNF;
import sample.connection.callbacks.FileCNF;
import sample.connection.callbacks.FileMetaDataCallBack;
import sample.connection.sockets.FileMetaDataReceiver;
import sample.connection.sockets.FileMetaDataSender;
import sample.connection.sockets.FileReceiver;
import sample.connection.sockets.FileSender;
import sample.iohandler.FileReader;
import sample.iohandler.FileWriter;
import sample.wirelessfileslistview.WirelessListEntry;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Statement;
import java.util.*;

public class Controller implements Initializable {
    private int confirmation = 0;
    ConnectionIO CLIENT = ConnectionIO.WAITING, SERVER = ConnectionIO.WAITING,
            CURRENT_TRANSFER = ConnectionIO.COMPLETED, FILE_META_RECEIVER = ConnectionIO.INACTIVE;
    @FXML
    private Button remove;

    @FXML
    void removeList(ActionEvent event) {

    }

    @FXML
    private ListView<WirelessListEntry> listView;

    ObservableList<WirelessListEntry> observableList = FXCollections.observableArrayList();
    ArrayList<File> arrayList = new ArrayList<>();
    FileChooser fil_chooser = new FileChooser();

    @FXML
    private Label fileName;


    @FXML
    private Button select;

    @FXML
    private Button send;


    @FXML
    void selectFile(ActionEvent event) {

        List<File> selectedFiles = fil_chooser.showOpenMultipleDialog(select.getScene().getWindow());
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                fileName.setText(file.getAbsolutePath());
                System.out.println(file.length());
                System.out.println(file.length() / (1024 * 1024) + " MB");
                if (file.isFile()) {
                    System.out.println("File");
                    arrayList.add(file);
                    observableList.add(new WirelessListEntry(file.getName()));
                } else System.out.println("Directory");
            }
        }
    }
    ConnectionCNF callback = new ConnectionCNF() {
        @Override
        public void clientConnected(String message) {
            if (message.equals("FAILURE")) {
                confirmation = -1;
                CLIENT = ConnectionIO.BREAK;
            } else {
                confirmation++;
                CLIENT = ConnectionIO.OK;
            }
            System.out.println(message);
        }

        @Override
        public void serverConnected(String message) {
            if (message.equals("FAILURE")) {
                confirmation = -1;
                SERVER = ConnectionIO.BREAK;
            } else {
                confirmation++;
                SERVER = ConnectionIO.OK;
            }
            System.out.println(message);
        }
    };

    FileCNF fileCNFCallback = new FileCNF() {
        @Override
        public void onReceived(String  fileMetaData) {
            System.out.println("Received " + fileMetaData);
            CURRENT_TRANSFER = ConnectionIO.COMPLETED;
        }

        @Override
        public void onSend(String fileMetaData) {
            System.out.println("sending " + fileMetaData);
            CURRENT_TRANSFER = ConnectionIO.COMPLETED;
            next();
        }
    };

    FileMetaDataCallBack fileMetaDataCallBack = new FileMetaDataCallBack() {
        @Override
        public void onReceived(FileMetaData fileMetaData) {
            if (fileMetaData != null) {
                System.out.println(fileMetaData.toString());
                try {
                    FileWriter fileWriter = new FileWriter(Common.receivedPath + fileMetaData.getName());
                    FileReceiver fileReceiver = Common.fileReceiver;
                    fileReceiver.receiveNextFile(fileWriter, fileMetaData.getSize());
                    Thread receiverThread = new Thread(fileReceiver);
                    CURRENT_TRANSFER = ConnectionIO.RECEIVING;
                    receiverThread.start();
                } catch (IOException e) {
                    System.out.println("this file is already exist ");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Error in meta sending");
            }

        }

        @Override
        public void onSend(FileMetaData fileMetaData) {
            if (fileMetaData != null) {
                System.out.println(fileMetaData.toString());
                FileSender fileSender = Common.fileSender;
                try {
                    FileReader fileReader = new FileReader(fileSender, fileMetaData.getPath(), fileCNFCallback);
                    Thread fileReaderThread = new Thread(fileReader);
                    CURRENT_TRANSFER = ConnectionIO.SENDING;
                    fileReaderThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Error in meta received");

            }
        }

    };


    @FXML
    void sendFile(ActionEvent event) throws IOException {


        if (SERVER == ConnectionIO.WAITING || CLIENT == ConnectionIO.WAITING) {
            System.out.println(SERVER.toString() + " " + CLIENT.toString());
        } else {
            if (CLIENT == ConnectionIO.BREAK) {
                InitConnectionClient initConnectionClient = new InitConnectionClient(callback);
                Thread clientInitThread = new Thread(initConnectionClient);
                clientInitThread.start();
            } else if (SERVER == ConnectionIO.BREAK) {
                InitConnectionServer initConnectionServer = new InitConnectionServer(callback);
                Thread serverInitThread = new Thread(initConnectionServer);
                serverInitThread.start();

            } else {
                if(FILE_META_RECEIVER == ConnectionIO.INACTIVE) {
                    send.setDisable(true);
                    next();
                    FILE_META_RECEIVER = ConnectionIO.ACTIVE;
                }

            }
        }
    }

    private File getNext() {
        if (arrayList.size() > 0) {
            File file = arrayList.get(0);
            arrayList.remove(0);
            return file;
        }
        return null;
    }

    private boolean makeConnection() {
        return true;
    }

    public void next() {
        File file = getNext();
        assert file != null;
        FileMetaData fileMetaData = new FileMetaData(file);
        FileMetaDataSender fileMetaDataSender = new FileMetaDataSender(fileMetaDataCallBack,fileMetaData);
        Thread fileMetaDataSenderThread = new Thread(fileMetaDataSender);
        fileMetaDataSenderThread.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.setItems(observableList);
        fil_chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Select files", "*.*"));
    }

}
