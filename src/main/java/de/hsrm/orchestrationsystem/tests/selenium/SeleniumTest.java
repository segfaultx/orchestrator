package de.hsrm.orchestrationsystem.tests.selenium;

import java.util.Map;

public interface SeleniumTest {

    void run();

    void addContext(Map<String, Object> context);

    SeleniumTest newInstance(String target, Map<String, Object> options);

    void interrupt();
}
