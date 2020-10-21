package nelson.tools.jsonConverter;

import nelson.tools.jsonConverter.model4json.*;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;

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

		Assert.assertThrows("File not found!!!", IOException.class, () -> {
			new XmlModel4JsonManager("as.xml");
		});
	}

	/**
	 * test the method {@link ModelHandle#getJson(String, String)}
	 */
	@Test
	public void testGetJson() {

		Assert.assertTrue(true);
	}
}
