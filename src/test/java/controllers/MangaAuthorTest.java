/**
 * 
 */
package controllers;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
/**
 * 
 */
class MangaAuthorTest {
	private MangaAuthor author;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
		author = new MangaAuthor("1", "name");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link controllers.MangaAuthor#getId()}.
	 */
	@Test
	void testGetId() {
		String id = author.getId();
		assertEquals("1", id);
	}

	/**
	 * Test method for {@link controllers.MangaAuthor#setId(java.lang.String)}.
	 */
	@Test
	void testSetId() {
		author.setId("2");
		String id = author.getId();
		assertEquals("2", id);
	}

	/**
	 * Test method for {@link controllers.MangaAuthor#getAuthor_name()}.
	 */
	@Test
	void testGetAuthor_name() {
		String name = author.getAuthor_name();
		assertEquals("name", name);
	}

	/**
	 * Test method for {@link controllers.MangaAuthor#setAuthor_name(java.lang.String)}.
	 */
	@Test
	void testSetAuthor_name() {
		author.setAuthor_name("newName");
		String name = author.getAuthor_name();
		assertEquals("newName", name);
	}

}
