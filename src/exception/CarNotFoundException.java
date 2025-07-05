package exception;

import static utils.Constants.CAR_NOT_FOUND_EXCEPTION_MESSAGE;

public class CarNotFoundException extends Exception{
    public CarNotFoundException(){
        super(CAR_NOT_FOUND_EXCEPTION_MESSAGE);
    }
}
