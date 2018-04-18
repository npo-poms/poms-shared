package nl.vpro.domain.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.Embargos;
import nl.vpro.domain.media.support.OwnerType;

/**
 * @author Michiel Meeuwissen
 * @since 5.7
 */
@Slf4j
public class Locations {


    public static Program realize(Program program, Platform platform, String pubOptie, OwnerType owner, Set<OwnerType> replaces) {
        Prediction prediction = program.getPrediction(platform);
        StreamingStatus streamingStatus = program.getStreamingPlatformStatus();

        Encryption encryption;
        if (prediction != null) {
            encryption = prediction.getEncryption();
        }  else {
            log.warn("Realizing without prediction");
            encryption = StreamingStatus.preferredEncryption(streamingStatus);
        }
        return addLocation(program, platform, encryption, pubOptie, owner, replaces);
    }

    public static Locations.RealizeResult realizeStreamingPlatformIfNeeded(MediaObject mediaObject, Platform platform) {
        StreamingStatus streamingPlatformStatus = mediaObject.getStreamingPlatformStatus();

        if (platform == Platform.INTERNETVOD) {
            Prediction webonly = createWebOnlyPredictionIfNeeded(mediaObject);
            log.info("Webonly : {}", webonly);
        }
        Prediction existingPredictionForPlatform = mediaObject.getPrediction(platform);
        Encryption encryption;
        if (existingPredictionForPlatform != null) {
            if (!existingPredictionForPlatform.isPlannedAvailability()) {
                log.debug("Can't realize {} for {} because no availability planned", mediaObject, platform);
                existingPredictionForPlatform.setState(Prediction.State.NOT_ANNOUNCED);
                return Locations.RealizeResult.builder()
                    .needed(false)
                    .program(mediaObject)
                    .reason("NEP status is " + streamingPlatformStatus + " but no availability planned ")
                    .build();
            }
            if (!streamingPlatformStatus.matches(existingPredictionForPlatform.getEncryption())) {
                log.debug("Can't realize {} for {} because incorrect encryption", mediaObject, platform);
                return Locations.RealizeResult.builder()
                    .needed(false)
                    .program(mediaObject)
                    .reason("NEP status is " + streamingPlatformStatus + " but request encryption is " + existingPredictionForPlatform.getEncryption())
                    .build();
            }
            encryption = existingPredictionForPlatform.getEncryption();
            if (encryption == null) {
                encryption = StreamingStatus.preferredEncryption(streamingPlatformStatus);
            }
        } else {
            log.info("No prediction found for platform {} in {} ", platform, mediaObject);
            return Locations.RealizeResult.builder()
                .needed(false)
                .program(mediaObject)
                .reason("NEP status is " + streamingPlatformStatus + " but no prediction found for platform " + platform + "  in " + mediaObject)
                 .build();
        }
        String locationUrl = getLocationUrl(mediaObject, platform, encryption, "nep");
        if (locationUrl == null) {
            // I think this cannot happen any more because of line #90
            return Locations.RealizeResult.builder()
                .needed(false)
                .program(mediaObject)
                .reason("NEP status is " + streamingPlatformStatus + " but no prediction found for platform " + platform + "  in " + mediaObject)
                .build();
        }

        // Checks if this exaction url is available already with correct owne?
        Location authorityLocation = mediaObject.findLocation(locationUrl, OwnerType.AUTHORITY);
        if (authorityLocation == null) {
            // no, just check platform then.
            authorityLocation = getAuthorityLocationForPlatform(mediaObject, platform);
        }
        if (authorityLocation == null) {
            authorityLocation = createLocation(mediaObject, existingPredictionForPlatform, locationUrl);
            if (authorityLocation == null) {
                log.debug("Not created new streaming platform location {} {} for mediaObject {}", locationUrl, platform, mediaObject.getMid());
                 return Locations.RealizeResult.builder()
                     .needed(false)
                     .program(mediaObject)
                     .reason("NEP status is " + streamingPlatformStatus + " but no existing locations or predictions matched")
                     .build();
            } else {
                log.info("creating new streaming platform location {} {} for mediaObject {}", locationUrl, platform, mediaObject.getMid());
                Embargos.copy(existingPredictionForPlatform, authorityLocation);
            }
        } else {
            if (!locationUrl.equals(authorityLocation.getProgramUrl())) {
                log.info("Updating location {} {} for mediaObject {}", locationUrl, platform, mediaObject.getMid());
                authorityLocation.setProgramUrl(locationUrl);
            } else {
                log.info("Location {} {} for mediaObject {} already exists", locationUrl, platform, mediaObject.getMid());
            }
            authorityLocation.setPlatform(platform);
        }

        updateLocationAndPredictions(authorityLocation, mediaObject, platform, getAVAttributes("nep"), OwnerType.AUTHORITY, new HashSet<>());


        return Locations.RealizeResult.builder()
            .needed(true)
            .location(authorityLocation)
            .program(mediaObject)
            .build();
    }

