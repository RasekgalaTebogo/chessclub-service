package za.co.chessclub.error;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(Long id) { super("Member with ID " + id + " not found."); }
}
