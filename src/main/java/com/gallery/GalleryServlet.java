package com.gallery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// *** USE JAVAX IMPORTS ***
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// **************************

@WebServlet("/gallery") // Annotation can stay, uses javax package
public class GalleryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Use simplified version first to guarantee startup
    // private static final String UPLOAD_DIR = "uploaded_images";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- Start with simplified logic ---
        List<String> imageNames = Collections.emptyList();
        // --- End simplification ---

        /* // --- Original file logic (Uncomment AFTER it loads once) ---
        String applicationPath = request.getServletContext().getRealPath("");
        Path uploadPath = null;
        List<String> imageNames = new ArrayList<>();

        // Add null check for applicationPath
        if (applicationPath != null && !applicationPath.isEmpty()) {
             uploadPath = Paths.get(applicationPath, UPLOAD_DIR);
             if (Files.exists(uploadPath) && Files.isDirectory(uploadPath)) {
                try (Stream<Path> files = Files.list(uploadPath)) {
                    imageNames = files
                        .filter(file -> Files.isRegularFile(file))
                        .map(file -> file.getFileName().toString())
                        .collect(Collectors.toList());
                    Collections.sort(imageNames);
                } catch (IOException e) {
                    System.err.println("Error reading upload directory: " + e.getMessage());
                    request.setAttribute("error", "Could not read gallery images.");
                }
            } else {
                 System.out.println("Upload directory does not exist yet: " + (uploadPath != null ? uploadPath.toString() : "Path is null"));
            }
        } else {
            System.err.println("Could not get real path for ServletContext.");
            request.setAttribute("error", "Server configuration error: Cannot determine application path.");
        }
        // --- End Original file logic --- */


        request.setAttribute("imageNames", imageNames);

        // Use relative path
        request.getRequestDispatcher("gallery.jsp").forward(request, response);
    }
}
