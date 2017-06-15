/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.web.media;

import org.springframework.util.MultiValueMap;

import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.search.MediaForm;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.transfer.extjs.TransferList;


public interface ExtMediaService {

    TransferList<?> suggestTitles(MediaForm form);

    TransferList<?> suggestTags(String input, int max);

    TransferList<?> search(MediaForm form, boolean onlyWriteable);

    TransferList<?> createMedia(MultiValueMap<String, String> map) throws ModificationException;

    TransferList<?> getMedia(Long mediaId);

    TransferList<?> updateMedia(Long mediaId, MultiValueMap<String, String> map) throws ModificationException;

    TransferList<?> deleteMedia(Long mediaId);

    TransferList<?> getSegments(Long mediaId);

    TransferList<?> createOrUpdateSegments(Long mediaId, MultiValueMap<String, String> map) throws ModificationException;

    TransferList<?> getLocations(Long mediaId);

    TransferList<?> createOrUpdateLocations(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getRelations(Long mediaId);

    TransferList<?> createOrUpdateRelations(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getEpisodes(Long mediaId);

    TransferList<?> createOrUpdateEpisodes(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getEpisodeOf(Long mediaId);

    TransferList<?> createOrUpdateEpisodeOfs(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getMembers(Long mediaId);

    TransferList<?> createOrUpdateMembers(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getMemberOf(Long mediaId);

    TransferList<?> createOrUpdateMemberOfs(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getImages(Long mediaId);

    TransferList<?> createOrUpdateImages(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getPersons(Long mediaId);

    TransferList<?> createOrUpdatePersons(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getWebsites(Long mediaId);

    TransferList<?> createOrUpdateWebsites(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getPortalRestrictions(Long mediaId);

    TransferList<?> createOrUpdatePortalRestrictions(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getGeoRestrictions(Long mediaId);

    TransferList<?> createOrUpdateGeoRestrictions(Long mediaId, MultiValueMap<String, String> map);

    TransferList<?> getOwnerData(Long mediaId, OwnerType owner);

    boolean getUseIndexForSearch();

    void setUseIndexForSearch(boolean useIndexForSearch);

    boolean getUseIndexForMembers();

    void setUseIndexForMembers(boolean useIndexForMembers);

}
