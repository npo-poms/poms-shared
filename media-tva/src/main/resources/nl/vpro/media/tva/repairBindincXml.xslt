<xsl:stylesheet version="1.0"
                xmlns:tva="urn:tva:metadata:2004"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
  Can be used to preprocess TV-anytime xmls as received by bindinc to adapt country codes to actual ISO-3166 ones.
  This can be used before using tvaTransformer.xsl. The POMS domain object requires standard compliant country codes.
  -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="tva:ProductionLocation">
    <xsl:copy>
      <xsl:attribute name="code">
        <xsl:choose>
          <xsl:when test="@code = 'EA'">
            <xsl:text>DDDE</xsl:text>
          </xsl:when>
          <xsl:when test="@code = 'CS'">
            <xsl:text>CSHH</xsl:text>
          </xsl:when>
          <xsl:when test="@code = 'RS'">
            <xsl:text>CS</xsl:text>
          </xsl:when>
          <xsl:when test="@code = 'SU'">
            <xsl:text>SUHH</xsl:text>
          </xsl:when>
          <xsl:when test="@code = 'YU'">
            <xsl:text>YUCS</xsl:text>
          </xsl:when>
          <xsl:when test="@code = 'ZR'">
            <xsl:text>ZRCD</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@code" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*" />
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