    public static void realizeAndRevokeLocationsIfNeeded(MediaObject media, Platform platform) {
        Locations.realizeStreamingPlatformIfNeeded(media, platform);
        Locations.removeLocationForPlatformIfNeeded(media, platform);
        Locations.updatePredictionStates(media, platform);
    }



    private static Location createLocation(final MediaObject mediaObject, final Prediction prediction, final String locationUrl){
        Location platformAuthorityLocation = new Location(locationUrl, OwnerType.AUTHORITY, prediction.getPlatform());
        platformAuthorityLocation.setPlatform(prediction.getPlatform());
        platformAuthorityLocation.setPublishStartInstant(prediction.getPublishStartInstant());
        platformAuthorityLocation.setPublishStopInstant(prediction.getPublishStopInstant());
        mediaObject.addLocation(platformAuthorityLocation);
        return platformAuthorityLocation;
    }


    private static Program addLocation(Program program, Platform platform, Encryption encryption, String pubOptie, OwnerType owner, Set<OwnerType> replaces) {
        String locationUrl = getLocationUrl(program, platform, encryption, pubOptie);
        if (locationUrl == null) {
            return program;
        }
        Location location = createOrFindLocation(program, locationUrl, owner, platform);

        updateLocationAndPredictions(location, program, platform, getAVAttributes(pubOptie), owner, replaces);
        return program;
    }


    private static void updateLocationAndPredictions(Location location, MediaObject program, Platform platform, AVAttributes avAttributes, OwnerType owner, Set<OwnerType> replaces) {
        location.setAvAttributes(avAttributes);
        if (replaces != null) {
            if (replaces.contains(location.getOwner())) {
                location.setOwner(owner);
            }
        }
        updatePredictionStates(program, platform);
    }

    private static String getLocationUrl(MediaObject program, Platform platform, Encryption encryption, String pubOptie) {
        String baseUrl = getBaseUrl(platform, encryption, pubOptie, program.getStreamingPlatformStatus());
        if (baseUrl == null) {
            return null;
        }
        return baseUrl + program.getMid();
    }


    private static String getBaseUrl(Platform platform, Encryption encryption, String publicationOption, StreamingStatus status) {
        if ("nep".equals(publicationOption)) {
            if (! status.matches(encryption)) {
                log.debug("{} does not match {}", status, encryption);
                return null;
            }
            boolean drm = encryption == Encryption.DRM;
            String scheme = drm ? "npo+drm" : "npo";
            return scheme + "://" + platform.name().toLowerCase() + ".omroep.nl/";
        } else if (platform == Platform.INTERNETVOD && "adaptive".equals(publicationOption)) {
            // https://jira.vpro.nl/browse/MSE-1516
            return "odip+http://odi.omroep.nl/video/" + publicationOption + "/";
        } else if (platform == Platform.INTERNETVOD) {
            return "odi+http://odi.omroep.nl/video/" + publicationOption + "/";
        } else if (platform == Platform.PLUSVOD) {
            return "sub+http://npo.npoplus.nl/video/" + publicationOption + "/";
        } else if (platform == Platform.TVVOD) {
            return "sub+http://tvvod.omroep.nl/video/" + publicationOption + "/";
        }

        throw new UnsupportedOperationException("Unsupported platform " + platform + " with puboption " + publicationOption);
    }


    private static Location createOrFindLocation(Program program, String locationUrl, OwnerType owner, Platform platform) {
        Location location = program.findLocation(locationUrl);
        if (location == null) {
            log.info("creating new location {} {} {} for mediaObject {}", locationUrl, owner, platform, program.getMid());
            location = new Location(locationUrl, owner, platform);
            location.setPlatform(platform);
            program.addLocation(location);
            Prediction prediction = program.getPrediction(platform);
            if (prediction.isNew()) {
                program.getPrediction(platform).setAuthority(Authority.SYSTEM);
                log.info("Created {}", prediction);
            }
        } else {
            log.debug("updating location {} {} for mediaObject {}", locationUrl, owner, program.getMid());
            location.setPlatform(platform);
        }
        return location;
    }



