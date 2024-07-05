package nl.vpro.berlijn.domain.productmetadata;

import java.util.List;

public record ContentRating(
    NicamAge nicamAge,
    List<NicamContent> nicamContent
) {

}
