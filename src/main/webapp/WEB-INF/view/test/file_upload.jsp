<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>file upload test</title>
</head>
<body>
	<div>
		<form method="post" action="/test/fileUpload/upload" enctype="multipart/form-data">
			<input type="file" name="upload_file" />
			<input type="submit" value="전송" />
		</form>
	</div>
</body>
</html>