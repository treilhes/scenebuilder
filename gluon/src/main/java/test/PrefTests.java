package test;
import com.oracle.javafx.scenebuilder.gluon.preferences.GluonDocumentPreferences;
import com.oracle.javafx.scenebuilder.gluon.preferences.GluonPreferences;

public class PrefTests {

	public PrefTests() {
		// TODO Auto-generated constructor stub
	}

	public static void doDocTest(GluonDocumentPreferences gdp) {
		
		gdp.getDefaultPosition().readFromJavaPreferences();
		gdp.getDefaultText().readFromJavaPreferences();
		System.out.println(gdp);
		
		gdp.getDefaultPosition().setValue(gdp.getDefaultPosition().getValue() + "x");
		gdp.getDefaultText().setValue(gdp.getDefaultText().getValue() + "y");
		
		gdp.getDefaultPosition().writeToJavaPreferences();
		gdp.getDefaultText().writeToJavaPreferences();
	}
	
	public static void doTest(GluonPreferences gp) {
		
		gp.getDefaultColor().readFromJavaPreferences();
		gp.getSaveFolder().readFromJavaPreferences();
		System.out.println(gp);
		
		gp.getDefaultColor().setValue(gp.getDefaultColor().getValue() + "z");
		gp.getSaveFolder().setValue(gp.getSaveFolder().getValue() + "a");
		
		gp.getDefaultColor().writeToJavaPreferences();
		gp.getSaveFolder().writeToJavaPreferences();
	}
}
