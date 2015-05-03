package cat.tv3.eng.rec.recomana.lupa.test;

import org.junit.After;
import org.junit.Before;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class LupaBaseIT3st {

public static JedisPool pool;

String	redisHost;
int		redisPort;
String	freelingHost;
int		freelingPort;
String	language;

public LupaBaseIT3st() {

	super();
}

@Before
public void establishTestConfiguration() throws NumberFormatException {

	redisHost = System.getProperty("redis_host");		
	redisPort = Integer.parseInt(System.getProperty("redis_port"));   
	freelingHost = System.getProperty("freeling_host");		
	freelingPort = Integer.parseInt(System.getProperty("freeling_port"));
	language = System.getProperty("language");
	
	boolean verbose = false;
	String verboseOption = System.getProperty("verbose_tests");
	if (verboseOption!=null && verboseOption.equals("true")) {
	verbose = true;
	}
	if (verbose) {
    	 System.err.println("\t*** TEST DETAILS [\n"+
		 			"\t\t redisHost="+redisHost+"\n"+
		 			"\t\t redisPort="+redisPort+"\n"+
		 			"\t\t freelingHost="+freelingHost+"\n"+
		 			"\t\t freelingPort="+freelingPort+"\n"+
		 			"\t\t language="+language+"\n"+
		 			"\t ]\n"
    			 );
     }

    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxActive(1);
    poolConfig.setMaxIdle(1);
    pool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort);

}


@After
public void teardown() {
	// FIXME: no closing of test connections
}


}
