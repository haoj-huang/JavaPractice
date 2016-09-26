package jsonparser;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

public class JsonParserTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParse() {
		String strJson = "{\"students\":[{\"name\":\"Jack\",\"age\":12}, {\"name\":\"Vista\",\"age\":23}, {\"name\":\"Kaka\",\"age\":22}, {\"name\":\"Hony\",\"age\":31}]}";
		try {
			Map<String,Object> jsonmap = JsonParser.parse(strJson);
			Set<Entry<String,Object>> set = jsonmap.entrySet();
			Iterator<Entry<String,Object>> it = set.iterator();
			while(it.hasNext()){
				Entry<String,Object> e = it.next();
				System.out.println(e.getKey()+":"+e.getValue());
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String js1 = "\"programmers\": [{\"firstName\": \"Brett\",\"lastName\": \"McLaughlin\",\"email\": \"aaaa\"}, {\"firstName\": \"Jason\",\"lastName\": \"Hunter\",\"email\": \"bbbb\"}, {\"firstName\": \"Elliotte\",\"lastName\": \"Harold\",\"email\": \"cccc\"}]";
		String js2 = "\"authors\": [{\"firstName\": \"Isaac\",\"lastName\": \"Asimov\",\"genre\": \"sciencefiction\"}, {\"firstName\": \"Tad\",\"lastName\": \"Williams\",\"genre\": \"fantasy\"}, {\"firstName\": \"Frank\",\"lastName\": \"Peretti\",\"genre\": \"christianfiction\"}]";
		String js3 = "\"musicians\": [{\"firstName\": \"Eric\",\"lastName\": \"Clapton\",\"instrument\": \"guitar\"}, {\"firstName\": \"Sergei\",\"lastName\": \"Rachmaninoff\",\"instrument\": \"piano\"}]";
		try {
			Map<String,Object> jsonmap = JsonParser.parse("{"+js1+","+js2+","+js3+"}");
			Set<Entry<String,Object>> set = jsonmap.entrySet();
			Iterator<Entry<String,Object>> it = set.iterator();
			while(it.hasNext()){
				Entry<String,Object> e = it.next();
				System.out.println(e.getKey()+":"+e.getValue());
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
