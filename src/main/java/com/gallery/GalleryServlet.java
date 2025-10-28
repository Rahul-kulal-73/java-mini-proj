package com.gallery;

import java.io.IOException;
// Removed file system imports (Path, Paths, Files, Stream)
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Use JAVAX imports
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/gallery")
public class GalleryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // UPLOAD_DIR constant is no longer used in doGet for this test
    // private static final String UPLOAD_DIR = "uploaded_images";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- SIMPLIFIED LOGIC ---
        // Temporarily skip reading from the file system to ensure the servlet initializes.
        // Assume an empty list for now.
        List<String> imageNames = Collections.emptyList();
        // --- END SIMPLIFICATION ---

        // Pass the (empty) list of image names to the JSP
        request.setAttribute("imageNames", imageNames);

        // Forward using the relative path
        request.getRequestDispatcher("gallery.jsp").forward(request, response);
    }
}
