<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:media="urn:vpro:media:2009"
  xpath-default-namespace="urn:vpro:media:2009"
  xmlns="urn:tva:metadata:2004"
  xmlns:mpeg7="urn:mpeg:mpeg7:schema:2001"
  exclude-result-prefixes="media">

  <!--
  XML that can produce the TVA XML back from poms data.

  This can probably be made more complete. For not it is only meant to create test-data
  in the integrations tests.
  -->

  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:param name="personExternalIdPrefix" select="'whatson:'" />


  <!-- root: map mediaInformation -> tva:TVAMain -->
  <xsl:template match="/mediaInformation">
    <TVAMain xmlns="urn:tva:metadata:2004" xmlns:mpeg7="urn:mpeg:mpeg7:schema:2001">
      <!-- ProgramDescription with ProgramInformationTable and ProgramLocationTable -->
      <ProgramDescription>
        <ProgramInformationTable>
          <!-- Map each poms program -> tva:ProgramInformation -->
          <xsl:for-each select="programTable/program">
            <ProgramInformation>
              <!-- programId from first crid child or mid attribute -->
              <xsl:attribute name="programId">
                <xsl:choose>
                  <xsl:when test="crid[1] and normalize-space(crid[1]) != ''">
                    <xsl:value-of select="normalize-space(crid[1])"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>generated-{@position}</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>


              <BasicDescription>
                <!-- main title -->
                <xsl:for-each select="title[@type='MAIN' and @owner = 'MIS']">
                  <Title>
                    <xsl:attribute name="type">main</xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </Title>
                </xsl:for-each>
                <xsl:for-each select="title[@type='SUB' and @owner = 'MIS']">
                  <Title>
                    <xsl:attribute name="type">episodeTitle</xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </Title>
                </xsl:for-each>
                <xsl:for-each select="episodeOf[1]">
                  <xsl:for-each select="memberOf[1]">
                    <xsl:for-each select="/mediaInformation/groupTable/group[@mid=current()/@midRef]">
                      <Title>
                        <xsl:attribute name="type">parentSeriesTitle</xsl:attribute>
                        <xsl:value-of select="normalize-space(title[@type='MAIN' and @owner = 'MIS'])"/>
                      </Title>
                    </xsl:for-each>
                  </xsl:for-each>
                </xsl:for-each>

                <!-- original title -->
                <xsl:for-each select="title[@type='ORIGINAL' and @owner = 'MIS']">
                  <Title>
                    <xsl:attribute name="type">translatedtitle</xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </Title>
                </xsl:for-each>

                <!-- descriptions -->
                <xsl:for-each select="description[@owner = 'MIS']">
                  <Synopsis>
                    <xsl:attribute name="length">
                      <xsl:choose>
                        <xsl:when test="@type='SHORT'">short</xsl:when>
                        <xsl:when test="@type='LONG'">long</xsl:when>
                        <xsl:otherwise>medium</xsl:otherwise>
                      </xsl:choose>
                    </xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </Synopsis>
                </xsl:for-each>

                <!-- other identifiers: map poProgID / poSeriesID -->
                <xsl:for-each select="poProgID">
                  <OtherIdentifier>
                    <xsl:attribute name="type">ProductID</xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </OtherIdentifier>
                </xsl:for-each>
                <xsl:for-each select="poSeriesID">
                  <OtherIdentifier>
                    <xsl:attribute name="type">SeriesID</xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                  </OtherIdentifier>
                </xsl:for-each>

                <!-- simple genre mapping: create tva:Genre entries using poms genre ids -->
                <xsl:for-each select="genre">
                  <Genre>
                    <xsl:attribute name="href">
                      <xsl:value-of select="concat('urn:tva:metadata:cs:2004:', normalize-space(.))"/>
                    </xsl:attribute>
                    <Name><xsl:value-of select="normalize-space(.)"/></Name>
                  </Genre>
                </xsl:for-each>
                <CreditsList>
                  <xsl:for-each select="credits/person[@externalId != '']">
                    <CreditsItem>
                      <xsl:attribute name="role">
                        <xsl:call-template name="roles">
                          <xsl:with-param name="id">
                            <xsl:value-of select="@role"/>
                          </xsl:with-param>
                        </xsl:call-template>
                      </xsl:attribute>
                      <PersonNameIDRef ref="{substring(@externalId, string-length($personExternalIdPrefix) + 1)}"/>
                    </CreditsItem>
                  </xsl:for-each>
                </CreditsList>


              </BasicDescription>
            </ProgramInformation>
          </xsl:for-each>
        </ProgramInformationTable>
        <GroupInformationTable /> <!-- seems useful, but unused -? -->

        <!-- ProgramLocationTable / Schedule: aggregate schedules from poms:schedule -->
        <ProgramLocationTable>
          <xsl:for-each select="/mediaInformation/schedule">
            <Schedule>
              <!-- map attributes -->
              <xsl:if test="@start"><xsl:attribute name="start"><xsl:value-of select="@start"/></xsl:attribute></xsl:if>
              <xsl:if test="@stop"><xsl:attribute name="end"><xsl:value-of select="@stop"/></xsl:attribute></xsl:if>
              <xsl:if test="@channel"><xsl:attribute name="serviceIDRef"><xsl:value-of select="@channel"/></xsl:attribute></xsl:if>


              <xsl:for-each select="scheduleEvent">
                <xsl:variable name="mid"><xsl:value-of select="@midRef" /></xsl:variable>
                <xsl:variable name="crid"><xsl:value-of select="/mediaInformation/programTable/program[@mid=$mid]/crid[1]" /></xsl:variable>

              <!-- each scheduleEvent -->
                <ScheduleEvent>
                  <Program crid="{$crid}" />

                  <!-- start/stop/duration -->
                  <xsl:if test="start"><PublishedStartTime><xsl:value-of select="start"/></PublishedStartTime></xsl:if>
                  <xsl:if test="duration"><PublishedDuration><xsl:value-of select="duration"/></PublishedDuration></xsl:if>

                  <!-- identifiers inside ScheduleEvent -->
                  <xsl:for-each select="poProgID">
                    <OtherIdentifier>
                      <xsl:attribute name="type">ProductID</xsl:attribute>
                      <xsl:value-of select="normalize-space(.)"/>
                    </OtherIdentifier>
                  </xsl:for-each>
                  <xsl:for-each select="poSeriesID">
                    <OtherIdentifier>
                      <xsl:attribute name="type">SeriesID</xsl:attribute>
                      <xsl:value-of select="normalize-space(.)"/>
                    </OtherIdentifier>
                  </xsl:for-each>
                  <xsl:for-each select="/mediaInformation/programTable/program[@mid=$mid]/episodeOf[1]">
                    <OtherIdentifier>
                      <xsl:attribute name="type">SeriesID</xsl:attribute>
                      <xsl:value-of select="normalize-space(@midRef)"/>
                    </OtherIdentifier>
                    <xsl:for-each select="memberOf[1]">
                      <OtherIdentifier>
                        <xsl:attribute name="type">ParentSeriesID</xsl:attribute>
                        <xsl:value-of select="normalize-space(@midRef)"/>
                      </OtherIdentifier>
                    </xsl:for-each>
                  </xsl:for-each>
                </ScheduleEvent>
              </xsl:for-each>
            </Schedule>
          </xsl:for-each>
        </ProgramLocationTable>

        <CreditsInformationTable>
          <xsl:for-each-group select="/mediaInformation/programTable/program/credits/person" group-by="@externalId">
            <xsl:if test="starts-with(@externalId, $personExternalIdPrefix)">
              <xsl:for-each select="/mediaInformation/programTable/program/credits/person[@externalId = current()/@externalId][1]">
                <PersonName personNameId="{substring(@externalId, string-length($personExternalIdPrefix) + 1)}">
                  <mpeg7:GivenName xml:lang="NL" initial="">
                    <xsl:value-of select="givenName" />
                  </mpeg7:GivenName>
                  <mpeg7:FamilyName xml:lang="NL">
                    <xsl:value-of select="familyName" />
                  </mpeg7:FamilyName>
                </PersonName>
              </xsl:for-each>
            </xsl:if>
          </xsl:for-each-group>
        </CreditsInformationTable>
      </ProgramDescription>
    </TVAMain>
  </xsl:template>




  <xsl:template name="roles">
    <xsl:param name="id"/>
    <xsl:choose>
      <xsl:when test="$id = 'ACTOR'">urn:mpeg:mpeg7:cs:RoleCS:2001:ACTOR</xsl:when>
      <xsl:when test="$id = 'PRESENTER'">urn:tva:metadata:cs:TVARoleCS:2004:V721</xsl:when>
      <xsl:when test="$id = 'COMMENTATOR'">urn:tva:metadata:cs:TVARoleCS:2004:V32</xsl:when>
      <xsl:when test="$id = 'DIRECTOR'">urn:mpeg:mpeg7:cs:RoleCS:2001:DIRECTOR</xsl:when>
      <xsl:when test="$id = 'SCRIPTWRITER'">urn:mpeg:mpeg7:cs:RoleCS:2001:SCRIPTWRITER</xsl:when>
      <xsl:when test="$id = 'COMPOSER'">urn:mpeg:mpeg7:cs:RoleCS:2001:COMPOSER</xsl:when>
      <xsl:when test="$id = 'PRODUCER'">PRODUCER</xsl:when>
      <xsl:when test="$id = 'ASSISTANT_DIRECTOR'">ASSISTANT_DIRECTOR</xsl:when>
      <xsl:when test="$id = 'CAMERA'">CAMERA</xsl:when>
      <xsl:when test="$id = 'CHOREOGRAPHY'">CHOREOGRAPHY</xsl:when>
      <xsl:when test="$id = 'DUBBING'">DUBBING</xsl:when>
      <xsl:when test="$id = 'MAKEUP'">MAKEUP</xsl:when>
      <xsl:when test="$id = 'EDITOR'">MONTAGE</xsl:when>
      <xsl:when test="$id = 'PRODUCTION_MANAGEMENT'">PRODUCTION_MANAGEMENT</xsl:when>
      <xsl:when test="$id = 'STAGING'">STAGING</xsl:when>
      <xsl:when test="$id = 'STUNT'">STUNT</xsl:when>
      <xsl:when test="$id = 'VISUAL_EFFECTS'">VISUAL_EFFECTS</xsl:when>
      <xsl:otherwise>UNDEFINED</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
