<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns="urn:vpro:media:2009"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:tva="urn:tva:metadata:2004"
  xmlns:mpeg7="urn:mpeg:mpeg7:schema:2001"
  xmlns:vpro="urn:vpro:saxon"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xsi:schemaLocation="urn:vpro:media:2009 https://poms.omroep.nl/schema/vproMedia.xsd
                      urn:tva:metadata:2004 http://www.broadcastingdata.com/dataimport/tva/new/v13_am1/tva_metadata_v13_am1.xsd"
  exclude-result-prefixes="tva mpeg7 xsi vpro xsd"
  version="2.0">

  <!--
    Deze XSLT kun je testen via nl.vpro.camel.media.routes.TVATransformerTest
  -->
  <xsl:output method="xml" indent="yes" encoding="UTF-8" version="1.0"/>
  <xsl:param name="channelMapping"/>
  <xsl:param name="newGenres"/>
  <xsl:param name="owner" select="'MIS'" />
  <xsl:param name="personUriPrefix" select="''" />
  <xsl:param name="workflow" select="''" />

  <xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz'"/>
  <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>

  <xsl:template match="/tva:TVAMain">
    <xsl:variable name="channel">
      <xsl:call-template name="channels">
        <xsl:with-param name="channel"
                        select="tva:ProgramDescription/tva:ProgramLocationTable/tva:Schedule/@serviceIDRef"/>
      </xsl:call-template>
    </xsl:variable>
    <!-- <mediaInformation> -->
    <xsl:element name="mediaInformation" namespace="urn:vpro:media:2009">
      <xsl:copy-of select="@publicationTime | @version"/>

      <xsl:variable name="avType">
        <xsl:call-template name="getMediaType">
          <xsl:with-param name="channel" select="$channel"/>
        </xsl:call-template>
      </xsl:variable>

      <!-- <programTable> -->
      <xsl:element name="programTable">
        <xsl:for-each select="tva:ProgramDescription/tva:ProgramInformationTable">

          <!-- <program> -->
          <xsl:for-each select="tva:ProgramInformation">
            <program>
              <xsl:if test="$workflow != ''">
                <xsl:attribute name="workflow"><xsl:value-of select="$workflow" /></xsl:attribute>
              </xsl:if>
              <xsl:variable name="crid">
                <xsl:value-of select="@programId"/>
              </xsl:variable>
              <!-- <poProgId> -->
              <xsl:variable name="poProgId">
                <xsl:value-of
                    select="normalize-space(../../tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent[tva:Program/@crid = $crid][1]/tva:OtherIdentifier[@type = 'ProductID']/text())"
                    />
              </xsl:variable>
              <xsl:if test="$poProgId != ''">
                <xsl:attribute name="mid">
                  <xsl:value-of select="$poProgId"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:copy-of select="@*[name() != 'programId' and name() != 'strand']"/>
              <xsl:choose>
                <xsl:when test="@strand = 'true'">
                  <xsl:attribute name="type">
                    <xsl:text>STRAND</xsl:text>
                  </xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:attribute name="type">
                    <xsl:text>BROADCAST</xsl:text>
                  </xsl:attribute>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:call-template name="baseMediaTemplate">
                <xsl:with-param name="channel" select="$channel"/>
                <xsl:with-param name="mid" select="$poProgId"/>

              </xsl:call-template>

              <!-- <poSeriesID -->
              <xsl:variable name="poSeriesId">
                <xsl:value-of
                    select="normalize-space(../../tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent[tva:Program/@crid = $crid][1]/tva:OtherIdentifier[@type = 'SeriesID']/text())"
                    />
              </xsl:variable>

              <!-- <poProgType> -->
              <xsl:for-each
                  select="tva:BasicDescription/tva:OtherTextElement[./@type = 'PoProgType']">
                <xsl:element name="poProgType">
                  <xsl:value-of select="substring(./text(), 0, 255)"/>
                </xsl:element>
              </xsl:for-each>
              <xsl:for-each select="../../tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent[tva:Program/@crid = $crid]/tva:OtherIdentifier[@type = 'SeriesID']">
                <descendantOf type="SEASON">
                  <xsl:attribute name="midRef">
                    <xsl:value-of select="normalize-space(text())" />
                  </xsl:attribute>
                </descendantOf>
              </xsl:for-each>
              <xsl:for-each
                  select="../../tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent[tva:Program/@crid = $crid]/tva:OtherIdentifier[@type = 'ParentSeriesID']">
                <descendantOf type="SERIES">
                  <xsl:attribute name="midRef">
                    <xsl:value-of select="normalize-space(text())"/>
                  </xsl:attribute>
                </descendantOf>
              </xsl:for-each>
              <xsl:if test="$poSeriesId != ''">
                <episodeOf type="SEASON">
                  <xsl:attribute name="midRef">
                    <xsl:value-of select="$poSeriesId"/>
                  </xsl:attribute>
                  <xsl:attribute name="index">
                    <xsl:choose>
                      <xsl:when test="tva:BasicDescription/tva:EpisodeNumber castable as xsd:integer and number(tva:BasicDescription/tva:EpisodeNumber) &gt;= 1">
                        <xsl:value-of select="tva:BasicDescription/tva:EpisodeNumber"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <!-- Default value because tva:EpisodeNumber not found or not numeric -->
                        <xsl:text>1</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:attribute>

                </episodeOf>
              </xsl:if>
            </program>
          </xsl:for-each>
        </xsl:for-each>
      </xsl:element>



      <groupTable>
        <xsl:variable name="seriesIDs">
          <xsl:for-each select="distinct-values(tva:ProgramDescription/tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent/tva:OtherIdentifier[@type = 'SeriesID']/text())">
            <xsl:if test="position() > 1">
              <xsl:text>|</xsl:text>
            </xsl:if>
            <xsl:value-of select="." />
          </xsl:for-each>
        </xsl:variable>
        <xsl:for-each
            select="tva:ProgramDescription/tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent/tva:OtherIdentifier[@type = 'SeriesID' and contains($seriesIDs, text())][1]">
          <xsl:variable name="programCrid">
            <xsl:value-of select="../tva:Program/@crid"/>
          </xsl:variable>
          <xsl:variable name="seriesMid">
            <xsl:value-of select="../tva:OtherIdentifier[@type = 'ParentSeriesID']" />
          </xsl:variable>
          <xsl:variable name="broadcasters" select="../tva:BroadcasterList/tva:Broadcaster/@code" />
          <group type="SEASON" mid="{.}" avType="{$avType}">
            <xsl:if test="$workflow != ''">
              <xsl:attribute name="workflow"><xsl:value-of select="$workflow" /></xsl:attribute>
            </xsl:if>
            <xsl:for-each
                select="//tva:ProgramDescription/tva:ProgramInformationTable/tva:ProgramInformation[@programId = $programCrid]/tva:BasicDescription[1]">
              <xsl:for-each select="$broadcasters">
                <broadcaster>
                  <xsl:attribute name="id">
                    <xsl:value-of select="vpro:findBroadcaster(.)"/>
                  </xsl:attribute>
                  <xsl:value-of select="vpro:findBroadcaster(.)"/>
                </broadcaster>
              </xsl:for-each>
              <xsl:if test="tva:Title[@type='main']">
                <title type="MAIN" owner="{$owner}">
                  <xsl:value-of select="normalize-space(tva:Title[@type='main'])"/>
                </title>
              </xsl:if>
              <xsl:for-each select="tva:Title[lower-case(@type)='translatedtitle']">
                <title type="ORIGINAL" owner="{$owner}">
                  <xsl:value-of select="normalize-space(text())"/>
                </title>
              </xsl:for-each>
              <xsl:if test="string-length(vpro:stripHtml(normalize-space(tva:Synopsis[@length = 'long' and not(@type)]))) > 0">
                <description type="MAIN" owner="{$owner}">
                  <xsl:value-of select="vpro:stripHtml(normalize-space(tva:Synopsis[@length = 'long' and not(@type)]))"/>
                </description>
              </xsl:if>
              <xsl:if test="$seriesMid != ''">
                <memberOf midRef="{$seriesMid}">
                  <xsl:choose>
                    <xsl:when test="tva:Season castable as xsd:integer">
                      <xsl:attribute name="index">
                        <xsl:value-of select="tva:Season" />
                      </xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:comment>tva:Season = <xsl:value-of select="tva:Season" /> is not a number</xsl:comment>
                    </xsl:otherwise>
                  </xsl:choose>
                </memberOf>
              </xsl:if>
              <xsl:if test="tva:Season">
                <poSequenceInformation>
                  <xsl:value-of select="substring(tva:Season, 0, 255)"/>
                </poSequenceInformation>
              </xsl:if>
            </xsl:for-each>

          </group>
        </xsl:for-each>

        <xsl:variable name="parentSeriesIDS">
          <xsl:for-each select="distinct-values(tva:ProgramDescription/tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent/tva:OtherIdentifier[@type = 'ParentSeriesID']/text())">
            <xsl:if test="position() > 1">
              <xsl:text>|</xsl:text>
            </xsl:if>
            <xsl:value-of select="."/>
          </xsl:for-each>
        </xsl:variable>


        <xsl:for-each select="tva:ProgramDescription/tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent/tva:OtherIdentifier[@type = 'ParentSeriesID' and contains($parentSeriesIDS, text())][1]">
          <xsl:variable name="programCrid">
            <xsl:value-of select="../tva:Program/@crid" />
          </xsl:variable>
          <xsl:variable name="broadcasters" select="../tva:BroadcasterList/tva:Broadcaster/@code" />
          <group type="SERIES" mid="{.}" avType="{$avType}">
            <xsl:if test="$workflow != ''">
              <xsl:attribute name="workflow"><xsl:value-of select="$workflow" /></xsl:attribute>
            </xsl:if>
            <xsl:for-each select="//tva:ProgramDescription/tva:ProgramInformationTable/tva:ProgramInformation[@programId = $programCrid]/tva:BasicDescription[1]">
              <xsl:for-each select="$broadcasters">
                <broadcaster>
                  <xsl:attribute name="id">
                    <xsl:value-of select="vpro:findBroadcaster(.)"/>
                  </xsl:attribute>
                  <xsl:value-of select="vpro:findBroadcaster(.)"/>
                </broadcaster>
              </xsl:for-each>
              <xsl:if test="tva:Title[@type='parentSeriesTitle']">
                <title type="MAIN" owner="{$owner}">
                  <xsl:value-of select="normalize-space(tva:Title[@type='parentSeriesTitle'])" />
                </title>
              </xsl:if>
              <xsl:if test="string-length(normalize-space(tva:Synopsis[@length = 'long' and @type='parentSeriesSynopsis'])) > 0">
                <description type="MAIN" owner="{$owner}">
                  <xsl:value-of select="vpro:stripHtml(normalize-space(tva:Synopsis[@length = 'long' and @type='parentSeriesSynopsis']))"/>
                </description>
              </xsl:if>
            </xsl:for-each>
          </group>
        </xsl:for-each>
      </groupTable>
      <!-- <schedule> -->
      <xsl:element name="schedule">
        <xsl:for-each select="tva:ProgramDescription/tva:ProgramLocationTable/tva:Schedule[@start != '' and @end != '']">
          <xsl:attribute name="channel">
            <xsl:value-of select="$channel"/>
          </xsl:attribute>
          <xsl:attribute name="start">
            <xsl:value-of select="@start"/>
          </xsl:attribute>
          <xsl:attribute name="stop">
            <xsl:value-of select="@end"/>
          </xsl:attribute>
          <xsl:copy-of select="@releaseVersion"/>
          <xsl:for-each select="tva:ScheduleEvent">
            <!-- <scheduleEvent> -->
            <xsl:element name="scheduleEvent">
              <xsl:if test="tva:InstanceMetadataId">
                <xsl:attribute name="imi">
                  <xsl:value-of select="tva:InstanceMetadataId"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:attribute name="urnRef">
                <xsl:value-of select="tva:Program/@crid"/>
              </xsl:attribute>
              <xsl:attribute name="channel">
                <xsl:value-of select="$channel"/>
              </xsl:attribute>
              <xsl:if test="tva:Program/@strand">
                <xsl:attribute name="type">
                  <xsl:text>STRAND</xsl:text>
                </xsl:attribute>
              </xsl:if>
              <xsl:if test="tva:Net/@code">
                <xsl:variable name="net"><xsl:value-of select="vpro:findNet(tva:Net/@code)"/></xsl:variable>
                <xsl:if test="$net != ''">
                  <xsl:attribute name="net"><xsl:value-of select="$net" /></xsl:attribute>
                </xsl:if>
              </xsl:if>
              <!-- <repeat> -->
              <xsl:if test="tva:Repeat or tva:RepeatText">
                <xsl:element name="repeat">
                  <xsl:choose>
                    <xsl:when test="tva:Repeat">
                      <xsl:attribute name="isRerun">
                        <xsl:value-of select="tva:Repeat/@value"/>
                      </xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:attribute name="isRerun">false</xsl:attribute>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:value-of select="tva:RepeatText"/>
                </xsl:element>
              </xsl:if>
              <!-- <avAttributes> -->
              <xsl:for-each select="tva:InstanceDescription">
                <xsl:for-each select="tva:MemberOf">
                  <xsl:call-template name="memberOf"/>
                </xsl:for-each>
                <xsl:for-each select="tva:AVAttributes">
                  <xsl:call-template name="avAttributes"/>
                </xsl:for-each>
              </xsl:for-each>
              <xsl:variable name="crid">
                <xsl:value-of select="tva:Program/@crid"/>
              </xsl:variable>
              <!-- textSubtitles -->
              <xsl:variable name="subtitlelsURN">
                <xsl:value-of
                    select="/tva:TVAMain/tva:ProgramDescription/tva:ProgramInformationTable/tva:ProgramInformation[@programId = $crid]/tva:BasicDescription/tva:Subtitling[@href = 'urn:bds:metadata:cs:SubtitlingCS:2007:TeletextSubtitles']/@href"
                    />
              </xsl:variable>
              <xsl:if test="normalize-space($subtitlelsURN)">
                <xsl:element name="textSubtitles">
                  <xsl:value-of
                      select="/tva:TVAMain/tva:ProgramDescription/tva:ProgramInformationTable/tva:ProgramInformation[@programId = $crid]/tva:BasicDescription/tva:Subtitling[@href = 'urn:bds:metadata:cs:SubtitlingCS:2007:TeletextSubtitles']/tva:Name/text()"
                      />
                </xsl:element>
              </xsl:if>
              <!-- textPage -->
              <xsl:variable name="textPage">
                <xsl:value-of
                    select="/tva:TVAMain/tva:ProgramDescription/tva:ProgramInformationTable/tva:ProgramInformation[@programId = $crid]/tva:BasicDescription/tva:RelatedMaterial[tva:HowRelated/@href = 'urn:bds:metadata:cs:TeletextRationaleCS:2007:1']/tva:MediaLocator/mpeg7:MediaUri"
                    />
              </xsl:variable>
              <xsl:if test="normalize-space($textPage)">
                <xsl:element name="textPage">
                  <xsl:value-of
                      select="$textPage"
                      />
                </xsl:element>
              </xsl:if>
              <!-- <start> -->
              <xsl:for-each select="tva:PublishedStartTime">
                <xsl:element name="start">
                  <xsl:value-of select="text()"/>
                </xsl:element>
              </xsl:for-each>
              <!-- <duration> -->
              <xsl:for-each select="tva:PublishedDuration">
                <xsl:element name="duration">
                  <xsl:value-of select="text()"/>
                </xsl:element>
              </xsl:for-each>
              <!-- poProgId -->
              <xsl:for-each select="tva:OtherIdentifier[@type = 'ProductID']">
                <xsl:element name="poProgID">
                  <xsl:value-of select="normalize-space(text())"/>
                </xsl:element>
              </xsl:for-each>
              <!-- poSeriesId -->
              <xsl:for-each select="tva:OtherIdentifier[@type = 'SeriesID']">
                <xsl:element name="poSeriesID">
                  <xsl:value-of select="normalize-space(text())"/>
                </xsl:element>
              </xsl:for-each>
              <xsl:for-each select="tva:Primary_lifestyle">
                <xsl:element name="primaryLifestyle">
                  <xsl:value-of select="@code"/>
                </xsl:element>
              </xsl:for-each>
              <xsl:for-each select="tva:Secondary_lifestyle">
                <xsl:element name="secondaryLifestyle">
                  <xsl:value-of select="@code"/>
                </xsl:element>
              </xsl:for-each>
            </xsl:element>
          </xsl:for-each>
        </xsl:for-each>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template name="baseMediaTemplate">
    <xsl:param name="channel"/>
    <xsl:param name="mid"/>

    <xsl:attribute name="avType">
      <xsl:call-template name="getMediaType">
        <xsl:with-param name="channel" select="$channel"/>
      </xsl:call-template>
    </xsl:attribute>
    <xsl:attribute name="embeddable">
      <xsl:text>true</xsl:text>
    </xsl:attribute>
    <!-- <crid> -->
    <xsl:variable name="crid">
      <xsl:value-of select="@programId | @groupId"/>
    </xsl:variable>
    <crid><xsl:value-of select="$crid"/></crid>
    <!-- <broadcaster> -->
    <xsl:for-each
        select="../../tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent[tva:Program/@crid = $crid][1]/tva:BroadcasterList">
      <xsl:for-each select="tva:Broadcaster">
        <broadcaster>
          <xsl:attribute name="id">
            <xsl:value-of select="vpro:findBroadcaster(@code)"/>
          </xsl:attribute>
          <xsl:value-of select="vpro:findBroadcaster(@code)" />
        </broadcaster>
      </xsl:for-each>
    </xsl:for-each>
    <!-- titles -->
    <xsl:choose>
      <xsl:when test="tva:BasicDescription/tva:Title[normalize-space(text()) !='']">
        <xsl:for-each select="tva:BasicDescription/tva:Title">
          <xsl:if test="normalize-space(text()) != ''">
            <xsl:choose>
              <xsl:when test="lower-case(./@type) = 'originallanguage'">
                <title type="ORIGINAL" owner="{$owner}">
                  <xsl:value-of select="normalize-space(text())"/>
                </title>
              </xsl:when>
              <xsl:when test="lower-case(./@type) = 'translatedepisodetitle'">
                <title type="ORIGINAL" owner="{$owner}">
                  <xsl:value-of select="normalize-space(text())"/>
                </title>
              </xsl:when>
              <xsl:when test="lower-case(./@type) = 'episodetitle'">
                <title type="SUB" owner="{$owner}">
                  <xsl:value-of select="normalize-space(text())"/>
                </title>
              </xsl:when>
              <xsl:when test="lower-case(./@type) = 'main'">
                <title type="MAIN" owner="{$owner}">
                  <xsl:value-of select="normalize-space(text())"/>
                </title>
              </xsl:when>
              <xsl:when test="lower-case(./@type) = 'parentseriestitle'">
                <xsl:comment>tva:Title[@type='parentseriestitle'] '<xsl:value-of select="."/>' goes to series</xsl:comment>
              </xsl:when>
              <xsl:when test="lower-case(./@type) = 'translatedtitle'">
                <xsl:choose>
                  <!-- Only export translatedtitle as title if no translatedepisodetitle exists -->
                  <xsl:when test="../tva:Title[lower-case(./@type) = 'translatedepisodetitle']">
                    <xsl:comment>tva:Title[@type='translatedtitle'] '<xsl:value-of select="."/>' goes to season</xsl:comment>
                  </xsl:when>
                  <xsl:otherwise>
                    <title type="ORIGINAL" owner="{$owner}">
                      <xsl:value-of select="normalize-space(text())"/>
                    </title>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:when>
              <xsl:when test="lower-case(./@type) = 'sendertitle'">
                <xsl:comment>Found SenderTitle <xsl:value-of select="."/> (what's that)</xsl:comment>
              </xsl:when>
              <xsl:when test="lower-case(./@type) = 'originalepisodetitle'">
                <xsl:comment>Found originalepisodetitle <xsl:value-of select="."/> (what's that)</xsl:comment>
              </xsl:when>
              <xsl:otherwise>
                <title type="KENNIKNIET-{@type}"> <!-- trigger an unmarshall error -->
                  <xsl:value-of select="normalize-space(text())"/>
                </title>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:if>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <title type="MAIN" owner="{$owner}">
          <xsl:choose>
            <xsl:when test="$mid != ''">
              <xsl:value-of select="$mid" />
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$crid" />
            </xsl:otherwise>
          </xsl:choose>
        </title>
      </xsl:otherwise>
    </xsl:choose>
    <!-- <description> -->
    <xsl:for-each select="tva:BasicDescription/tva:Synopsis[vpro:stripHtml(normalize-space(text())) != '']">
      <xsl:choose>
        <xsl:when test="@length = 'short'">
          <description type="SHORT" owner="{$owner}">
            <xsl:call-template name="synopsisAssembler">
              <xsl:with-param name="text">
                <xsl:value-of select="vpro:stripHtml(normalize-space(text()))"/>
              </xsl:with-param>
              <xsl:with-param name="type">
                <xsl:value-of select="../tva:OtherTextElement[./@type = 'PoProgType']"/>
              </xsl:with-param>
            </xsl:call-template>
          </description>
        </xsl:when>
        <xsl:when test="@length = 'medium'">
          <description type="MAIN" owner="{$owner}">
            <xsl:call-template name="synopsisAssembler">
              <xsl:with-param name="text">
                <xsl:value-of select="vpro:stripHtml(normalize-space(text()))"/>
              </xsl:with-param>
              <xsl:with-param name="type">
                <xsl:value-of select="../tva:OtherTextElement[./@type = 'PoProgType']"/>
              </xsl:with-param>
            </xsl:call-template>
          </description>
        </xsl:when>
        <xsl:when test="@length = 'long' and not(@type)">
          <xsl:comment>tva:Synopsis[@length = 'long'] '<xsl:value-of select="."/>' goes to season</xsl:comment>
        </xsl:when>
        <xsl:when test="@length = 'long' and @type = 'parentSeriesSynopsis'">
          <xsl:comment>tva:Synopsis[@length = 'long' and @type = 'parentSeriesSynopsis'] '<xsl:value-of select="."/>' goes to series</xsl:comment>
        </xsl:when>
        <xsl:otherwise>
          <xsl:comment>Not recognized tva:Synopsis[@length=<xsl:copy-of select="@length"/> @type=<xsl:copy-of select="@type" />]</xsl:comment>
        </xsl:otherwise>

      </xsl:choose>
    </xsl:for-each>
    <xsl:for-each select="tva:BasicDescription/tva:OneLineDescription[normalize-space(text()) != '']">
      <description owner="{$owner}" type="KICKER">
        <xsl:value-of select="vpro:stripHtml(normalize-space(text()))"/>
      </description>
    </xsl:for-each>
    <xsl:choose>
      <xsl:when test="$newGenres = 'true'">
        <xsl:for-each
            select="tva:BasicDescription/tva:Genre[starts-with(@href, 'urn:tva:metadata:cs:2004:')][last()]">
          <xsl:element name="genre">
            <xsl:attribute name="id">
              <xsl:value-of select="vpro:transformEpgGenre(@href)"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each
            select="vpro:transformMisGenres(tva:BasicDescription/tva:Genre[starts-with(@href, 'urn:po:metadata:cs:GenreCS')]/tva:Name)">
          <xsl:element name="genre">
            <xsl:attribute name="id">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:element>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
    <!-- <source> -->
    <xsl:for-each
        select="tva:BasicDescription/tva:RelatedMaterial[tva:HowRelated/@href = 'urn:bds:metadata:cs:ContentSourceCS:2007:Generic' and contains(tva:HowRelated/tva:Name/text(), 'Bron')]">
      <xsl:element name="source">
        <xsl:value-of select="tva:MediaLocator/mpeg7:MediaUri"/>
      </xsl:element>
    </xsl:for-each>
    <!-- <country> -->
    <xsl:choose>
      <xsl:when test="$newGenres = 'true'">
        <!-- Disabled for older MIS deliveries -->
        <xsl:for-each select="tva:BasicDescription/tva:ProductionLocationList/tva:ProductionLocation">
          <xsl:element name="country">
            <xsl:attribute name="code">
              <xsl:value-of select="@code"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>


    <!-- language -->
    <xsl:for-each select="tva:BasicDescription/tva:Language">
      <xsl:element name="language">
        <xsl:attribute name="code">
          <xsl:variable name="lowered" select="translate(text(), $uppercase, $lowercase)" />
          <xsl:choose>
            <!-- I think we agreed on ISO-631 for language codes, but TVA xml consistently contains 'XX' for 'no linguistic content'. -->
            <xsl:when test="$lowered = 'xx'">
              <xsl:text>zxx</xsl:text>
            </xsl:when>
            <xsl:when test="$lowered = 'zz'">
              <!-- MSE-3989 -->
              <xsl:text>und</xsl:text>
            </xsl:when>
            <!-- It's a guess, but I suppose they mean 'Czech' with cz -->
             <xsl:when test="$lowered = 'cz'">
              <xsl:text>cs</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$lowered"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="tva:BasicDescription/tva:IsDubbed">
      <xsl:element name="isDubbed">
        <xsl:value-of select="text()"/>
      </xsl:element>
    </xsl:for-each>

    <!-- <releaseDate>...<year> -->
    <xsl:for-each select="tva:BasicDescription/tva:ReleaseInformation/tva:ReleaseDate/tva:Year">
      <xsl:element name="releaseYear">
        <xsl:value-of select="text()"/>
      </xsl:element>
    </xsl:for-each>
    <!-- <duration> -->
    <!-- See MSE-2123
            <xsl:for-each select="tva:BasicDescription/tva:Duration">
                <xsl:element name="duration">
                    <xsl:value-of select="text()"/>
                </xsl:element>
            </xsl:for-each>
    -->
    <xsl:choose>
      <xsl:when test="../../tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent[tva:Program/@crid = $crid]/tva:PublishedDuration/text()">
        <xsl:element name="duration">
          <xsl:value-of select="../../tva:ProgramLocationTable/tva:Schedule/tva:ScheduleEvent[tva:Program/@crid = $crid][1]/tva:PublishedDuration/text()"/>
        </xsl:element>
      </xsl:when>
      <xsl:when test="tva:BasicDescription/tva:Duration/text()">
        <xsl:element name="duration">
          <xsl:value-of select="tva:BasicDescription/tva:Duration/text()"/>
        </xsl:element>
      </xsl:when>
    </xsl:choose>
    <!-- <credits> -->
    <xsl:for-each select="tva:BasicDescription/tva:CreditsList">
      <xsl:element name="credits">
        <!-- <person> -->
        <xsl:for-each select="tva:CreditsItem/tva:PersonNameIDRef">
          <xsl:element name="person">
            <xsl:attribute name="role">
              <xsl:call-template name="roles">
                <xsl:with-param name="id">
                  <xsl:value-of select="../@role"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:if test="$personUriPrefix != ''">
              <xsl:attribute name="gtaaUri">
                <xsl:value-of select="$personUriPrefix" /><xsl:value-of select="current()/@ref" />
              </xsl:attribute>
            </xsl:if>
            <xsl:element name="givenName">
              <xsl:value-of
                  select="/tva:TVAMain/tva:ProgramDescription/tva:CreditsInformationTable/tva:PersonName[@personNameId = current()/@ref]/mpeg7:GivenName"
                  />
            </xsl:element>
            <xsl:element name="familyName">
              <xsl:value-of
                  select="/tva:TVAMain/tva:ProgramDescription/tva:CreditsInformationTable/tva:PersonName[@personNameId = current()/@ref]/mpeg7:FamilyName"
                  />
            </xsl:element>

          </xsl:element>
        </xsl:for-each>
      </xsl:element>
    </xsl:for-each>
    <!-- <award> -->
    <xsl:for-each select="tva:BasicDescription/tva:AwardsText">
      <xsl:element name="award">
        <xsl:value-of select="text()"/>
      </xsl:element>
    </xsl:for-each>
    <!-- reference -->
    <!-- <ageRating> -->
    <xsl:for-each select="tva:BasicDescription/tva:ParentalGuidance/mpeg7:ParentalRating">
      <xsl:element name="ageRating">
        <xsl:choose>
          <xsl:when test="@href = 'urn:po:metadata:cs:NicamParentalRatingCS:2007:AL'">ALL</xsl:when>
          <xsl:when test="@href = 'urn:po:metadata:cs:NicamParentalRatingCS:2007:16'">16</xsl:when>
          <xsl:when test="@href = 'urn:po:metadata:cs:NicamParentalRatingCS:2007:12'">12</xsl:when>
          <xsl:when test="@href = 'urn:po:metadata:cs:NicamParentalRatingCS:2007:9'">9</xsl:when>
          <xsl:when test="@href = 'urn:po:metadata:cs:NicamParentalRatingCS:2007:6'">6</xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="mpeg7:Name"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:element>
    </xsl:for-each>
    <!-- <contentRating> -->
    <xsl:for-each select="tva:BasicDescription/tva:Genre|tva:BasicDescription/tva:ParentalGuidance/tva:Genre">
      <xsl:if test="starts-with(@href, 'urn:po:metadata:cs:NicamWarningCS:2007')">
        <xsl:element name="contentRating">
          <xsl:choose>
            <xsl:when test="@href = 'urn:po:metadata:cs:NicamWarningCS:2007:d'">DISCRIMINATIE</xsl:when>
            <xsl:when test="@href = 'urn:po:metadata:cs:NicamWarningCS:2007:t'">GROF_TAALGEBRUIK</xsl:when>
            <xsl:when test="@href = 'urn:po:metadata:cs:NicamWarningCS:2007:a'">ANGST</xsl:when>
            <xsl:when test="@href = 'urn:po:metadata:cs:NicamWarningCS:2007:g'">GEWELD</xsl:when>
            <xsl:when test="@href = 'urn:po:metadata:cs:NicamWarningCS:2007:s'">SEKS</xsl:when>
            <xsl:when test="@href = 'urn:po:metadata:cs:NicamWarningCS:2007:h'">DRUGS_EN_ALCOHOL</xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="tva:Name"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:element>
      </xsl:if>
    </xsl:for-each>
    <!-- <email> -->
    <xsl:for-each
        select="tva:BasicDescription/tva:RelatedMaterial[tva:HowRelated/@href = 'urn:bds:metadata:cs:EmailRationaleCS:2007:1']">
      <xsl:if test="vpro:validListValue('nl.vpro.domain.media.MediaObject', 'email', normalize-space(tva:MediaLocator/mpeg7:MediaUri))">
        <xsl:element name="email">
          <xsl:value-of select="normalize-space(tva:MediaLocator/mpeg7:MediaUri)"/>
        </xsl:element>
      </xsl:if>
    </xsl:for-each>
    <!-- <website> -->
    <xsl:for-each
        select="tva:BasicDescription/tva:RelatedMaterial[tva:HowRelated/@href = 'urn:bds:metadata:cs:WebsiteRationaleCS:2007:1']">
      <xsl:element name="website">
        <xsl:value-of select="tva:MediaLocator/mpeg7:MediaUri"/>
      </xsl:element>
    </xsl:for-each>

    <!-- twitter -->
    <xsl:for-each
        select="tva:BasicDescription/tva:TwitterAccount">
      <xsl:element name="twitter">
        <xsl:attribute name="type">ACCOUNT</xsl:attribute>
        <xsl:if test="not(starts-with(text(), '@'))">
          <xsl:text>@</xsl:text>
        </xsl:if>
        <xsl:choose>
          <xsl:when test="starts-with(text(), 'https://twitter.com/')">
            <xsl:value-of select="substring-after(text(), 'https://twitter.com/')" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="text()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:element>
    </xsl:for-each>
    <xsl:for-each
        select="tva:BasicDescription/tva:TwitterHashTag">
      <xsl:element name="twitter">
        <xsl:attribute name="type">HASHTAG</xsl:attribute>
        <xsl:if test="not(starts-with(text(), '#'))">
          <xsl:text>#</xsl:text>
        </xsl:if>
        <xsl:value-of select="text()"/>
      </xsl:element>
    </xsl:for-each>

    <!-- <teletext> -->
    <xsl:for-each
        select="tva:BasicDescription/tva:RelatedMaterial[tva:HowRelated/@href = 'urn:bds:metadata:cs:TeletextRationaleCS:2007:1']">
      <xsl:element name="teletext">
        <xsl:value-of select="tva:MediaLocator/mpeg7:MediaUri"/>
      </xsl:element>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="synopsisAssembler">
    <xsl:param name="text"/>
    <xsl:param name="type"/>

    <!-- This templates handles extra formatting rules to be applied to synopsis fields starting with a lower case letter. -->

    <xsl:choose>
      <xsl:when test="contains($lowercase, substring($text,1,1))">
        <xsl:choose>
          <xsl:when test="contains($lowercase, substring($type,1,1))">
            <!-- Prefix synopsis with country and poProgType: "American" +  " thriller"  + " staring .....".

For now just omit country prefix and cast the first poProgType character to uppercase. -->
            <xsl:value-of
                select="concat(translate(substring($type,1,1), $lowercase, $uppercase), substring($type,2), ' ', $text)"
                />
          </xsl:when>
          <xsl:otherwise>
            <!-- Prefix synopsis with poProgType:  "Thriller"  + " staring ....." -->
            <xsl:value-of select="concat($type, ' ', $text)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="normalize-space($text)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="roles">
    <xsl:param name="id"/>
    <xsl:choose>
      <xsl:when test="$id = 'urn:mpeg:mpeg7:cs:RoleCS:2001:ACTOR'">ACTOR</xsl:when>
      <xsl:when test="$id = 'urn:tva:metadata:cs:TVARoleCS:2004:V721'">PRESENTER</xsl:when>
      <xsl:when test="$id = 'urn:tva:metadata:cs:TVARoleCS:2004:V32'">COMMENTATOR</xsl:when>
      <xsl:when test="$id = 'urn:mpeg:mpeg7:cs:RoleCS:2001:DIRECTOR'">DIRECTOR</xsl:when>
      <xsl:when test="$id = 'urn:mpeg:mpeg7:cs:RoleCS:2001:SCRIPTWRITER'">SCRIPTWRITER</xsl:when>
      <xsl:when test="$id = 'urn:mpeg:mpeg7:cs:RoleCS:2001:COMPOSER'">COMPOSER</xsl:when>
      <xsl:otherwise>UNDEFINED</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="memberOf">
    <xsl:element name="memberOf">
      <xsl:value-of select="@crid"/>
    </xsl:element>
  </xsl:template>

  <xsl:template name="avAttributes">
    <xsl:element name="avAttributes">
      <xsl:for-each select="tva:VideoAttributes">
        <xsl:element name="videoAttributes">
          <xsl:for-each select="tva:AspectRatio">
            <xsl:element name="aspectRatio">
              <xsl:value-of select="text()"/>
            </xsl:element>
          </xsl:for-each>
        </xsl:element>
      </xsl:for-each>
      <!--
                  <xsl:for-each select="tva:AudioAttributes">
                      <xsl:element name="audioAttributes">
                      </xsl:element>
                  </xsl:for-each>
      -->
    </xsl:element>
  </xsl:template>

  <xsl:template name="channels">
    <xsl:param name="channel"/>
    <xsl:variable name="id">
      <xsl:value-of select="$channelMapping/properties/entry[@key = $channel]/text()"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$id != ''">
        <xsl:value-of select="$id"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>unrecognized</xsl:text>
        <xsl:value-of select="$channel"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="getMediaType">
    <xsl:param name="channel"/>
    <xsl:choose>
      <xsl:when test="$channel = 'RAD1'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RAD2'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RAD3'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RAD4'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RAD5'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RAD6'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'REGR'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RFRY'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RNOO'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RTVD'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'ROST'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RGEL'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RFLE'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RBRA'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RUTR'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RNOH'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RWST'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RRIJ'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'LRAD'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RZEE'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'COMM'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'RVER'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'SLAM'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'SKYR'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'BNRN'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'KINK'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'PCAZ'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'QMUS'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'R538'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'GOLD'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'ARRO'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'FUNX'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'CLAS'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'BEL1'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'BEL2'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'KLAR'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'BBR1'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'BBR2'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'BBR3'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'BBR4'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'BBWS'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'BBCX'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'NDR3'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'WDR4'">AUDIO</xsl:when>
      <xsl:when test="$channel = 'WDR3'">AUDIO</xsl:when>
      <xsl:otherwise>VIDEO</xsl:otherwise>
    </xsl:choose>

  </xsl:template>


  <!--
  <xsl:template match="node() | @*">
      <xsl:copy>
          <xsl:apply-templates select="@* | node()" />
      </xsl:copy>
  </xsl:template>
  -->
</xsl:stylesheet>
