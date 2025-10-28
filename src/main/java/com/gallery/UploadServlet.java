package com.gallery;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

// *** USE JAVAX IMPORTS ***
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
// **************************

@WebServlet("/upload") // Annotation can stay, uses javax package
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,     // 10MB
                 maxRequestSize = 1024 * 1024 * 50)  // 50MB
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String UPLOAD_DIR = "uploaded_images";

    /**
     * Handles GET requests to display the upload form.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Use relative path
        request.getRequestDispatcher("upload.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- Start with simplified logic ---
        try {
            Part filePart = request.getPart("imageFile"); // Still need this to get filename
            String fileName = getFileName(filePart);

            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("error", "No file selected for upload.");
                request.getRequestDispatcher("upload.jsp").forward(request, response);
                return;
            }

             fileName = sanitizeFileName(fileName); // Sanitize even if not saving

            // Simulate success without writing
             request.getSession().setAttribute("uploadMessage", "Image '" + fileName + "' (simulated upload) received!");
             response.sendRedirect("gallery"); // Redirect back to the GalleryServlet

        } catch (Exception e) {
            System.err.println("Processing upload failed (before file saving): " + e.getMessage());
            request.setAttribute("error", "Processing upload failed: " + e.getMessage());
            request.getRequestDispatcher("upload.jsp").forward(request, response);
        }
       // --- End simplification ---


        /* // --- Original file logic (Uncomment AFTER it loads once) ---
        String applicationPath = request.getServletContext().getRealPath("");
        Path uploadPath = null;

        // Add null check for applicationPath
         if (applicationPath != null && !applicationPath.isEmpty()) {
            uploadPath = Paths.get(applicationPath, UPLOAD_DIR);

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
         } else {
             System.err.println("Could not get real path for ServletContext. Cannot save file.");
             request.setAttribute("error", "Server configuration error: Cannot determine application path for saving.");
             request.getRequestDispatcher("upload.jsp").forward(request, response);
             return;
         }


        try {
            Part filePart = request.getPart("imageFile");
            String fileName = getFileName(filePart);

            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("error", "No file selected for upload.");
                request.getRequestDispatcher("upload.jsp").forward(request, response);
                return;
            }

            fileName = sanitizeFileName(fileName);

            // Save the file (only if uploadPath is not null)
            if (uploadPath != null) {
                try (InputStream fileContent = filePart.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                 request.getSession().setAttribute("uploadMessage", "Image '" + fileName + "' uploaded successfully!");
                 response.sendRedirect("gallery"); // Redirect back to the GalleryServlet
            } else {
                 // Handle case where path couldn't be determined (already sent error above)
                 // This part might be redundant if the check at the start handles it.
                  request.setAttribute("error", "Cannot save file due to path error.");
                  request.getRequestDispatcher("upload.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.err.println("Upload failed: " + e.getMessage());
            request.setAttribute("error", "Upload failed: " + e.getMessage());
            request.getRequestDispatcher("upload.jsp").forward(request, response);
        }
         // --- End Original file logic --- */
    }

    private String getFileName(Part part) {
        if (part == null) return null;
        String header = part.getHeader("content-disposition");
        if (header == null) return null;
        for (String content : header.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "unknown_file";
        return fileName.replaceAll("[\\\\/]", "").replaceAll("\\s+", "_");
    }
}
