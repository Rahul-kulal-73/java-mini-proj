package com.gallery;

import java.io.IOException;
// *** USE JAVAX IMPORTS ***
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests to display the upload form.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Use relative path (no leading "/") to find the JSP
        request.getRequestDispatcher("upload.jsp").forward(request, response);
    }

    /**
     * Handles POST requests to simulate file upload.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Use simplified logic to prevent file system errors
        try {
            Part filePart = request.getPart("imageFile");
            String fileName = getFileName(filePart);

            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("error", "No file selected for upload.");
                request.getRequestDispatcher("upload.jsp").forward(request, response);
                return;
            }
            
            // Simulate success without writing to disk
            request.getSession().setAttribute("uploadMessage", "Image '" + sanitizeFileName(fileName) + "' (simulated) received!");
            response.sendRedirect("gallery"); // Redirect back to the GalleryServlet

        } catch (Exception e) {
            request.setAttribute("error", "Processing upload failed: " + e.getMessage());
            request.getRequestDispatcher("upload.jsp").forward(request, response);
        }
    }

    /**
     * Extracts file name from HTTP header content-disposition
     */
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

    /**
     * Simple sanitation to remove path components and replace spaces.
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "unknown_file";
        return fileName.replaceAll("[\\\\/]", "").replaceAll("\\s+", "_");
    }
}
