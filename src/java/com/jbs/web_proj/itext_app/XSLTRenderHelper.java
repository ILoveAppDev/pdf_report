package com.jbs.web_proj.itext_app;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
   * This class is used to parse XML-format properties 
   * 
   * Example:
   * 
   *  XSLTRenderHelper myXSLTRenderHelper = new XSLTRenderHelper();
   * 
   *  sql_string
   *         = myXSLTRenderHelper.GetValueFromXMLProperty(
   *                 "./litens_conf/sql_reg_trans.xsl",
   *                 "./litens_conf/params.xml",
   *                 "sql_id",
   *                 "IQuery_Str" );
   * 
   * Example of sql_reg_trans.xsl, don't modify it
   * 
   * <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   *
   *    <xsl:output
   *     method="xml"
   *     version="1.0"
   *     encoding="utf8"
   *     omit-xml-declaration="yes"
   *     />
   *
   *    <xsl:param name="sql_id" select="''"/>
   *
   *    <xsl:template match="/root">
   *
   *        <xsl:for-each select="child::*">
   *           <xsl:if test="name()=$sql_id">
   *              <xsl:value-of select="child::text()" disable-output-escaping="yes" /> 
   *           </xsl:if>
   *        </xsl:for-each>
   *  
   *    </xsl:template>
   *
   * </xsl:stylesheet>
   *
   * Example of params.xml
   * 
   * <root>
   * 
   *   <IQuery_Str>
   *     
   *     select * 
   *     from [Parts] 
   *     where 
   *     ( 
   *         [Title Block.Part Type] Equal To 'M-Assembly'
   *         or [Title Block.Part Type] Equal To 'M-Component'  
   *         or [Title Block.Part Type] Equal To 'M-Only'
   *         or [Title Block.Part Type] Equal To 'M-Packaging'
   *         or [Title Block.Part Type] Equal To 'M-SubAssembly'
   *
   *     )
   *     			
   *     and [Page Two.Manufacturing Sites] Contains Any 'LAC' 
   *     and [Manufacturers.External Part #] Does Not Contains 'non'
   *     and [Manufacturers.External Part #] Does Not Contains 'Non' 
   *     and ( 
   *         [Manufacturers.External Part #] Contains 'bonded'
   *         or [Manufacturers.External Part #] Contains 'Bonded'
   *     )
   *     
   *   </IQuery_Str>
   * 
   *  
   * </root>
   * 
   * @param xsltTransFilePath 
   *    This is the internal file used in xsl transformation.
   *    Copy the above example of sql_reg_trans.xsl.
   *    Don't modify it
   * 
   * @param xmlSourceFilePath 
   *    This is the xml file to store property values
   *
   * @param param_name 
   *    This is the internal tag name, usually is "sql_id", don't change it
   * 
   * @param param_value 
   *    This is the name of the property you want to retrieve.
   *  
   */
public class XSLTRenderHelper {

    public String GetValueFromXMLProperty(
            String xsltTransFilePath,
            String xmlSourceFilePath,
            String param_name,
            String param_value
    ) throws IOException {

        String Msg = null;

        try {

            if (xmlSourceFilePath == null || xsltTransFilePath.compareToIgnoreCase("") == 0) {
                Msg = "xmlSourceFilePath == null, or, xsltTransFilePath == null";
                throw new IOException(Msg);
            }

            StringTokenizer st = new StringTokenizer(xmlSourceFilePath, "|");

            String xml_str = "";

            while (st.hasMoreTokens()) {

                String individual_file_value = st.nextToken().trim();

                File xmlSourceFile = new File(individual_file_value);

                if (xmlSourceFile == null) {
                    Msg = "xmlSourceFile == null";
                    throw new IOException(Msg);

                }

                // write the JDOM data to a StringWriter
                StringWriter sw_out = new StringWriter();

                Msg = "In XSLTRenderHelper -> GetValueFromXMLProperty: "
                        + "\n. param_name -> " + param_name + "; param_value -> "
                        + param_value
                        + "\n. xmlSourceFilePath -> "
                        + individual_file_value
                        + "\n. xsltTransFilePath -> "
                        + xsltTransFilePath;

                Transformer trans = StylesheetCache.newTransformer(xsltTransFilePath);

                if (trans == null) {
                    Msg = "trans == null";
                    throw new IOException(Msg);

                }

                // -DproxyHost=websensenew.litens -DproxyPort=3128
                trans.setParameter(param_name, param_value);

                trans.transform(new StreamSource(xmlSourceFile),
                        new StreamResult(sw_out));

                sw_out.close();

                xml_str = sw_out.toString().trim();

                if (xml_str != null && xml_str.trim().compareToIgnoreCase("") != 0) {
                    break;
                }

            }

            return xml_str;

        } catch (Exception ex) {

            Msg = "In XSLTRenderHelper->GetValueFromXMLProperty: " + ex.getMessage();

            StackTraceElement[] stack = ex.getStackTrace();
            String theTrace = "";
            for (StackTraceElement line : stack) {
                theTrace += line.toString() + "\r\n";
            }

            ex.printStackTrace();

            throw new IOException(Msg);

        }

    }

    public XSLTRenderHelper() {
    }

}
