/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jbs.web_proj.itext_app;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.beans.Statement;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author xiangdong.li
 */
public class MainServlet extends HttpServlet {

    private String AppRoot_Path = "";

    public void init(ServletConfig sc) throws ServletException {

        super.init(sc);

        ServletContext myServletContext = sc.getServletContext();
        this.AppRoot_Path = myServletContext.getRealPath("/");

    }

    public void service(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(true);

        synchronized (session) {

            String year = ((HttpServletRequest) request).getParameter("year");
            String month = ((HttpServletRequest) request).getParameter("month");
            String location = ((HttpServletRequest) request).getParameter("location");

            ArrayList param_list = null;

            if (year == null
                    || month == null
                    || location == null
                    || year.compareToIgnoreCase("") == 0
                    || month.compareToIgnoreCase("") == 0
                    || location.compareToIgnoreCase("") == 0) {
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            Document iDocument = null;
            PdfWriter writer = null;
            Connection con = null;
            Helper myHelper = null;

            String file_name = "";

            try {

                myHelper = new Helper();
                BuildPDF myBuildPDF = new BuildPDF();

                XSLTRenderHelper myXSLTRenderHelper = new XSLTRenderHelper();

                file_name
                        = myXSLTRenderHelper.GetValueFromXMLProperty(
                                AppRoot_Path + "/WEB-INF/sql_reg_trans.xsl",
                                AppRoot_Path + "/WEB-INF/params.xml",
                                "sql_id",
                                "Output_File"
                        );

                iDocument
                        = myHelper.Init_Document(
                                myXSLTRenderHelper,
                                this.AppRoot_Path,
                                file_name
                        );

                PdfPTable t_blank = myHelper.t_blank;
                PdfPCell blank_cell = myHelper.blank_cell;

                writer = myHelper.writer;

                con = myHelper.Init_DB(myXSLTRenderHelper, this.AppRoot_Path);

                param_list = new ArrayList();
                param_list.add(year);
                param_list.add(month);
                param_list.add(location);

                iDocument
                        = myHelper.BuildTable(
                                myXSLTRenderHelper,
                                myBuildPDF,
                                con,
                                param_list,
                                AppRoot_Path,
                                "Test Data for " + year + "-" + month + ", " + location,
                                "Test_Query"
                        );

                t_blank.addCell(blank_cell);
                t_blank.addCell(blank_cell);
                t_blank.addCell(blank_cell);

                iDocument.add(t_blank);
               

            } catch (DocumentException ex) {

                //     } catch (SQLException ex) {
                //         ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {

                if (iDocument != null) {
                    iDocument.close();
                }

                if (writer != null) {
                    writer.close();
                }

                try {

                    if (myHelper != null && myHelper.stmt != null) {
                        myHelper.stmt.close();
                    }
                    if (myHelper != null && myHelper.rs != null) {
                        myHelper.rs.close();
                    }
                    if (con != null) {
                        con.close();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }

     //       request.getRequestDispatcher("result.jsp").forward(request, response);
            String contextPath = getServletContext().getRealPath(File.separator);
            File pdfFile = new File(contextPath + file_name);

            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=" + file_name);
            response.setContentLength((int) pdfFile.length());

            FileInputStream fileInputStream = new FileInputStream(pdfFile);
            OutputStream responseOutputStream = response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }

            return;

        }

    }
}
