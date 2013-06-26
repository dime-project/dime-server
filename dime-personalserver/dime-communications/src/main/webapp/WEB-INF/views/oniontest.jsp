<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE HTML>
<html>
<head>
<title>Title of the document</title>
</head>
<body>
	<h1>OnionCat</h1>
	<div>
		<form action="refresh" method="get">
			<input type="submit" value="Refresh" />
		</form>
		<form action="start" method="post">
			<input type="submit" value="Start tor" />
		</form>
		<form action="stop" method="post">
			<input type="submit" value="Stop tor" />
		</form>
	</div>

	<div>
		<form action="" method="post">
			<input type="submit" value="Add HiddenService" />
		</form>
	</div>

	<div>
		<c:forEach var="address" items="${addresses}">
			<p>
				<c:out value="${address}" />
			</p>
		</c:forEach>
	</div>

</body>
</html>
