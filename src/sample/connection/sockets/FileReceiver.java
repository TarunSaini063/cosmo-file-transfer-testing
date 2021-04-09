package sample.connection.sockets;

import sample.connection.Common;
import sample.connection.callbacks.FileCNF;
import sample.iohandler.FileWriter;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class FileReceiver implements Runnable{
    private final int port = Common.port;
    private FileWriter fileWriter;
    private long size;
    FileCNF callback;
    public FileReceiver() {

    }
    public void setCallBack(FileCNF callback){
        this.callback = callback;
    }
    public void receiveNextFile(FileWriter fileWriter, long size) {
        this.fileWriter = fileWriter;
        this.size = size;
    }

    void receive() throws IOException {
        SocketChannel channel = Common.server;
        try {
            doTransfer(channel);
        }finally {
            this.fileWriter.close();
        }
    }

    private void doTransfer(final SocketChannel channel) throws IOException {
        assert !Objects.isNull(channel);

        this.fileWriter.transfer(channel, this.size);
    }

    @Override
    public void run() {
        SocketChannel channel = Common.server;
        try {
            try {
                doTransfer(channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }finally {
            try {
                this.fileWriter.close();
                callback.onReceived(fileWriter.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
