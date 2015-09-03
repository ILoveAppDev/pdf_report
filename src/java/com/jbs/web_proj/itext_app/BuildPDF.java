package com.jbs.web_proj.itext_app;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author xiangdong.li
 */
public class BuildPDF {

    protected void Build(
            String title_str,
            ResultSet rs,
            Document iDocument
    ) throws Exception {

        
        float max_col_len = 40;
        
        PdfPCell title = null;

        Font title_font1
                = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, new Color(0, 0, 255));

        Font title_font2
                = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new Color(255, 0, 0));

        Font title_font
                = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new Color(255, 255, 255));

        Font cont_font
                = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Font.NORMAL, new Color(0, 0, 0));

        PdfPTable t_title = new PdfPTable(1);
        
        
        t_title.setWidthPercentage(100f);
        t_title.setHorizontalAlignment(Element.ALIGN_CENTER);

        Rectangle border = new Rectangle(0f, 0f);

        border.setBorderWidthLeft(0);
        border.setBorderWidthBottom(0);
        border.setBorderWidthRight(0);
        border.setBorderWidthTop(0);
        border.setBorderColor(java.awt.Color.white);
        border.setBackgroundColor(java.awt.Color.white);

        title = new PdfPCell(new Phrase(title_str, title_font1));
        title.cloneNonPositionParameters(border);

        title.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        t_title.addCell(title);

        title = new PdfPCell(new Phrase(" ", title_font2));
        title.cloneNonPositionParameters(border);

        t_title.addCell(title);

        ArrayList colname = new ArrayList();

        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            String value = ((String) rs.getMetaData().getColumnLabel(i)).toLowerCase();

            colname.add(value);

        }

        
        // Get data
        ArrayList RowList = new ArrayList();

        while (rs.next()) {

            ArrayList ColMsgs = new ArrayList();

            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
               
                if (rs.getString(i) != null) {

                    String col_value_read = rs.getString(i);

                    ColMsgs.add(col_value_read.trim());
                                       
                } else {
                    ColMsgs.add("");
                }
            }

            RowList.add(ColMsgs);
        }

        ArrayList<Float> col_len = new ArrayList<Float>();
        
        if (RowList.size() > 0) {
        
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                
                String colname_value = ((String) rs.getMetaData().getColumnLabel(i)).toLowerCase();
                
                float len = 0;
                
                for (int j = 0; j < RowList.size(); j++) {
                
                    ArrayList row = (ArrayList) RowList.get(j);
                    
                    String col_val = (String)row.get(i-1);
                    
                    if ( col_val.length() > len )
                    {
                        len = col_val.length();
                    }
                    
                    if ( col_val.length() > max_col_len )
                    {
                        len = max_col_len;
                    }
                    
                }
                
                if ( len <= colname_value.length() ) 
                {
                    len = colname_value.length() + 2;
                }
                
                col_len.add( len );
                
            }
            
        }
        
        if ( col_len.size() > 0 )
        {
            float total_len = 0;
            
            for (int i = 0; i < col_len.size(); i++) {
                
                total_len = total_len + col_len.get(i);
                
            }
            
            for (int i = 0; i < col_len.size(); i++) {
                
                float new_len = (float) (100.0 * col_len.get(i) / total_len);
                
                col_len.set(i, new_len);
                
            }
            
            
        }
        
        
        
        PdfPTable t1 = new PdfPTable(colname.size());
        t1.setWidthPercentage(100f);

        if ( col_len.size() > 0 )
        {
            
            float[] floatArray = new float[col_len.size()];
            int i = 0;

            for (Float f : col_len) {
                floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
            }
            
            t1.setWidths( floatArray );
        }
        
        
        for (int i = 0; i < colname.size(); i++) {
            
            title = new PdfPCell(new Phrase(((String) colname.get(i)).replaceAll("_", " ").toUpperCase(), title_font));

            title.setBackgroundColor(java.awt.Color.black);
            title.setHorizontalAlignment(Element.ALIGN_CENTER);

            border.setBorderWidthLeft(0f);
            border.setBorderWidthBottom(0f);
            border.setBorderWidthRight(0f);
            border.setBorderWidthTop(0f);
            border.setBackgroundColor(java.awt.Color.black);

            title.cloneNonPositionParameters(border);
            title.setNoWrap(true);
            
            t1.addCell(title);

        }

        
        
        if (RowList.size() > 0) {
            
            for (int i = 0; i < RowList.size(); i++) {

                ArrayList row = (ArrayList) RowList.get(i);

                for (int j = 0; j < colname.size(); j++) {

  //                  System.out.println("(String) row.get(j): " + (String) row.get(j));
                    title = new PdfPCell(new Phrase((String) row.get(j), cont_font));

                    title.setBackgroundColor(java.awt.Color.white);
                    title.setHorizontalAlignment(Element.ALIGN_CENTER);

                    border.setBackgroundColor(java.awt.Color.white);
                    title.cloneNonPositionParameters(border);

                    t1.addCell(title);

                }

            }
        }

        iDocument.add(t_title);
        iDocument.add(t1);

    }

}
