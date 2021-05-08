import org.robotframework.javalib.library.AnnotationLibrary;

public class ALib extends AnnotationLibrary {

  public ALib() {
    super("de/**/*.class");
  }

  /**
   * This is a dummy keyword for plain java class libdoc
   *
   * @param keywordName The name of the keyword
   */
  @Override
  public String getKeywordDocumentation(String keywordName) {
    if (keywordName.equals("__intro__")) {
      return "This is the general library documentation of ALib.";
    }
    return super.getKeywordDocumentation(keywordName);
  }
}
