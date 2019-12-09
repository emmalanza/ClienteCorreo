import emma.Launcher;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class PruebaTest extends ApplicationTest {

    //no funciona

    @Before
    public void setup() throws Exception {
        ApplicationTest.launch(Launcher.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }

    @Test
    public void iniciarSesion(){

        clickOn("#tf_email");
        write("pruebapruebadi@gmail.com");
        clickOn("#pf_password");
        write("PruebaDi2019");
        clickOn("#bt_aceptar");

    }



}
