<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Simple Photo Gallery</title>
    <style>
        body { font-family: sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; }
        .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; border-bottom: 2px solid #ccc; padding-bottom: 10px; }
        h1 { color: #333; font-size: 2rem; }
        .upload-button { background-color: #28a745; color: white; padding: 10px 20px; border: none; border-radius: 8px; text-decoration: none; font-size: 1rem; transition: background-color 0.3s ease; }
        .upload-button:hover { background-color: #1e7e34; }
        
        /* Responsive grid layout for the images */
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
            height: 200px; /* Fixed height for consistent thumbnails */
            object-fit: cover;
            display: block;
        }
        .no-images { text-align: center; color: #666; padding: 50px; font-size: 1.2rem; }
    </style>
</head>
<body>
    <div class="header">
        <h1>My Simple Java Gallery</h1>
        <a href="upload.jsp" class="upload-button">Upload New Image</a>
    </div>

    <% 
        // Retrieve the list of image file names from the GalleryServlet
        List<String> imageNames = (List<String>) request.getAttribute("imageNames");
        
        // Retrieve any upload message from the session (set by UploadServlet)
        String uploadMessage = (String) session.getAttribute("uploadMessage");
        if (uploadMessage != null) {
            out.println("<p class='message' style='color: green; text-align: center;'><strong>" + uploadMessage + "</strong></p>");
            session.removeAttribute("uploadMessage"); // Remove to prevent reappearance on refresh
        }

        if (imageNames == null || imageNames.isEmpty()) {
            out.println("<p class='no-images'>No images uploaded yet. Time to add some photos!</p>");
        } else {
            out.println("<div class='gallery-grid'>");
            for (String fileName : imageNames) {
                // The URL path must match the folder created by the UploadServlet
                // This path maps to /src/main/webapp/uploaded_images/ on the server
                String imageUrl = "uploaded_images/" + fileName;
    %>
            <div class="image-card">
                <img src="<%= imageUrl %>" alt="<%= fileName %>">
            </div>
    <%
            }
            out.println("</div>");
        }
    %>

</body>
</html>