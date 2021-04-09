package sample.connection.callbacks;

public interface ConnectionCNF {
    void clientConnected(String message);
    void serverConnected(String message);
}
