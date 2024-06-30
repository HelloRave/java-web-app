package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MangaCollectionServlet
 */
@WebServlet("/MangaCollectionServlet")
public class MangaCollectionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String jdbcURL = "jdbc:mysql://localhost:3306/java-web-app";
	private String jdbcUsername = "root";
	private String jdbcPassword = "P@ssw0rd";

	private static final String SELECT_ALL_RECORDS = "SELECT * FROM MangaRecords"
			+ " JOIN GenreRecords ON MangaRecords.id = GenreRecords.record_id"
			+ " JOIN MangaGenre ON MangaGenre.id = GenreRecords.genre_id"
			+ " JOIN MangaAuthors ON MangaAuthors.id = MangaRecords.author;";
	private static final String SELECT_ALL_GENRE = "SELECT * FROM MangaGenre";
	private static final String SELECT_AUTHOR_BY_NAME = "SELECT * FROM MangaAuthors WHERE author_name=?";
	private static final String SELECT_RECORDS_BY_ID = "SELECT * FROM MangaRecords"
			+ " JOIN GenreRecords ON MangaRecords.id = GenreRecords.record_id"
			+ " JOIN MangaGenre ON MangaGenre.id = GenreRecords.genre_id"
			+ " JOIN MangaAuthors ON MangaAuthors.id = MangaRecords.author" + " WHERE MangaRecords.id=?;";
	private static final String INSERT_AUTHOR_SQL = "INSERT INTO MangaAuthors (`author_name`) VALUES (?);";
	private static final String INSERT_RECORDS_SQL = "INSERT INTO MangaRecords"
			+ "  (`title`, `author`, `ongoing`, `chapters`, `published`, `anime_adaptation`, `description`, `url`) VALUES "
			+ " (?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String INSERT_GENRE_SQL = "INSERT INTO GenreRecords" + "  (`record_id`, `genre_id`) VALUES "
			+ " (?, ?);";
	private static final String UPDATE_RECORDS_SQL = "UPDATE MangaRecords"
			+ " SET title = ?, author = ?, ongoing = ?, chapters = ?, published = ?, anime_adaptation = ?, description = ?, url = ?"
			+ " WHERE id = ?;";
	private static final String DELETE_RECORDS_SQL = "DELETE from MangaRecords where id = ?;";
	private static final String DELETE_GENRE_SQL = "DELETE from GenreRecords where record_id = ?;";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MangaCollectionServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getServletPath();
		try {
			switch (action) {
			case "/MangaCollectionServlet/delete":
				deleteManga(request, response);
				break;
			case "/MangaCollectionServlet/edit":
				showEditForm(request, response);
				break;
			case "/MangaCollectionServlet/update":
				updateManga(request, response);
				break;
			case "/MangaCollectionServlet/dashboard":
				listMangaCollections(request, response);
				break;
			case "/MangaCollectionServlet/add":
				showAddForm(request, response);
				break;
			case "/MangaCollectionServlet/insert":
				addNewManga(request, response);
				break;
			}
		} catch (SQLException ex) {
			throw new ServletException(ex);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private void listMangaCollections(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException, ServletException {
		List<MangaCollection> mangas = new ArrayList<>();
		List<String> titleList = new ArrayList<>();

		try {
			Connection connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_RECORDS);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String authorId = rs.getString("author");
				String authorName = rs.getString("author_name");
				boolean ongoing = rs.getBoolean("ongoing");
				int chapters = rs.getInt("chapters");
				LocalDate published = rs.getDate("published").toLocalDate();
				boolean anime_adaptation = rs.getBoolean("anime_adaptation");
				String description = rs.getString("description");
				String url = rs.getString("url");
				String genreId = rs.getString("genre_id");
				String genre = rs.getString("genre_name");
				if (!titleList.contains(title)) {
					titleList.add(title);
					MangaAuthor author = new MangaAuthor(authorId, authorName);
					MangaCollection collection = new MangaCollection(id, title, author, ongoing, chapters, published,
							anime_adaptation, description, url);
					MangaGenre mangaGenre = new MangaGenre(genreId, genre);
					collection.genre.add(mangaGenre);
					mangas.add(collection);
				} else {
					for (int i = 0; i < mangas.size(); i++) {
						if (mangas.get(i).title.equals(title)) {
							MangaGenre mangaGenre = new MangaGenre(genreId, genre);
							mangas.get(i).genre.add(mangaGenre);
						}
					}
				}
			}
			request.setAttribute("listMangaCollection", mangas);
			request.getRequestDispatcher("/mangaManagement.jsp").forward(request, response);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void addNewManga(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException, ServletException {
		String title = request.getParameter("title");
		String author = request.getParameter("author");
		String published = request.getParameter("published");
		String chapters = request.getParameter("chapters");
		boolean ongoing = Boolean.parseBoolean(request.getParameter("ongoing"));
		boolean animeAdaptation = Boolean.parseBoolean(request.getParameter("anime-adaptation"));
		String[] genre = request.getParameterValues("genre");
		String description = request.getParameter("description");
		String url = request.getParameter("url");

		try {
			Connection connection = getConnection();
			PreparedStatement findAuthor = connection.prepareStatement(SELECT_AUTHOR_BY_NAME);
			findAuthor.setString(1, author);
			ResultSet rs = findAuthor.executeQuery();
			String authorId;

			if (!rs.isBeforeFirst()) {
				PreparedStatement psAuthorTable = connection.prepareStatement(INSERT_AUTHOR_SQL,
						Statement.RETURN_GENERATED_KEYS);
				psAuthorTable.setString(1, author);
				int newAuthorRow = psAuthorTable.executeUpdate();

				if (newAuthorRow == 0) {
					throw new SQLException("Creating author failed, no rows affected.");
				}

				ResultSet isNewAuthor = psAuthorTable.getGeneratedKeys();
				if (isNewAuthor.next()) {
					authorId = isNewAuthor.getString(1);
				} else {
					throw new SQLException("Creating manga failed, no ID obtained.");
				}
			} else {
				rs.next();
				authorId = rs.getString(1);
			}

			PreparedStatement insertNewRecord = connection.prepareStatement(INSERT_RECORDS_SQL,
					Statement.RETURN_GENERATED_KEYS);
			insertNewRecord.setString(1, title);
			insertNewRecord.setString(2, authorId);
			insertNewRecord.setBoolean(3, ongoing);
			insertNewRecord.setString(4, chapters);
			insertNewRecord.setString(5, published);
			insertNewRecord.setBoolean(6, animeAdaptation);
			insertNewRecord.setString(7, description);
			insertNewRecord.setString(8, url);

			int newRow = insertNewRecord.executeUpdate();

			if (newRow == 0) {
				throw new SQLException("Creating manga failed, no rows affected.");
			}

			ResultSet isNewRecord = insertNewRecord.getGeneratedKeys();
			if (isNewRecord.next()) {
				String newRecordId = isNewRecord.getString(1);
				for (int i = 0; i < genre.length; i++) {
					PreparedStatement insertGenreRecords = connection.prepareStatement(INSERT_GENRE_SQL);
					insertGenreRecords.setString(1, newRecordId);
					insertGenreRecords.setString(2, genre[i]);
					insertGenreRecords.executeUpdate();
				}
			} else {
				throw new SQLException("Creating manga failed, no ID obtained.");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		response.sendRedirect("http://localhost:8090/java-web-app/MangaCollectionServlet/dashboard");
	}

	private void showAddForm(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException, ServletException {
		List<MangaGenre> genreList = new ArrayList<MangaGenre>();

		try {
			Connection connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_GENRE);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				String id = rs.getString("id");
				String genre = rs.getString("genre_name");
				MangaGenre mangaGenre = new MangaGenre(id, genre);
				genreList.add(mangaGenre);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		request.setAttribute("genreList", genreList);
		request.getRequestDispatcher("/addManga.jsp").forward(request, response);
	}

	private void showEditForm(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ServletException, IOException {
		String id = request.getParameter("id");
		List<String> titleList = new ArrayList<>();
		MangaCollection selectedManga = new MangaCollection(0, "", new MangaAuthor("", ""), false, 0, null, false, "",
				"");
		List<MangaGenre> genreList = new ArrayList<MangaGenre>();
		List<String> selectedGenre = new ArrayList<>();

		try {
			Connection connection = getConnection();
			PreparedStatement getRecordById = connection.prepareStatement(SELECT_RECORDS_BY_ID);
			getRecordById.setString(1, id);
			ResultSet isRecord = getRecordById.executeQuery();

			while (isRecord.next()) {
				int recordId = isRecord.getInt("id");
				String title = isRecord.getString("title");
				String authorId = isRecord.getString("author");
				String authorName = isRecord.getString("author_name");
				boolean ongoing = isRecord.getBoolean("ongoing");
				int chapters = isRecord.getInt("chapters");
				LocalDate published = isRecord.getDate("published").toLocalDate();
				boolean anime_adaptation = isRecord.getBoolean("anime_adaptation");
				String description = isRecord.getString("description");
				String url = isRecord.getString("url");
				String genreId = isRecord.getString("genre_id");
				String genre = isRecord.getString("genre_name");
				if (!titleList.contains(title)) {
					titleList.add(title);
					MangaAuthor author = new MangaAuthor(authorId, authorName);
					selectedManga = new MangaCollection(recordId, title, author, ongoing, chapters, published,
							anime_adaptation, description, url);
					MangaGenre mangaGenre = new MangaGenre(genreId, genre);
					selectedManga.genre.add(mangaGenre);
				} else {
					MangaGenre mangaGenre = new MangaGenre(genreId, genre);
					selectedManga.genre.add(mangaGenre);
				}
			}

			for (int i = 0; i < selectedManga.getGenre().size(); i++) {
				selectedGenre.add(selectedManga.getGenre().get(i).getId());
			}

			PreparedStatement getAllGenre = connection.prepareStatement(SELECT_ALL_GENRE);
			ResultSet genreResult = getAllGenre.executeQuery();

			while (genreResult.next()) {
				String genreId = genreResult.getString("id");
				String genre = genreResult.getString("genre_name");
				MangaGenre mangaGenre = new MangaGenre(genreId, genre);
				genreList.add(mangaGenre);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		request.setAttribute("id", id);
		request.setAttribute("genreList", genreList);
		request.setAttribute("selectedGenre", selectedGenre);
		request.setAttribute("selectedManga", selectedManga);
		request.getRequestDispatcher("/editManga.jsp").forward(request, response);
	}

	private void updateManga(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ServletException, IOException {
		String id = request.getParameter("id");
		String title = request.getParameter("title");
		String author = request.getParameter("author");
		String published = request.getParameter("published");
		String chapters = request.getParameter("chapters");
		boolean ongoing = Boolean.parseBoolean(request.getParameter("ongoing"));
		boolean animeAdaptation = Boolean.parseBoolean(request.getParameter("anime-adaptation"));
		String[] genre = request.getParameterValues("genre");
		String description = request.getParameter("description");
		String url = request.getParameter("url");

		try {
			Connection connection = getConnection();
			PreparedStatement findAuthor = connection.prepareStatement(SELECT_AUTHOR_BY_NAME);
			findAuthor.setString(1, author);
			ResultSet rs = findAuthor.executeQuery();
			String authorId;

			if (!rs.isBeforeFirst()) {
				PreparedStatement psAuthorTable = connection.prepareStatement(INSERT_AUTHOR_SQL,
						Statement.RETURN_GENERATED_KEYS);
				psAuthorTable.setString(1, author);
				int newAuthorRow = psAuthorTable.executeUpdate();

				if (newAuthorRow == 0) {
					throw new SQLException("Creating author failed, no rows affected.");
				}

				ResultSet isNewAuthor = psAuthorTable.getGeneratedKeys();
				if (isNewAuthor.next()) {
					authorId = isNewAuthor.getString(1);
				} else {
					throw new SQLException("Creating manga failed, no ID obtained.");
				}
			} else {
				rs.next();
				authorId = rs.getString(1);
			}

			PreparedStatement updateRecord = connection.prepareStatement(UPDATE_RECORDS_SQL,
					Statement.RETURN_GENERATED_KEYS);
			updateRecord.setString(1, title);
			updateRecord.setString(2, authorId);
			updateRecord.setBoolean(3, ongoing);
			updateRecord.setString(4, chapters);
			updateRecord.setString(5, published);
			updateRecord.setBoolean(6, animeAdaptation);
			updateRecord.setString(7, description);
			updateRecord.setString(8, url);
			updateRecord.setString(9, id);

			int newRow = updateRecord.executeUpdate();

			if (newRow == 0) {
				throw new SQLException("Updating manga failed, no rows affected.");
			}

			PreparedStatement deleteGenreRecord = connection.prepareStatement(DELETE_GENRE_SQL);
			deleteGenreRecord.setString(1, id);
			int deletedRow = deleteGenreRecord.executeUpdate();

			if (deletedRow == 0) {
				throw new SQLException("Deleting genre_records failed, no rows affected.");
			}

			for (int i = 0; i < genre.length; i++) {
				PreparedStatement insertGenreRecords = connection.prepareStatement(INSERT_GENRE_SQL);
				insertGenreRecords.setString(1, id);
				insertGenreRecords.setString(2, genre[i]);
				insertGenreRecords.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		response.sendRedirect("http://localhost:8090/java-web-app/MangaCollectionServlet/dashboard");
	}

	private void deleteManga(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ServletException, IOException {
		String mangaId = request.getParameter("id");
		try (Connection connection = getConnection();) {
			PreparedStatement deleteGenreRecord = connection.prepareStatement(DELETE_GENRE_SQL);
			deleteGenreRecord.setString(1, mangaId);
			int deletedRow = deleteGenreRecord.executeUpdate();

			if (deletedRow == 0) {
				throw new SQLException("Deleting manga failed, no rows affected.");
			}

			PreparedStatement deleteRecord = connection.prepareStatement(DELETE_RECORDS_SQL);
			deleteRecord.setString(1, mangaId);
			int deletedRecord = deleteRecord.executeUpdate();

			if (deletedRecord == 0) {
				throw new SQLException("Deleting manga failed, no rows affected.");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		response.sendRedirect("http://localhost:8090/java-web-app/MangaCollectionServlet/dashboard");
	}

	protected Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return connection;
	}
}
