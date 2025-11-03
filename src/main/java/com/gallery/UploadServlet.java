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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Use relative path
        request.getRequestDispatcher("upload.jsp").forward(request, response);
    }

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
            
            // Simulate success
            request.getSession().setAttribute("uploadMessage", "Image '" + sanitizeFileName(fileName) + "' (simulated) received!");
            response.sendRedirect("gallery");

        } catch (Exception e) {
            request.setAttribute("error", "Processing upload failed: " + e.getMessage());
            request.getRequestDispatcher("upload.jsp").forward(request, response);
        }
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
