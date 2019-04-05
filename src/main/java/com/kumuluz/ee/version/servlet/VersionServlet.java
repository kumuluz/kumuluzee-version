package com.kumuluz.ee.version.servlet;

import com.kumuluz.ee.version.VersionExtension;
import com.kumuluz.ee.version.pojo.VersionPojo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class VersionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();

        VersionPojo versionPojo = VersionExtension.getVersionPojo();
        writer.println(versionPojo.toJSON());

        writer.close();
    }
}
