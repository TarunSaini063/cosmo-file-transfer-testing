package sample.connection;

import sample.connection.callbacks.ConnectionCNF;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class InitConnectionServer implements Runnable {
    ConnectionCNF callback;
    private void init(final ServerSocketChannel serverSocketChannel) throws IOException {
        assert !Objects.isNull(serverSocketChannel);

        serverSocketChannel.bind(new InetSocketAddress(Common.port));
    }

    public InitConnectionServer(ConnectionCNF callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        SocketChannel channel = null;
        if (Common.serverSocketChannel == null) {
            try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
                Common.serverSocketChannel = serverSocketChannel;
                try {
                    init(Common.serverSocketChannel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                channel = Common.serverSocketChannel.accept();
                Common.server = channel;
            } catch (IOException e) {
                e.printStackTrace();
                callback.serverConnected("FAILURE");
                return;
            }
        } else {
            if (Common.server == null) {
                try {
                    channel = Common.serverSocketChannel.accept();
                    Common.server = channel;
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.serverConnected("FAILURE");
                    return;
                }
            }
        }
        try {
            assert channel != null;
            Common.serverObjectOutputStream = new ObjectOutputStream(channel.socket().getOutputStream());
            Common.serverObjectInputStream = new ObjectInputStream(channel.socket().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            callback.serverConnected("FAILURE");
            return;
        }
        callback.serverConnected("SUCCESS");
    }
}
