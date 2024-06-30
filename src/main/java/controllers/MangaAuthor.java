package controllers;

public class MangaAuthor {
	public MangaAuthor(String id, String author_name) {
		super();
		this.id = id;
		this.author_name = author_name;
	}

	protected String id;
	protected String author_name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor_name() {
		return author_name;
	}

	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}
}
