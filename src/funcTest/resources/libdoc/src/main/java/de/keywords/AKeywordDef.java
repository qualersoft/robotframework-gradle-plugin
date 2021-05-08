package de.keywords;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.annotation.ArgumentNames;

@RobotKeywords
public class AKeywordDef {
  @RobotKeyword("Say hello")
  @ArgumentNames("greeting")
  public void sayHello(String greeting) {
    System.out.println("Hello, " + greeting);
  }
}
