package sample.connection.callbacks;

import sample.FileMetaData;

public interface FileCNF {
    public void onReceived(String fileName);
    public void onSend(String fileMetaData);
}
