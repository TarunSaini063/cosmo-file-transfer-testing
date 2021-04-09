package sample.connection;

import sample.ConnectionIO;
import sample.connection.sockets.FileReceiver;
import sample.connection.sockets.FileSender;
import sample.iohandler.FileReader;
import sample.iohandler.FileWriter;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

public class Common {
    public static  boolean CLIENT = false;
    public static ConnectionIO CONNECTION_STATUS =  ConnectionIO.BREAK;
    public static String ip = "localhost";
    public static int port = 32443;
    public static FileSender fileSender = null;
    public static FileReceiver fileReceiver = null;
    public static ServerSocketChannel serverSocketChannel = null;
    public static SocketChannel server = null;
    public static InetSocketAddress hostAddress = null;
    public static SocketChannel client = null;
    public static ObjectOutputStream clientObjectOutputStream;
    public static ObjectInputStream clientObjectInputStream;
    public static ObjectOutputStream serverObjectOutputStream;
    public static ObjectInputStream serverObjectInputStream;
    public static String receivedPath = new File(System.getProperty("user.home")).getAbsolutePath()+"'"+ FileSystems.getDefault()+"'"+"Downloads/";
}
