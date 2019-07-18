package aa_it;

import com.snc.glide.it.ITEnvironment;
import com.snc.glide.it.util.AA_SeleniumSetup;

/**
 * This is a standard setup for an IT project
 * 
 * It does install a glide instance (from the latest glide-dist build), restores
 * a zboot (from the latest glide-db-dump) and starts it.
 * 
 * Alternatively, you can use the glide.test.url system/maven property, in which
 * case this will do nothing and the tests will run against the URL you
 * specified.
 * 
 * This runs in the maven integration-test phase (you can tell from the IT
 * suffix), and since those run in alphabetical order the aa_it.AA_Setup make
 * sure it's the first test to run.
 * 
 * This example extends AA_SeleniumSetup which does extra magic to make sure the
 * selenium tests can also run on headless machines (jenkins). If no test in
 * your project is using selenium, you can extend AA_Setup instead.
 * 
 * If you want to debug tests in this test project via your IDE, you will first
 * need to execute this test on your local system via the Maven command line in
 * this test project's home directory //glide-test/remote-update-set-test. The
 * command is > mvn clean install -P it -Dit.test=AA_SetupIT.
 * 
 * @see ITEnvironment#TEST_URL
 */
public class AA_SetupIT extends AA_SeleniumSetup {
	public AA_SetupIT() {
		super("test-triaging");
	}

}