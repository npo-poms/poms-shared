# This was called to generate the classes in src/main/java/nl/vpro/domain/media/projectm/metadata/
# The result was checked in. If the XSD changes it can be run again.
PACKAGE:=nl.vpro.domain.npo
DIR:=src/main/java/$(subst .,/,$(PACKAGE))



$(DIR)/projectm/metadata/v2_1/ObjectFactory.java:  src/main/resources/nl/vpro/domain/npo/projectm/metadata_v2_1.xsd
	mkdir -p src/main/java
	echo $(DIR)/metadata/ObjectFactory.java
	xjc -p $(PACKAGE).projectm.metadata.v2_1 -d src/main/java $<

$(DIR)/projectm/metadata/v3_2/ObjectFactory.java:  src/main/resources/nl/vpro/domain/npo/projectm/metadata_v3_2.xsd
	mkdir -p src/main/java
	echo $(DIR)/metadata/ObjectFactory.java
	xjc -p $(PACKAGE).projectm.metadata.v3_2 -d src/main/java $<
