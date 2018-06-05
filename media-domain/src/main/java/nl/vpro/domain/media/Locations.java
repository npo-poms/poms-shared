package nl.vpro.domain.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
            Optional<Prediction> webonly = createWebOnlyPredictionIfNeeded(mediaObject);
            log.debug("Webonly : {}", webonly);
        }
        final Prediction existingPredictionForPlatform = mediaObject.getPrediction(platform);
        Encryption encryption;
        final List<Location> authorityLocations = new ArrayList<>();
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
                if (existingPredictionForPlatform.getEncryption() != Encryption.NONE) {
                    return Locations.RealizeResult.builder()
                        .needed(false)
                        .program(mediaObject)
                        .reason("NEP status is " + streamingPlatformStatus + " but request encryption is " + existingPredictionForPlatform.getEncryption())
                        .build();
                } else {
                    if (existingPredictionForPlatform.getEncryption() != Encryption.DRM) {
                        createDrmImplicitely(mediaObject, platform, authorityLocations);
                        if (authorityLocations.isEmpty()) {
                            return Locations.RealizeResult.builder()
                                .needed(false)
                                .program(mediaObject)
                                .reason("NEP status is " + streamingPlatformStatus + " but request encryption is " + existingPredictionForPlatform.getEncryption())
                                .build();
                        } else {
                             return Locations.RealizeResult.builder()
                                .needed(true)
                                .program(mediaObject)
                                .reason("NEP status is " + streamingPlatformStatus + " but request encryption is " + existingPredictionForPlatform.getEncryption())
                                .build();
                        }
                    }

                }
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


        Location authorityLocation = getAuthorityLocation(mediaObject, platform, encryption, "For " + encryption);
        if (authorityLocation != null) {
            authorityLocations.add(authorityLocation);
            updateLocationAndPredictions(authorityLocation, mediaObject, platform, getAVAttributes("nep"), OwnerType.AUTHORITY, new HashSet<>());
        }

        //MSE-3992
        if (encryption != Encryption.DRM) {
            createDrmImplicitely(mediaObject, platform, authorityLocations);
        }

        if (authorityLocations.isEmpty()) {
            return Locations.RealizeResult.builder()
                .needed(false)
                .program(mediaObject)
                .reason("NEP status is " + streamingPlatformStatus + " but no existing locations or predictions matched")
                .build();
        }

        return Locations.RealizeResult.builder()
            .needed(true)
            .locations(authorityLocations)
            .program(mediaObject)
            .build();
    }

    private static void createDrmImplicitely(MediaObject mediaObject, Platform platform, List<Location> authorityLocations) {
            Location authorityLocation2 = getAuthorityLocation(mediaObject, platform, Encryption.DRM, "Encryption is not drm, so make one with DRM too");
            if (authorityLocation2 != null) {
                authorityLocations.add(authorityLocation2);
                updateLocationAndPredictions(authorityLocation2, mediaObject, platform, getAVAttributes("nep"), OwnerType.AUTHORITY, new HashSet<>());
            }
    }

    private static Location getAuthorityLocation(MediaObject mediaObject, Platform platform, Encryption encryption, String reason) {
        String locationUrl = createLocationUrl(mediaObject, platform, encryption, "nep");
        if (locationUrl == null) {
            return null;
            // I think this cannot happen
        }

        // Checks if this exaction url is available already with correct owne?
        Location authorityLocation = mediaObject.findLocation(locationUrl, OwnerType.AUTHORITY);
        final Prediction existingPredictionForPlatform = mediaObject.getPrediction(platform);

        if (authorityLocation == null) {
            // no, just check platform then.
            authorityLocation = getAuthorityLocationsForPlatform(mediaObject, platform).stream()
                .filter(l -> getEncryptionFromProgramUrl(l) == encryption)
                .findFirst().
                orElse(null);
        }
        if (authorityLocation == null) {
            authorityLocation = createLocation(mediaObject, existingPredictionForPlatform, locationUrl);
            if (authorityLocation == null) {
                log.debug("Not created new streaming platform location {} {} for mediaObject {}", locationUrl, platform, mediaObject.getMid());
                return null;
            } else {
                log.info("creating new streaming platform location {} {} for mediaObject {} because {}", locationUrl, platform, mediaObject.getMid(), reason);
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
        return authorityLocation;

    }


    public static void realizeAndRevokeLocationsIfNeeded(MediaObject media, Platform platform) {
        Locations.removeLocationForPlatformIfNeeded(media, platform);
        Locations.realizeStreamingPlatformIfNeeded(media, platform);
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
        String locationUrl = createLocationUrl(program, platform, encryption, pubOptie);
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

    /**
     * Create a new location url. Doesn't change the mediaobject.
     */
    private static String createLocationUrl(MediaObject program, Platform platform, Encryption encryption, String pubOptie) {
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
        List<Location> existingPlatformLocations = getAuthorityLocationsForPlatform(mediaObject, platform);
        Prediction existingPredictionForPlatform = mediaObject.getPrediction(platform);
        StreamingStatus streamingPlatformStatus = mediaObject.getStreamingPlatformStatus();
        for (Location existingPlatformLocation : existingPlatformLocations) {
            if (!existingPredictionForPlatform.isPlannedAvailability()) {
                mediaObject.removeLocation(existingPlatformLocation);
            } else if (!streamingPlatformStatus.matches(existingPredictionForPlatform)) {
                mediaObject.removeLocation(existingPlatformLocation);
            } else if (existingPredictionForPlatform.getEncryption() == null && StreamingStatus.preferredEncryption(streamingPlatformStatus) != getEncryptionFromProgramUrl(existingPlatformLocation)) {
                mediaObject.removeLocation(existingPlatformLocation);
            } else {
                log.info("{} does not need to be removed", existingPlatformLocation);
            }
        }
        updatePredictionStates(mediaObject, platform);
    }

    private static Encryption getEncryptionFromProgramUrl(Location location) {
        String url = location.getProgramUrl();
        if (url.startsWith("npo+drm")) {
            return Encryption.DRM;
        } else {
            return Encryption.NONE;
        }
    }

    /**
     * If a mediaobject has BROADCASTER INTERNETVOD locations (which are not deleted)
     * then we need to have INTERNETVOD predictoin which can be set to 'REALIZED'.
     *
     * This is not always the case, this method can correct that.
     */
    public static Optional<Prediction> createWebOnlyPredictionIfNeeded(MediaObject mediaObject) {
        Set<Location> existingWebonlyLocations = mediaObject.getLocations().stream()
            .filter(l -> l.getOwner() == OwnerType.BROADCASTER)
            .filter(l -> Platform.INTERNETVOD.matches(l.getPlatform()))
            .filter(l -> ! l.isDeleted())
            .collect(Collectors.toSet());
        Prediction existingPrediction = mediaObject.getPrediction(Platform.INTERNETVOD);
        if (existingPrediction == null && ! existingWebonlyLocations.isEmpty()) {
            // yes, no prediction found, but one is expected because there are matching locations
            Prediction prediction = mediaObject.findOrCreatePrediction(Platform.INTERNETVOD);
            prediction.setPlannedAvailability(true);
            prediction.setEncryption(null);

            Iterator<Location> i = existingWebonlyLocations.iterator();
            Location first = i.next();
            Embargos.copyIfLessRestrictedOrTargetUnset(first, prediction);
            i.forEachRemaining((l) -> Embargos.copyIfLessRestricted(l, prediction));

            return Optional.of(prediction);
        } else {
            return Optional.ofNullable(existingPrediction);
        }
    }

    private static List<Location> getAuthorityLocationsForPlatform(MediaObject mediaObject, Platform platform){
        return mediaObject.getLocations().stream()
            .filter(l -> l.getOwner() == OwnerType.AUTHORITY && l.getPlatform() == platform)
            .collect(Collectors.toList());
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
    @lombok.Builder
    @Data
    public static class RealizeResult {
        final MediaObject program;
        final boolean needed;
        final String reason;
        final List<Location> locations;
        @lombok.Builder.Default
        CompletableFuture<?> extraTasks = CompletableFuture.completedFuture(null);
    }
}
