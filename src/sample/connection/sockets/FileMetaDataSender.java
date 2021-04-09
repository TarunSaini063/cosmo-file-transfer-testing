package sample.connection.sockets;

import sample.FileMetaData;
import sample.connection.Common;
import sample.connection.callbacks.FileMetaDataCallBack;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class FileMetaDataSender implements Runnable{
    FileMetaDataCallBack callBack;
    FileMetaData fileMetaData;
    public FileMetaDataSender(FileMetaDataCallBack callBack, FileMetaData fileMetaData) {
        this.callBack = callBack;
        this.fileMetaData = fileMetaData;
    }

    @Override
    public void run() {
        ObjectOutputStream objectOutputStream;
        if(Common.CLIENT) {
            objectOutputStream =Common.clientObjectOutputStream;
        }else{
            objectOutputStream = Common.serverObjectOutputStream;
        }
        assert objectOutputStream != null;
        try {
            objectOutputStream.writeObject(fileMetaData);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            callBack.onSend(null);
            return;
        }
        callBack.onSend(fileMetaData);
    }
}
