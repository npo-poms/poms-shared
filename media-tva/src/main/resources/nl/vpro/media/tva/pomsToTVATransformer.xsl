<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="urn:vpro:media:2009"
  xpath-default-namespace="urn:vpro:media:2009"
  xmlns:tva="urn:tva:metadata:2004"
  xmlns:mpeg7="urn:mpeg:mpeg7:schema:2001"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  exclude-result-prefixes="mpeg7 xsi">

  <!--
  XML that can produce the TVA XML back from poms data.

  This can probably be made more complete. For not it is only meant to create test-data
  in the integrations tests.
  -->

  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <!-- root: map mediaInformation -> tva:TVAMain -->
  <xsl:template match="/mediaInformation">
    <tva:TVAMain xmlns:tva="urn:tva:metadata:2004" xmlns:mpeg7="urn:mpeg:mpeg7:schema:2001">
      <!-- ProgramDescription with ProgramInformationTable and ProgramLocationTable -->
      <tva:ProgramDescription>
        <tva:ProgramInformationTable>
          <!-- Map each poms program -> tva:ProgramInformation -->
          <xsl:for-each select="programTable/program">
            <tva:ProgramInformation>
              <!-- programId from first crid child or mid attribute -->
              <xsl:attribute name="programId">
                <xsl:choose>
                  <xsl:when test="crid[1] and normalize-space(crid[1]) != ''">
                    <xsl:value-of select="normalize-space(crid[1])"/>
                  </xsl:when>
                  <xsl:when test="@mid">
                    <xsl:value-of select="@mid"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>generated-{@position}</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>

              <tva:BasicDescription>
                <!-- main title -->
                <xsl:for-each select="title[@type='MAIN' and @owner = 'MIS']">
                  <tva:Title>
                    <xsl:attribute name="type">main</xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </tva:Title>
                </xsl:for-each>

                <!-- original title -->
                <xsl:for-each select="title[@type='ORIGINAL' and @owner = 'MIS']">
                  <tva:Title>
                    <xsl:attribute name="type">translatedtitle</xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </tva:Title>
                </xsl:for-each>

                <!-- descriptions -->
                <xsl:for-each select="description[@owner = 'MIS']">
                  <tva:Synopsis>
                    <xsl:attribute name="length">
                      <xsl:choose>
                        <xsl:when test="@type='SHORT'">short</xsl:when>
                        <xsl:when test="@type='LONG'">long</xsl:when>
                        <xsl:otherwise>medium</xsl:otherwise>
                      </xsl:choose>
                    </xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </tva:Synopsis>
                </xsl:for-each>

                <!-- other identifiers: map poProgID / poSeriesID -->
                <xsl:for-each select="poProgID">
                  <tva:OtherIdentifier>
                    <xsl:attribute name="type">ProductID</xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </tva:OtherIdentifier>
                </xsl:for-each>
                <xsl:for-each select="poSeriesID">
                  <tva:OtherIdentifier>
                    <xsl:attribute name="type">SeriesID</xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </tva:OtherIdentifier>
                </xsl:for-each>

                <!-- simple genre mapping: create tva:Genre entries using poms genre ids -->
                <xsl:for-each select="genre">
                  <tva:Genre>
                    <xsl:attribute name="href">
                      <xsl:value-of select="concat('urn:tva:metadata:cs:2004:', normalize-space(.))"/>
                    </xsl:attribute>
                    <tva:Name><xsl:value-of select="normalize-space(.)"/></tva:Name>
                  </tva:Genre>
                </xsl:for-each>

              </tva:BasicDescription>
            </tva:ProgramInformation>
          </xsl:for-each>
        </tva:ProgramInformationTable>

        <!-- ProgramLocationTable / Schedule: aggregate schedules from poms:schedule -->
        <tva:ProgramLocationTable>
          <xsl:for-each select="/mediaInformation/schedule">
            <tva:Schedule>
              <!-- map attributes -->
              <xsl:if test="@start"><xsl:attribute name="start"><xsl:value-of select="@start"/></xsl:attribute></xsl:if>
              <xsl:if test="@stop"><xsl:attribute name="end"><xsl:value-of select="@stop"/></xsl:attribute></xsl:if>
              <xsl:if test="@channel"><xsl:attribute name="serviceIDRef"><xsl:value-of select="@channel"/></xsl:attribute></xsl:if>


              <xsl:for-each select="scheduleEvent">
                <xsl:variable name="mid"><xsl:value-of select="@midRef" /></xsl:variable>
                <xsl:variable name="crid"><xsl:value-of select="/mediaInformation/programTable/program[@mid=$mid]/crid[1]" /></xsl:variable>
              <!-- each scheduleEvent -->
                <tva:ScheduleEvent>
                  <tva:Program crid="{$crid}" />

                  <!-- start/stop/duration -->
                  <xsl:if test="start"><tva:PublishedStartTime><xsl:value-of select="start"/></tva:PublishedStartTime></xsl:if>
                  <xsl:if test="duration"><tva:PublishedDuration><xsl:value-of select="duration"/></tva:PublishedDuration></xsl:if>

                  <!-- identifiers inside ScheduleEvent -->
                  <xsl:for-each select="poProgID">
                    <tva:OtherIdentifier>
                      <xsl:attribute name="type">ProductID</xsl:attribute>
                      <xsl:value-of select="normalize-space(.)"/>
                    </tva:OtherIdentifier>
                  </xsl:for-each>
                  <xsl:for-each select="poSeriesID">
                    <tva:OtherIdentifier>
                      <xsl:attribute name="type">SeriesID</xsl:attribute>
                      <xsl:value-of select="normalize-space(.)"/>
                    </tva:OtherIdentifier>
                  </xsl:for-each>

                </tva:ScheduleEvent>
              </xsl:for-each>

            </tva:Schedule>
          </xsl:for-each>
        </tva:ProgramLocationTable>
      </tva:ProgramDescription>
    </tva:TVAMain>
  </xsl:template>

  <!-- identity fallback for nodes not explicitly mapped (keeps simple text children) -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
