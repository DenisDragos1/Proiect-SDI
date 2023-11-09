package requests;

/**
 * The Requestable interface should be implemented by any object that is sent to the server.
 */
public interface Requestable<T> {
    T getData();
    int getNumber();
    RequestType getType();
}
