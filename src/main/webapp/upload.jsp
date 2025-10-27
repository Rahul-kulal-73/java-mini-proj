<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Image</title>
    <style>
        body { font-family: sans-serif; background-color: #f4f7f6; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .container { background-color: #ffffff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); width: 100%; max-width: 400px; text-align: center; }
        h1 { color: #333; margin-bottom: 20px; font-size: 1.5rem; }
        form { display: flex; flex-direction: column; gap: 15px; }
        input[type="file"] { 
            border: 1px solid #ccc; 
            padding: 10px; 
            border-radius: 8px; 
            background-color: #e9ecef; 
            cursor: pointer;
        }
        button { background-color: #007bff; color: white; padding: 12px; border: none; border-radius: 8px; cursor: pointer; font-size: 1rem; transition: background-color 0.3s ease; }
        button:hover { background-color: #0056b3; }
        .message { margin-top: 20px; color: green; font-weight: bold; }
        .error { color: red; font-weight: bold; }
        .back-link { margin-top: 15px; display: block; color: #007bff; text-decoration: none; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Upload a New Photo</h1>
        
        <% 
            // Display messages (success/error) passed via request attributes from the UploadServlet
            String message = (String) request.getAttribute("message");
            String error = (String) request.getAttribute("error");

            if (message != null && !message.isEmpty()) {
                out.println("<p class=\"message\">" + message + "</p>");
            }
            if (error != null && !error.isEmpty()) {
                out.println("<p class=\"error\">" + error + "</p>");
            }
        %>
        
        <form action="upload" method="post" enctype="multipart/form-data">
            <input type="file" name="imageFile" accept="image/*" required>
            <button type="submit">Upload Image</button>
        </form>

        <a href="gallery" class="back-link">View Gallery</a>
    </div>
</body>
</html>