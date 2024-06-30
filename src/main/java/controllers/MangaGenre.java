package controllers;

public class MangaGenre {
	public MangaGenre(String id, String genre_name) {
		super();
		this.id = id;
		this.genre_name = genre_name;
	}

	protected String id;
	protected String genre_name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGenre_name() {
		return genre_name;
	}

	public void setGenre_name(String genre_name) {
		this.genre_name = genre_name;
	}
}
