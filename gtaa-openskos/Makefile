
KEY_A:=$(shell awk -F'[=:]' '$$1 == "gtaa.key.acc" {print $$2}' $(HOME)/conf/openskosrepository.properties)
PASSWORD_A:=$(shell awk -F'[=:]' '$$1 == "gtaa.key.acc" {print $$3}' $(HOME)/conf/openskosrepository.properties | jq -Rr @uri)

KEY_P:=$(shell awk -F'[=:]' '$$1 == "gtaa.key.prod" {print $$2}' $(HOME)/conf/openskosrepository.properties)
PASSWORD_P:=$(shell awk -F'[=:]' '$$1 == "gtaa.key.prod" {print $$3}' $(HOME)/conf/openskosrepository.properties | jq -Rr @uri)

TERM:=Lutjebroek

test_acc:
		cat geo.xml | sed 's/REPLACE/$(shell date)/g' | curl -D- -XPOST --header 'content-type: application/xml' --header 'accept: application/rdf+xml'    "https://skos-acc.beeldengeluid.nl/api/concept?key=$(KEY_A):$(PASSWORD_A)&collection=gtaa&autoGenerateIdentifiers=true&tenant=beng" --data-binary @-

test_acc_lutjebroek:
		cat lutjebroek.xml | sed 's/REPLACE/$(shell date)/g' | curl -D- -XPOST --header 'content-type: application/xml' --header 'accept: application/rdf+xml'    "https://skos-acc.beeldengeluid.nl/api/concept?key=$(KEY_A):$(PASSWORD_A)&collection=gtaa&autoGenerateIdentifiers=true&tenant=beng" --data-binary @-



# FAILS:
# Network error on JustSKOS invokement. Message: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested targetmichiel@mitulo:(main,10)~/github/npo-poms/poms-shared/gtaa-openskos$
test_prod:
		cat geo.xml | sed 's/REPLACE/$(shell date)/g' | curl -D- -XPOST --header 'content-type: application/xml' --header 'accept: application/rdf+xml'    "https://gtaa.apis.beeldengeluid.nl/api/concept?key=$(KEY_P):$(PASSWORD_P)&collection=gtaa&autoGenerateIdentifiers=true&tenant=beng" --data-binary @-

search:
		curl --get --data-urlencode "q=$(TERM)*" \
                --data-urlencode "tenant=beng" \
                'https://gtaa.apis.beeldengeluid.nl/api/find-concepts'


search_acc:
		curl --get --data-urlencode "q=$(TERM)*" \
                --data-urlencode "tenant=beng" \
                'https://skos-acc.beeldengeluid.nl/api/find-concepts'



%.svg: %.dot Makefile
	 dot  -Tsvg $< -o $@
