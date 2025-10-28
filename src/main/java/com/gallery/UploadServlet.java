package com.gallery;

import java.io.IOException;
import java.io.InputStream; // Keep for getPart()
// Removed file system imports (Path, Paths, Files, StandardCopyOption)

// Use JAVAX imports
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part; // Keep for getPart()

@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,     // 10MB
                 maxRequestSize = 1024 * 1024 * 50)  // 50MB
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // UPLOAD_DIR constant is temporarily unused
    private static final String UPLOAD_DIR = "uploaded_images";

    /**
     * Handles GET requests to display the upload form.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("upload.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- SIMPLIFIED LOGIC ---
        // Temporarily skip all file system operations

        // String applicationPath = request.getServletContext().getRealPath("");
        // Path uploadPath = Paths.get(applicationPath, UPLOAD_DIR);

        // if (!Files.exists(uploadPath)) {
        //     try {
        //         Files.createDirectories(uploadPath);
        //     } catch (IOException e) {
        //         System.err.println("Error creating upload directory: " + e.getMessage());
        //         request.setAttribute("error", "Server failed to create the necessary directory.");
        //         request.getRequestDispatcher("upload.jsp").forward(request, response);
        //         return;
        //     }
        // }

        try {
            Part filePart = request.getPart("imageFile");
            String fileName = getFileName(filePart);

            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("error", "No file selected for upload.");
                request.getRequestDispatcher("upload.jsp").forward(request, response);
                return;
            }

            fileName = sanitizeFileName(fileName);

            // Temporarily skip saving the file
            // try (InputStream fileContent = filePart.getInputStream()) {
            //     Path filePath = uploadPath.resolve(fileName);
            //     Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
            // }

            // --- END SIMPLIFICATION ---

            // Still redirect, but pretend the upload worked
            request.getSession().setAttribute("uploadMessage", "Image '" + fileName + "' (simulated upload) received!");
            response.sendRedirect("gallery"); // Redirect back to the GalleryServlet

        } catch (Exception e) { // Catch potential errors from getPart etc.
            System.err.println("Processing upload failed (before file saving): " + e.getMessage());
            request.setAttribute("error", "Processing upload failed: " + e.getMessage());
            request.getRequestDispatcher("upload.jsp").forward(request, response);
        }
    }

    private String getFileName(Part part) {
        if (part == null) return null; // Add null check
        String header = part.getHeader("content-disposition");
        if (header == null) return null; // Add null check
        for (String content : header.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private String sanitizeFileName(String fileName) {
         if (fileName == null) return "unknown_file"; // Add null check
        // Remove path components (e.g., ../) and replace spaces with underscores
        return fileName.replaceAll("[\\\\/]", "").replaceAll("\\s+", "_");
    }
}
