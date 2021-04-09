package sample.iohandler;

import org.apache.commons.lang.StringUtils;
import sample.connection.callbacks.FileCNF;
import sample.connection.sockets.FileSender;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class FileReader implements Runnable {
    private final FileChannel channel;
    private final FileSender sender;
    private FileCNF callback;
    private String path;
    public FileReader(final FileSender sender, final String path,FileCNF callback) throws IOException {
        if (Objects.isNull(sender) || StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("sender and path required");
        }

        this.sender = sender;
        this.channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ);
        this.callback = callback;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void read() throws IOException {
        try {
            transfer();
        } finally {
            close();
        }
    }

    void close() throws IOException {
        this.sender.close();
        this.channel.close();
    }

    private void transfer() throws IOException {
        this.sender.transfer(this.channel, 0l, this.channel.size());
    }

    @Override
    public void run() {
        try {
            transfer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close();
                callback.onSend(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
