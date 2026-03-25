package nl.vpro.wonvpp.domain;

public record CreditsType(
    PersonType person,
    String function,
    String role
    ) {
}
