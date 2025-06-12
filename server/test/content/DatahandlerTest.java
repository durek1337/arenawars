package content;

import org.junit.Test;
import static org.junit.Assert.*;

public class DatahandlerTest {
    @Test
    public void testValidEmails() {
        assertTrue(Datahandler.isValidEmail("user@example.com"));
        assertTrue(Datahandler.isValidEmail("name.surname@test.co.uk"));
    }

    @Test
    public void testInvalidEmails() {
        assertFalse(Datahandler.isValidEmail("invalid-email"));
        assertFalse(Datahandler.isValidEmail("user@example"));
    }
}
