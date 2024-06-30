package controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MangaCollection {
	public MangaCollection(int id, String title, MangaAuthor author, boolean ongoing, int chapters, LocalDate published,
			boolean anime_adaptation, String description, String url) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.ongoing = ongoing;
		this.chapters = chapters;
		this.published = published;
		this.anime_adaptation = anime_adaptation;
		this.description = description;
		this.url = url;
	}

	protected int id;
	protected String title;
	protected MangaAuthor author;
	protected boolean ongoing;
	protected int chapters;
	protected LocalDate published;
	protected boolean anime_adaptation;
	protected String description;
	protected String url;
	protected List<MangaGenre> genre = new ArrayList<>();

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public MangaAuthor getAuthor() {
		return author;
	}
	public void setAuthor(MangaAuthor author) {
		this.author = author;
	}
	public boolean isOngoing() {
		return ongoing;
	}
	public void setOngoing(boolean ongoing) {
		this.ongoing = ongoing;
	}
	public int getChapters() {
		return chapters;
	}
	public void setChapters(int chapters) {
		this.chapters = chapters;
	}
	public LocalDate getPublished() {
		return published;
	}
	public void setPublished(LocalDate published) {
		this.published = published;
	}
	public boolean isAnime_adaptation() {
		return anime_adaptation;
	}
	public void setAnime_adaptation(boolean anime_adaptation) {
		this.anime_adaptation = anime_adaptation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<MangaGenre> getGenre() {
		return genre;
	}
	
}
