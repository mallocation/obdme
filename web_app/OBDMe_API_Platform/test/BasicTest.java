import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;
import models.obdme.User;

public class BasicTest extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
		Fixtures.load("data.yml");
	}

    @Test
    public void aVeryImportantThingToTest() {
    	User user = User.findByEmail("farmboy30@gmail.com");
    	assertNotNull(user);
    }

}
