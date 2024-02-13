PACKAGE:=nl.vpro.domain.npo
DIR:=src/main/java/$(subst .,/,$(PACKAGE))
REPO=$(HOME)/.m2/repository
jaxbcommons=$(REPO)/com/btmatthews/jaxb2_commons/jaxb2-commons-lang3/1.0.0/jaxb2-commons-lang3-1.0.0.jar
VB=2.3.0
V=$(VB).1

xjc=$(REPO)/com/sun/xml/bind/jaxb-xjc/$(V)/jaxb-xjc-$(V).jar
jaxb=$(REPO)/com/sun/xml/bind/jaxb-core/$(V)/jaxb-core-$(V).jar
jaxbapi=$(REPO)/javax/xml/bind/jaxb-api/$(VB)/jaxb-api-$(VB).jar
jaxbimpl=$(REPO)/com/sun/xml/bind/jaxb-impl/$(V)/jaxb-impl-$(V).jar

commonslang=$(REPO)/org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar
lombok=$(REPO)/de/plushnikov/xjc/xjc-lombok-plugin/1.0/xjc-lombok-plugin-1.0.jar

CLASSPATH=$(xjc):$(jaxb):$(jaxbapi):$(jaxbimpl):$(lombok)
get=cd /tmp ; mvn  dependency:get

#REPO)/com/sun/istack/istack-commons-runtime/2.16/istack-commons-runtime-2.16.jar:$(REP#/org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar

XJC=java -cp $(CLASSPATH) com.sun.tools.xjc.XJCFacade -classpath $(CLASSPATH) -verbose -Xlombok -mark-generated -extension
#XJC=xjc -classpath "$(CLASSPATH)" -verbose -mark-generated -extension -Xlombok

.PHONY: all
all: $(DIR)/notify/v3_0/ObjectFactory.java $(DIR)/notify/v3_2/ObjectFactory.java $(DIR)/restriction/v2_1/ObjectFactory.java $(DIR)/restriction/v2_2/ObjectFactory.java $(DIR)/revoke/v3_0/ObjectFactory.java $(DIR)/forecast/v2_0/ObjectFactory.java $(DIR)/notify/nep/ObjectFactory.java

$(DIR)/notify/v3_0/ObjectFactory.java: src/main/resources/npo-xsd/notify_3.0.xsd deps
	$(XJC) -p $(PACKAGE).notify.v3_0 -d src/main/java $<

$(DIR)/notify/v3_2/ObjectFactory.java: src/main/resources/npo-xsd/notify_3.2.xsd deps
	$(XJC) -p $(PACKAGE).notify.v3_2 -d src/main/java $<

$(DIR)/restriction/v2_1/ObjectFactory.java: src/main/resources/npo-xsd/restriction_2.1.xsd deps
	$(XJC) -p $(PACKAGE).restriction.v2_1 -d src/main/java $<


$(DIR)/restriction/v2_2/ObjectFactory.java: src/main/resources/npo-xsd/restriction_2.2.xsd deps
	$(XJC) -p $(PACKAGE).restriction.v2_2 -d src/main/java $<

$(DIR)/revoke/v3_0/ObjectFactory.java: src/main/resources/npo-xsd/revoke_3.0.xsd deps
	$(XJC) -p $(PACKAGE).revoke.v3_0 -d src/main/java $<



$(DIR)/forecast/v2_0/ObjectFactory.java: src/main/resources/npo-xsd/voorspel_2.0.xsd deps
	$(XJC) -p $(PACKAGE).forecast.v2_0 -d src/main/java $<


$(DIR)/prediction/ObjectFactory.java: src/main/resources/npo-xsd/voorspel.xsd $(deps)
	$(XJC) -p $(PACKAGE).prediction -d src/main/java $<


$(DIR)/notify/nep/ObjectFactory.java: src/main/resources/npo-xsd/location-notify.xsd deps
	$(XJC) -p $(PACKAGE).notify.nep -d src/main/java $<



deps: $(xjc) $(lombok) $(jaxb) $(jaxbapi) $(jaxbimpl)
	echo "hoi"
	#$(get) -DgroupId=com.btmatthews.jaxb2_commons -DartifactId=jaxb2-commons-lang3 -Dversion=1.0.0 -Dpackaging=jar
	#mvn dependency:build-classpath -DgroupId=com.btmatthews.jaxb2_commons -DartifactId=jaxb2-commons-lang3 -Dversion=1.0.0 -Dpackaging=jar

$(lombok):
	$(get) -DgroupId=de.plushnikov.xjc -DartifactId=xjc-lombok-plugin -Dversion=1.0 -Dpackaging=jar

$(xjc):
	$(get) -DgroupId=com.sun.xml.bind -DartifactId=jaxb-xjc -Dversion=$(V) -Dpackaging=jar

$(jaxb):
	$(get) -DgroupId=com.sun.xml.bind -DartifactId=jaxb-core -Dversion=$(V) -Dpackaging=jar

$(jaxbapi):
	$(get) -DgroupId=jakarta.xml.bind -DartifactId=jaxb-api -Dversion=$(VB) -Dpackaging=jar

$(jaxbimpl):
	$(get) -DgroupId=com.sun.xml.bind -DartifactId=jaxb-impl -Dversion=$(V) -Dpackaging=jar



clean:
	echo $(DIR)
	rm -rf $(DIR)
