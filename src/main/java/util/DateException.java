package util;

public class DateException extends Exception{
    Exception e;

    public DateException(String message){
        super(message);
        e = new Exception(message);
    }

    public String toString() {
        return e.getMessage();
    }
}
