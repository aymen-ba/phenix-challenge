
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class GeneratorTest {


    @Test
    public void testGenerateStoreIds() throws Exception {

        Generator g = new Generator(2, 5, 10, "20190629",true);

        List<String> ids = Calculator.getAllStoresIds("data", Main.REGEX_UUID_DATE, Main.REGEX_UUID, Main.REGEX_DATE, "20190629");

        assertEquals(ids.size(), 2);

        Compactionner.deleteDirectory(new File("data"));
    }


    @Test
    public void testgenerateTransactionsDay() {

        Generator g = new Generator(20, 100, 1000, "20190629", false);

        File transactionFile = new File("data/transactions_" + "20190629" + ".data");

        assertTrue(transactionFile.exists());

        Compactionner.deleteDirectory(new File("data"));

    }

}

