package sample.connection.callbacks;

import sample.FileMetaData;

public interface FileMetaDataCallBack {
    public void onReceived(FileMetaData message);
    public void onSend(FileMetaData message);
}
