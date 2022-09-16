package exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ClientException extends Exception {
    public ClientException(String message){
        super(message);
    }

}
