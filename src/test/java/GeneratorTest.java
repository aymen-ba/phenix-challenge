
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class GeneratorTest {


    @Test
    public void testGenerategeneratestoreIds() throws Exception {

        Compactionner.deleteDirectory(new File("data"));

        Generator g = new Generator(1200, 1000, 1000000, "20190629",false);

        List<String> ids = Calculator.getAllStoresIds("data", Main.REGEX_UUID_DATE, Main.REGEX_UUID, Main.REGEX_DATE, "20190629");

        assertEquals(ids.size(), 1200);

        Compactionner.deleteDirectory(new File("data"));
    }


    @Test
    public void testgenerateTransactionsDay() {

        Generator g = new Generator(1200, 1000, 1000000, "20190629", false);

        File transactionFile = new File("data/transactions_" + "20190629" + ".data");

        assertTrue(transactionFile.exists());

        Compactionner.deleteDirectory(new File("data"));

    }

}

