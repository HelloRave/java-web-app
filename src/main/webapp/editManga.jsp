<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<title>Add New Manga</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">
</head>
<body>
	<h2 class='text-center py-3'>Edit Manga</h2>
	<div class="container">
		<form action="update" method="post">
			<input type="hidden" name="id"
				value="<c:out
				value='${id}' />" />
			<fieldset class="form-group">
				<label class="font-weight-bold">Title</label> <input type="text"
					class="form-control" name="title"
					value="<c:out
value='${selectedManga.title}' />"
					required="required">
			</fieldset>
			<fieldset class="form-group">
				<label class="font-weight-bold">Author</label> <input type="text"
					class="form-control" name="author"
					value="<c:out
value='${selectedManga.author.author_name}' />"
					required="required">
			</fieldset>
			<fieldset class="form-group">
				<label class="font-weight-bold">Published</label> <input type="date"
					class="form-control" name="published"
					value="<c:out
value='${selectedManga.published}' />"
					required="required">
			</fieldset>
			<fieldset class="form-group">
				<label class="font-weight-bold">Chapters</label> <input
					type="number" min="1" class="form-control" name="chapters"
					value="<c:out
value='${selectedManga.chapters}' />"
					required="required">
			</fieldset>
			<fieldset class="form-group">
				<p class="font-weight-bold">Ongoing</p>
				<div class="form-check form-check-inline">
					<input class="form-check-input" type="radio" id="ongoing-true"
						name="ongoing" value="true"
						${selectedManga.ongoing.equals(true) ? 'checked' : null}>
					<label class="form-check-label" for="ongoing-true">Yes</label>
				</div>
				<div class="form-check form-check-inline">
					<input class="form-check-input" type="radio" id="ongoing-false"
						name="ongoing" value="false"
						${selectedManga.ongoing.equals(false) ? 'checked' : null}>
					<label class="form-check-label" for="ongoing-false">No</label>
				</div>
			</fieldset>
			<fieldset class="form-group">
				<p class="font-weight-bold">Anime Adaptation</p>
				<div class="form-check form-check-inline">
					<input class="form-check-input" type="radio" id="anime-true"
						name="anime-adaptation" value="true"
						${selectedManga.anime_adaptation.equals(true) ? 'checked' : null}>
					<label class="form-check-label" for="anime-true">Yes</label>
				</div>
				<div class="form-check form-check-inline">
					<input class="form-check-input" type="radio" id="anime-false"
						name="anime-adaptation" value="false"
						${selectedManga.anime_adaptation.equals(false) ? "checked" : null}>
					<label class="form-check-label" for="anime-false">No</label>
				</div>
			</fieldset>
			<fieldset class="form-group">
				<label class="font-weight-bold">Genre (Select one or more of
					the following)</label> <select name="genre" class="form-control" multiple>
					<c:forEach var="genre" items="${genreList}">
						<option value="${genre.id}" class="text-capitalize"
							${selectedGenre.contains(genre.id) ? "selected" : null }>${genre.genre_name}</option>
					</c:forEach>
				</select>
			</fieldset>
			<fieldset class="form-group">
				<label class="font-weight-bold">Description</label>
				<textarea name="description" class="form-control" rows="5">${selectedManga.description}</textarea>
			</fieldset>
			<fieldset class="form-group">
				<label class="font-weight-bold">Photo</label> <input type="url"
					class="form-control" name="url"
					value="<c:out
value='${selectedManga.url}' />" required="required">
			</fieldset>
			<a href="dashboard" class="btn btn-secondary">Back</a>
			<button type="submit" class="btn btn-success">Update</button>
		</form>
	</div>
</body>
</html>