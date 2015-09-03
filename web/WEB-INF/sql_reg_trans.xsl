<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output
        method="xml"
        version="1.0"
        encoding="utf8"
        omit-xml-declaration="yes"
        />

  <xsl:param name="sql_id" select="''"/>

 <xsl:template match="/root">

    <xsl:for-each select="child::*">
        <xsl:if test="name()=$sql_id">
            <xsl:value-of select="child::text()" disable-output-escaping="yes" /> 
        </xsl:if>
    </xsl:for-each>
     
  </xsl:template>

</xsl:stylesheet>