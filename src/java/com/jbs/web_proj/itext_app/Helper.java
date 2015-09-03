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
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xiangdong.li
 */
public class Helper {

    protected PdfPTable t_blank;
    protected PdfPCell blank_cell;
    protected PdfWriter writer;
    protected Rectangle border_blank;
    protected Document iDocument;

    protected PreparedStatement stmt;
    protected ResultSet rs;

    public Helper() {

    }

    protected Connection Init_DB(
            XSLTRenderHelper myXSLTRenderHelper,
            String AppRoot_Path
    ) throws IOException {

        String driver_classname
                = myXSLTRenderHelper.GetValueFromXMLProperty(
                        AppRoot_Path + "/WEB-INF/sql_reg_trans.xsl",
                        AppRoot_Path + "/WEB-INF/params.xml",
                        "sql_id",
                        "db_classname"
                );

        String server_url
                = myXSLTRenderHelper.GetValueFromXMLProperty(
                        AppRoot_Path + "/WEB-INF/sql_reg_trans.xsl",
                        AppRoot_Path + "/WEB-INF/params.xml",
                        "sql_id",
                        "db_server_url"
                );

        String login_id
                = myXSLTRenderHelper.GetValueFromXMLProperty(
                        AppRoot_Path + "/WEB-INF/sql_reg_trans.xsl",
                        AppRoot_Path + "/WEB-INF/params.xml",
                        "sql_id",
                        "db_login_id"
                );

        String login_pw
                = myXSLTRenderHelper.GetValueFromXMLProperty(
                        AppRoot_Path + "/WEB-INF/sql_reg_trans.xsl",
                        AppRoot_Path + "/WEB-INF/params.xml",
                        "sql_id",
                        "db_login_pw"
                );

        try {
            Class.forName(driver_classname);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        Connection con = null;

        try {

            con = DriverManager.getConnection(
                    server_url, login_id,
                    login_pw);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return con;

    }

    protected Document Init_Document(
            XSLTRenderHelper myXSLTRenderHelper,
            String AppRoot_Path,
            String file_name
    ) throws DocumentException, IOException {

        Rectangle actual_page_size = PageSize.A4.rotate();
        border_blank = new Rectangle(0f, 0f);

        String page_margin_left = "10";
        String page_margin_right = "10";
        String page_margin_top = "10";
        String page_margin_bottom = "10";

        Float margin_left = new Float(page_margin_left).floatValue();
        Float margin_right = new Float(page_margin_right).floatValue();
        Float margin_top = new Float(page_margin_top).floatValue();
        Float margin_bottom = new Float(page_margin_bottom).floatValue();

        iDocument = new com.lowagie.text.Document(
                actual_page_size,
                margin_left,
                margin_right,
                margin_top,
                margin_bottom
        );

        border_blank = new Rectangle(0f, 0f);

        writer
                = PdfWriter.getInstance(
                        iDocument,
                        new FileOutputStream(AppRoot_Path + file_name)
                );

        File file = new File(file_name);

        if (file.exists()) {
            file.delete();
        }

        writer
                = PdfWriter.getInstance(
                        iDocument,
                        new FileOutputStream(file_name)
                );

        writer.setEncryption(null, null,
                PdfWriter.AllowCopy, PdfWriter.STANDARD_ENCRYPTION_128);

        // This works on iText version: itext-2.0.5.jar but I download and install itext-2.1.7.jar
        writer.setViewerPreferences(
                PdfWriter.HideWindowUI
                | PdfWriter.HideMenubar
                | PdfWriter.HideToolbar
        );

        iDocument.open();

        t_blank = new PdfPTable(1);

        blank_cell
                = new PdfPCell(
                        new Phrase(
                                " ",
                                FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new Color(255, 255, 255))
                        )
                );

        border_blank.setBorderWidthLeft(0);
        border_blank.setBorderWidthBottom(0);
        border_blank.setBorderWidthRight(0);
        border_blank.setBorderWidthTop(0);
        border_blank.setBorderColor(java.awt.Color.white);
        border_blank.setBackgroundColor(java.awt.Color.white);

        blank_cell.cloneNonPositionParameters(border_blank);

        t_blank.addCell(blank_cell);

        Image img
                = Image.getInstance(
                        AppRoot_Path
                        + myXSLTRenderHelper.GetValueFromXMLProperty(
                                AppRoot_Path + "/WEB-INF/sql_reg_trans.xsl",
                                AppRoot_Path + "/WEB-INF/params.xml",
                                "sql_id",
                                "header_logo"
                        )
                );

        iDocument.add(img);
        iDocument.add(t_blank);
        iDocument.add(t_blank);

        return iDocument;

    }

    protected Document BuildTable(
            XSLTRenderHelper myXSLTRenderHelper,
            BuildPDF myBuildPDF,
            Connection con,
            ArrayList param_list,
            String AppRoot_Path,
            String View_Title,
            String sql_id_str
    ) throws IOException, SQLException, Exception {

        stmt = null;

        String sql_str
                = myXSLTRenderHelper.GetValueFromXMLProperty(
                        AppRoot_Path + "/WEB-INF/sql_reg_trans.xsl",
                        AppRoot_Path + "/WEB-INF/params.xml",
                        "sql_id",
                        sql_id_str
                );

        stmt = con.prepareStatement(sql_str);

        if (param_list.size() > 0) {

            for (int i = 0; i < param_list.size(); i++) {

                stmt.setString(i + 1, (String) param_list.get(i));

            }

        }

        rs = stmt.executeQuery();

        myBuildPDF.Build(
                View_Title,
                rs,
                iDocument
        );

        return iDocument;

    }

    
}
