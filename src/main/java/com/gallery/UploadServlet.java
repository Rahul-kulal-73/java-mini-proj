package com.gallery;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

// Using the standard javax.* imports for Tomcat 9
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Handles file uploads.
 * - GET requests (/upload) show the upload.jsp form.
 * - POST requests (/upload) handle the file saving and redirect to the gallery.
 */
@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,     // 10MB
                 maxRequestSize = 1024 * 1024 * 50)  // 50MB
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * The name of the directory to store uploaded files.
     * This will be created *inside* the web application's deployment folder.
     */
    private static final String UPLOAD_DIR = "uploaded_images";

    /**
     * Handles GET requests by displaying the upload form.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Forward the request to the JSP page that contains the HTML form
        request.getRequestDispatcher("upload.jsp").forward(request, response);
    }

    /**
     * Handles POST requests by processing the uploaded file.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Get the real path to the web application's root directory
        // This is the *deployed* location on the server, e.g., .../tomcat/webapps/YourProjectName/
        String applicationPath = request.getServletContext().getRealPath("");
        
        // 2. Resolve the full path to the upload directory
        Path uploadPath = Paths.get(applicationPath, UPLOAD_DIR);

        // 3. Create the upload directory if it does not exist
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                System.err.println("Error creating upload directory: " + e.getMessage());
                request.setAttribute("error", "Server failed to create the necessary directory.");
                request.getRequestDispatcher("upload.jsp").forward(request, response);
                return; // Stop processing
            }
        }

        try {
            // 4. Get the file part from the request (must match the <input name="..."> in the form)
            Part filePart = request.getPart("imageFile"); 
            String fileName = getFileName(filePart);

            // 5. Validate that a file was actually selected
            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("error", "No file selected for upload.");
                request.getRequestDispatcher("upload.jsp").forward(request, response);
                return;
            }

            // 6. Sanitize the file name to prevent directory traversal issues
            fileName = sanitizeFileName(fileName);

            // 7. Save the file to the server
            try (InputStream fileContent = filePart.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // 8. Set a success message in the session
            request.getSession().setAttribute("uploadMessage", "Image '" + fileName + "' uploaded successfully!");
            
            // 9. Redirect back to the GalleryServlet to display the updated gallery
            // We use sendRedirect to follow the Post-Redirect-Get (PRG) pattern.
            response.sendRedirect("gallery");

        } catch (Exception e) {
            System.err.println("Upload failed: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
            request.setAttribute("error", "Upload failed: " + e.getMessage());
            request.getRequestDispatcher("upload.jsp").forward(request, response);
        }
    }

    /**
     * Extracts the file name from the Content-Disposition header of a Part.
     */
    private String getFileName(Part part) {
        if (part == null) return null;
        String header = part.getHeader("content-disposition");
        if (header == null) return null;
        for (String content : header.split(";")) {
            if (content.trim().startsWith("filename")) {
                // Return the file name, stripping quotes
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    /**
     * Sanitizes a file name by removing path separators and replacing spaces.
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "unknown_file";
        // Remove any path separators (forward/backward slashes) and replace spaces with underscores
        return fileName.replaceAll("[\\\\/]", "").replaceAll("\\s+", "_");
    }
}
