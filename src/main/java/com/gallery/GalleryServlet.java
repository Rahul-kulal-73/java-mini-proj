package com.gallery;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

// *** USE JAVAX IMPORTS ***
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/gallery")
public class GalleryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Use simplified logic to prevent file system errors on startup
        List<String> imageNames = Collections.emptyList();
        request.setAttribute("imageNames", imageNames);
        
        // Use relative path (no leading "/") to find the JSP
        request.getRequestDispatcher("gallery.jsp").forward(request, response);
    }
}
