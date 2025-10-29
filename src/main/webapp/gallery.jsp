<%-- These 'taglib' lines are necessary to use JSTL (the "c:" tags) --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Simple Photo Gallery</title>
    <style>
        /* Your excellent CSS is unchanged */
        body { font-family: sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; }
        .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; border-bottom: 2px solid #ccc; padding-bottom: 10px; }
        h1 { color: #333; font-size: 2rem; }
        .upload-button { background-color: #28a745; color: white; padding: 10px 20px; border: none; border-radius: 8px; text-decoration: none; font-size: 1rem; transition: background-color 0.3s ease; }
        .upload-button:hover { background-color: #1e7e34; }
        
        .gallery-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 20px;
        }
        .image-card {
            background-color: #fff;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            transition: transform 0.2s ease;
        }
        .image-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
        }
        .image-card img {
            width: 100%;
            height: 200px;
            object-fit: cover;
            display: block;
        }
        .no-images { text-align: center; color: #666; padding: 50px; font-size: 1.2rem; }
        .message { color: #2F6F23; background: #E6F7E3; padding: 15px; border-radius: 8px; text-align: center; font-weight: bold; margin-bottom: 20px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>My Simple Java Gallery</h1>
        
        <%-- 
          FIX 1: The link now points to the "/upload" servlet path,
          which correctly loads "upload.jsp". 
          We use the contextPath to make the link reliable.
        --%>
        <a href="${pageContext.request.contextPath}/upload" class="upload-button">Upload New Image</a>
    </div>

    <%-- 
      FIX 2 (Style): Replaced scriptlets with JSTL.
      This checks for the session message and removes it. 
    --%>
    <c:if test="${not empty sessionScope.uploadMessage}">
        <p class="message">${sessionScope.uploadMessage}</p>
        <c:remove var="uploadMessage" scope="session" />
    </c:if>

    <div class="gallery-grid">
        <%-- This loops through the imageNames list from the servlet --%>
        <c:forEach var="fileName" items="${imageNames}">
            <div class="image-card">
                <%-- 
                  FIX 3: The 'src' path is now absolute to your app.
                  This creates a reliable URL like:
                  /YourProjectName/uploaded_images/my-photo.jpg
                --%>
                <img src="${pageContext.request.contextPath}/uploaded_images/${fileName}" alt="${fileName}">
            </div>
        </c:forEach>
    </div>

    <%-- This 'c:if' block checks if the list was empty --%>
    <c:if test="${empty imageNames}">
        <p class="no-images">No images uploaded yet. Time to add some photos!</p>
    </c:if>

</body>
</html>
