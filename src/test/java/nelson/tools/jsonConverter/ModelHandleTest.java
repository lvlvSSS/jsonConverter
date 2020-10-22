package nelson.tools.jsonConverter;

import nelson.tools.jsonConverter.helper.JacksonUtils;
import nelson.tools.jsonConverter.helper.JsExecutor;
import nelson.tools.jsonConverter.model4json.*;

import org.dom4j.DocumentException;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ModelHandleTest {

	private String path;

	@Before
	public void init() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		path = loader.getResource("model4json.xml").getPath();
		File file = new File(path);
		Assert.assertTrue(file.exists());
	}

	/**
	 * test the method {@link ModelHandle#getJson(String, String)}
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	@Test
	public void testGetJson() throws IOException, DocumentException {
		// test the testPersons exists or not.
		XmlModel4JsonManager manager = new XmlModel4JsonManager(this.path);
		XmlModel model = manager.getModel("testPersons");
		Assert.assertNotNull(model);

		ModelHandle modelhandle = new ModelHandle(manager);
		String re = modelhandle.getJson("testPersons", JacksonUtils.toJson(getSource()));
		Assert.assertEquals(JacksonUtils.toJson(getTarget()), re);
	}

	private List<Map<String, Object>> getSource() {
		List<Map<String, Object>> persons = new ArrayList<Map<String, Object>>();
		Map<String, Object> person1 = new HashMap<String, Object>();
		person1.put("CurrentYear", 2020);
		person1.put("BirthYear", 1989);
		person1.put("Name", "Father");

		Map<String, Object> person2 = new HashMap<String, Object>();
		person2.put("CurrentYear", 2020);
		person2.put("BirthYear", 1990);
		person2.put("Name", "Mother");

		Map<String, Object> person3 = new HashMap<String, Object>();
		person3.put("CurrentYear", 2021);
		person3.put("BirthYear", 2020);
		person3.put("Name", "Boy");

		Map<String, Object> person4 = new HashMap<String, Object>();
		person4.put("CurrentYear", 2022);
		person4.put("BirthYear", 2020);
		person4.put("Name", "Girl");

		List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
		li.add(person3);
		li.add(person4);
		person1.put("Children", li);
		person2.put("Children", li);
		persons.add(person1);
		persons.add(person2);

		// System.out.println(JacksonUtils.toJson(persons));
		return persons;
	}

	private Map<String, Object> getTarget() {
		Map<String, Object> target = new HashMap<String, Object>();
		target.put("Token", "1111");

		List<Map<String, Object>> persons = new ArrayList<Map<String, Object>>();
		Map<String, Object> person1 = new HashMap<String, Object>();
		person1.put("Age", 31);
		person1.put("BirthYear", 1989);
		person1.put("Name", "Father");

		Map<String, Object> person2 = new HashMap<String, Object>();
		person2.put("Age", 30);
		person2.put("BirthYear", 1990);
		person2.put("Name", "Mother");

		Map<String, Object> person3 = new HashMap<String, Object>();
		person3.put("Age", 1);
		person3.put("BirthYear", 2020);
		person3.put("Name", "Boy");

		Map<String, Object> person4 = new HashMap<String, Object>();
		person4.put("Age", 2);
		person4.put("BirthYear", 2020);
		person4.put("Name", "Girl");

		List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
		li.add(person3);
		li.add(person4);
		person1.put("Children", li);
		person2.put("Children", li);
		persons.add(person1);
		persons.add(person2);
		target.put("Persons", persons);

		return target;
	}
}
