package sample.connection.sockets;

import sample.FileMetaData;
import sample.connection.Common;
import sample.connection.callbacks.FileMetaDataCallBack;

import java.io.IOException;
import java.io.ObjectInputStream;

public class FileMetaDataReceiver implements Runnable {
    FileMetaDataCallBack callBack;

    public FileMetaDataReceiver(FileMetaDataCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream;
        if(Common.CLIENT){
            objectInputStream = Common.clientObjectInputStream;
        }else{
            objectInputStream = Common.serverObjectInputStream;
        }
        while (true){
            FileMetaData fileMetaData;
            try {
                fileMetaData = (FileMetaData)objectInputStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
                callBack.onReceived(null);
                continue;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                callBack.onReceived(null);
                continue;
            }
            callBack.onReceived(fileMetaData);
        }
    }
}
