package exception;

import static utils.Constants.USER_NOT_FOUND_EXCEPTION_MESSAGE;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super(USER_NOT_FOUND_EXCEPTION_MESSAGE);
    }
}