import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

@RobotKeywords
public class AKeywordDef {
  @RobotKeyword("Say hello")
  @ArgumentNames("greeting")
  public void sayHello(greeting: String) {
    println("Hello, " + greeting);
  }
}