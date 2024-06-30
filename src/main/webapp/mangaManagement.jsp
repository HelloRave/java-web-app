<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<title>Manga Collection</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">
</head>
<body>
	<h2 class='text-center py-3'>Manga Collection</h2>
	<div class='container'>
		<div class='row mb-2'>
			<a href="add"
				class="btn btn-success">Add New Manga</a>
		</div>
		<div class='row row-cols-4'>
			<c:forEach var='manga' items='${listMangaCollection}'>
				<div class="card mr-2 mb-2" style="width: 18rem;">
					<img src="${manga.url}" class="card-img-top"
						style="height: 25rem; object-fit: cover" alt="manga">
					<div class="card-body">
						<h5 class='card-title'>${manga.title}</h5>
						<p>By: ${manga.author.author_name}</p>
						<p>Published: ${manga.published}</p>
						<c:forEach var='genre' items='${manga.genre}'>
							<span class="badge badge-primary mb-2">${genre.genre_name}</span>
						</c:forEach>
						<p class="card-text">${manga.description}</p>
					</div>
					<div class='card-footer'>
						<a href='edit?id=<c:out value='${manga.id}'/>' class='card-link'>Edit manga</a>
						<a href='delete?id=<c:out value='${manga.id}'/>' class='card-link'>Delete manga</a>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</body>
</html>