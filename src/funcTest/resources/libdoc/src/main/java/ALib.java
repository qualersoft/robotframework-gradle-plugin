import org.robotframework.javalib.library.AnnotationLibrary;

@Keywords
public class ALib extends AnnotationLibrary {

  public ALib() {
    super("keywords/*.class");
  }
}