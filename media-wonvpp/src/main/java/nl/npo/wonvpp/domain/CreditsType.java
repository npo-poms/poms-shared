package nl.npo.wonvpp.domain;

public record CreditsType(
    PersonType person,
    String function,
    String role
    ) {
}