    public static void removeLocationForPlatformIfNeeded(MediaObject mediaObject, Platform platform){
        Location existingPlatformLocation = getAuthorityLocationForPlatform(mediaObject, platform);
        Prediction existingPredictionForPlatform = mediaObject.getPrediction(platform);
        StreamingStatus streamingPlatformStatus = mediaObject.getStreamingPlatformStatus();
        if(existingPlatformLocation != null) {
            if (! existingPredictionForPlatform.isPlannedAvailability()) {
                mediaObject.removeLocation(existingPlatformLocation);
            } else if (! streamingPlatformStatus.matches(existingPredictionForPlatform.getEncryption())) {
                mediaObject.removeLocation(existingPlatformLocation);
            } else if ( existingPredictionForPlatform.getEncryption() == null) {
                mediaObject.removeLocation(existingPlatformLocation);
            } else {
                log.info("{} does not need to be removed", existingPlatformLocation);
            }

        }
        updatePredictionStates(mediaObject, platform);
    }
    public static Prediction createWebOnlyPredictionIfNeeded(MediaObject mediaObject) {
        Set<Location> existingWebonlyLocations = mediaObject.getLocations().stream()
            .filter(l -> l.getOwner() == OwnerType.BROADCASTER)
            .filter(l -> Platform.INTERNETVOD.matches(l.getPlatform()))
            .collect(Collectors.toSet());
        Prediction existingPrediction = mediaObject.getPrediction(Platform.INTERNETVOD);
        if (existingPrediction == null && ! existingWebonlyLocations.isEmpty()) {
            Prediction prediction = mediaObject.findOrCreatePrediction(Platform.INTERNETVOD);
            prediction.setPlannedAvailability(true);
            prediction.setEncryption(null);
            for (Location l : existingWebonlyLocations) {
                Embargos.copyIfLessRestricted(l, prediction);
            }
            return prediction;
        }
        return null;

    }

    private static Location getAuthorityLocationForPlatform(MediaObject mediaObject, Platform platform){
        return mediaObject.getLocations().stream()
            .filter(l -> l.getOwner() == OwnerType.AUTHORITY && l.getPlatform() == platform)
            .findFirst()
            .orElse(null);
    }




    public static boolean updatePredictionStates(MediaObject object) {
        boolean change = false;
        for (Prediction prediction : object.getPredictions()) {
            change |= updatePredictionStates(object, prediction.getPlatform());
        }
        return change;
    }

    public static boolean updatePredictionStates(MediaObject mediaObject, Platform platform) {
        if (platform == null) {
            return false;
        }
        boolean changes = false;
        Prediction prediction = MediaObjects.getPrediction(platform, mediaObject.getPredictions());
        if (prediction != null) {
            Prediction.State requiredState = prediction.isPlannedAvailability() ? Prediction.State.ANNOUNCED : Prediction.State.NOT_ANNOUNCED;

            for (Location location : mediaObject.getLocations()) {
                Platform locationPlatform = location.getPlatform();

                if (locationPlatform == null) {
                    log.debug("Location has no explicit platform");
                    // this might be a good idea?
                    //log.debug("Location has no explicit platform. Taking it {} implicitely", Platform.INTERNETVOD);
                    //locationPlatform = Platform.INTERNETVOD;
                }
                if (locationPlatform == platform) {
                    if (location.isPublishable() && ! location.isDeleted()) {
                        requiredState = Prediction.State.REALIZED;
                        break;
                    }
                    if (location.wasUnderEmbargo() || location.isDeleted()) {
                        requiredState = Prediction.State.REVOKED;
                    }
                }
            }
            if (prediction.getState() != requiredState) {
                log.info("Set state of {} {} {} -> {}", mediaObject.getMid(), prediction, prediction.getState(), requiredState);
                prediction.setState(requiredState);
                changes = true;
            }
        }
        return changes;
    }

    private static AVAttributes getAVAttributes(String pubOption) {
        return getAVAttributes(pubOption, "");
    }


    private static AVAttributes getAVAttributes(String pubOption, String overrideFile) {

        Properties properties = new Properties();
        try {
            properties.load(Locations.class.getResourceAsStream("/authority.puboptions.properties"));
            if (StringUtils.isNotBlank(overrideFile)) {
                final InputStream inputStream;
                if (overrideFile.startsWith("classpath:")) {
                    inputStream = Locations.class.getResourceAsStream(overrideFile.substring("classpath:".length()));
                } else {
                    URL url = new URL(overrideFile);
                    inputStream = url.openStream();
                }
                if (inputStream != null) {
                    properties.load(inputStream);
                }
            }
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        String config = properties.getProperty(pubOption);
        if (config == null) {
            String message = "No publication option " + pubOption + " found in /authority.puboptions.properties";
            if (StringUtils.isNotBlank(overrideFile)) {
                message += " or '" + overrideFile + "'";
            }
            throw new IllegalArgumentException(message);
        }
        String[] split = config.split(",", 2);
        return AVAttributes.builder()
            .avFileFormat(AVFileFormat.valueOf(split[0]))
            .bitrate(split.length > 1 ? Integer.valueOf(split[1]) : null)
            .build();
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class RealizeResult {
        final MediaObject program;
        final boolean needed;
        final String reason;
        final Location location;
    }
}
