package de.hsrm.orchestrationsystem.testcase_orchestration.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TestCaseException extends RuntimeException {

    public TestCaseException(String msg){
        super(msg);
    }

    public TestCaseException(String msg, Exception cause){
        super(msg, cause);
    }
}
