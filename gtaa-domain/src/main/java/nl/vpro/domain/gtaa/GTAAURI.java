package nl.vpro.domain.gtaa;

@nl.vpro.validation.URI(schemes = {"http", "https"}, mustHaveScheme = true, hosts = {"data.beeldengeluid.nl"}, patterns = {"http://data\\.beeldengeluid\\.nl/gtaa/\\d+"})
public @interface GTAAURI {
}
