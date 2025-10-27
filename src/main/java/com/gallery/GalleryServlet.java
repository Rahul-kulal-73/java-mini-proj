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

// *** UPDATED TO JAKARTA EE IMPORTS ***
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// ************************************

@WebServlet("/gallery")
public class GalleryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static final String UPLOAD_DIR = "uploaded_images";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String applicationPath = request.getServletContext().getRealPath("");
        Path uploadPath = Paths.get(applicationPath, UPLOAD_DIR);
        
        List<String> imageNames = new ArrayList<>();

        if (Files.exists(uploadPath) && Files.isDirectory(uploadPath)) {
            try (Stream<Path> files = Files.list(uploadPath)) {
                // Filter for regular files and get their names
                imageNames = files
                    .filter(file -> Files.isRegularFile(file))
                    .map(file -> file.getFileName().toString())
                    .collect(Collectors.toList());
                
                // Sort the files for a consistent display order
                Collections.sort(imageNames);
                
            } catch (IOException e) {
                System.err.println("Error reading upload directory: " + e.getMessage());
                request.setAttribute("error", "Could not read gallery images.");
            }
        } 

        // Pass the list of image names to the JSP
        request.setAttribute("imageNames", imageNames);

        // FIX APPLIED: Removed the leading slash (/) to resolve the dispatcher 404 error.
        request.getRequestDispatcher("gallery.jsp").forward(request, response);
    }
}
