package nl.vpro.nep.domain.workflow;


import lombok.Getter;

@Getter
public enum StatusType {
    UNKNOWN("De workflow is nog niet bekend", false), // and therefore not returned by gatekeeper
    RUNNING("De ​workflow ​is ​gestart ​en ​in ​verwerking", false),
    COMPLETED("De ​workflow ​is ​succesvol ​afgerond ​en ​de ​streams ​zijn ​afgemeld bij ​POMS", true),
    FAILED("De ​workflow ​kon ​niet ​worden ​voltooid", true),
    TIMED_OUT("De ​workflow ​was ​niet ​binnen ​een ​redelijke ​tijd ​voltooid ​en ​is afgebroken", true),
    TERMINATED("De ​workflow ​is ​handmatig ​afgebroken ​door ​een ​NEP ​medewerker", true),
    PAUSED("De ​workflow ​is ​gepauzeerd ​door ​een ​NEP ​medewerker", false);
    private final String description;
    private final boolean endStatus;

    StatusType(String description, boolean endStatus) {
        this.description = description;
        this.endStatus = endStatus;
    }
}

