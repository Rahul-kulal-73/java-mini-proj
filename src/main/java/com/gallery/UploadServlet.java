package com.gallery;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

// Mandatory annotation for handling file uploads (multipart/form-data)
@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,     // 10MB
                 maxRequestSize = 1024 * 1024 * 50)  // 50MB
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Directory relative to the web application root where files are saved
    private static final String UPLOAD_DIR = "uploaded_images";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get the absolute path to the web application root
        String applicationPath = request.getServletContext().getRealPath("");
        Path uploadPath = Paths.get(applicationPath, UPLOAD_DIR);
        
        // Ensure the upload directory exists
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                System.err.println("Error creating upload directory: " + e.getMessage());
                request.setAttribute("error", "Server failed to create the necessary directory.");
                request.getRequestDispatcher("upload.jsp").forward(request, response);
                return;
            }
        }

        try {
            // Get the file part named "imageFile"
            Part filePart = request.getPart("imageFile");
            String fileName = getFileName(filePart);
            
            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("error", "No file selected for upload.");
                request.getRequestDispatcher("upload.jsp").forward(request, response);
                return;
            }
            
            fileName = sanitizeFileName(fileName);

            // Save the file
            try (InputStream fileContent = filePart.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                // Copy the input stream to the target file path, replacing if it exists
                Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Set a success message in the session and redirect to the gallery
            request.getSession().setAttribute("uploadMessage", "Image '" + fileName + "' uploaded successfully!");
            response.sendRedirect("gallery");

        } catch (Exception e) {
            System.err.println("Upload failed: " + e.getMessage());
            request.setAttribute("error", "Upload failed: " + e.getMessage());
            request.getRequestDispatcher("upload.jsp").forward(request, response);
        }
    }

    /**
     * Extracts file name from HTTP header content-disposition
     */
    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                // Return the filename, removing quotes
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    /**
     * Simple sanitation to remove path components and replace spaces.
     */
    private String sanitizeFileName(String fileName) {
        // Remove path components (e.g., ../) and replace spaces with underscores
        return fileName.replaceAll("[\\\\/]", "").replaceAll("\\s+", "_");
    }
}