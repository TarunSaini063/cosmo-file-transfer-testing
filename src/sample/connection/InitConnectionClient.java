package sample.connection;

import sample.connection.callbacks.ConnectionCNF;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class InitConnectionClient implements Runnable{
    ConnectionCNF callback;

    public InitConnectionClient(ConnectionCNF callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
         final InetSocketAddress hostAddress;
         SocketChannel client;
        if (Common.hostAddress != null) {
            hostAddress = Common.hostAddress;
            if (Common.client != null) {
                client = Common.client;
            } else {
                try {
                    client = SocketChannel.open(hostAddress);
                    Common.client = client;
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.clientConnected("FAILURE");
                    return;
                }
            }
        } else {
            hostAddress = new InetSocketAddress(Common.port);
            Common.hostAddress = hostAddress;
            try {
                client = SocketChannel.open(hostAddress);
                Common.client = client;
            } catch (IOException e) {
                e.printStackTrace();
                callback.clientConnected("FAILURE");
                return;
            }
        }
        try {
            assert client != null;
            Common.clientObjectOutputStream = new ObjectOutputStream(client.socket().getOutputStream());
            Common.clientObjectInputStream = new ObjectInputStream(client.socket().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            callback.clientConnected("FAILURE");
            return;
        }
        callback.clientConnected("SUCCESS");
    }
}
