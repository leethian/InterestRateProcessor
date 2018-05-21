package util;

public class MASException extends Exception {
    Exception e;

    public MASException(String message){
        super(message);
        e = new Exception(message);
    }

    public String toString() {
        return e.getMessage();
    }
}