package nl.vpro.domain.media;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.domain.Embargos;
import nl.vpro.domain.media.support.OwnerType;

import static nl.vpro.domain.Changeables.instant;

/**
 * Utilities related to poms 'authoritative locations'. I.e. {@link MediaObject#getLocations() locations} that are implicitly added (because of some notification from an external system, currently NEP), with {@link OwnerType owner} {@link OwnerType#AUTHORITY}
 *
 * @author Michiel Meeuwissen
 * @since 5.7
 */
@Slf4j
public class AuthorityLocations {



    private final String audioTemplate;

    @Inject
    public AuthorityLocations(@Value("${authority.locations.audioTemplate:https://entry.cdn.npoaudio.nl/handle/%s.mp3}") String audioTemplate) {
        this.audioTemplate = audioTemplate == null ? "https://entry.cdn.npoaudio.nl/handle/%s.mp3" : audioTemplate;
    }


    /**
     * This will be called if for a certain platform and 'puboptie' a 'notify' is received. E.g. a 'projecm' notify message would trigger this.
     * TODO I think this is not used any more, we receive notification from NEP (which we assign 'pubOptie=nep')
     *
     */
    public Program realize(
        @NonNull Program program,
        @NonNull Platform platform,
        @NonNull String pubOptie,
        @NonNull OwnerType owner,
        @NonNull Set<OwnerType> replaces) {
        final Prediction prediction = program.getPrediction(platform);
        final StreamingStatus streamingStatus = program.getStreamingPlatformStatus();

        Encryption encryption;
        if (prediction != null) {
            encryption = prediction.getEncryption();
        }  else {
            log.warn("Realizing without prediction");
            encryption = StreamingStatus.preferredEncryption(streamingStatus);
        }
        return addLocation(program, platform, encryption, pubOptie, owner, replaces);
    }

    /**
     * This will be called per platform if an NEP notify is received.
     */
    public AuthorityLocations.RealizeResult realizeStreamingPlatformIfNeeded(
        @NonNull MediaObject mediaObject,
        @NonNull AVType avType,
        @NonNull Platform platform,
        @NonNull Predicate<Location> locationPredicate,
        @NonNull Instant now) {
        if (mediaObject.getAVType() == null) {
            return cannotRealize(mediaObject, now);
        }

        return realizeStreamingPlatformIfNeededAudioAndVideo(avType, mediaObject, platform, locationPredicate, now);
     }

    AuthorityLocations.RealizeResult cannotRealize(
        @NonNull MediaObject mediaObject,
        @NonNull Instant now) {
        return AuthorityLocations.RealizeResult.builder()
            .needed(false)
            .program(mediaObject)
            .reason("Only audio and video media objects currently can be realized. This one is " + mediaObject.getAVType())
            .build();
    }


    private AuthorityLocations.RealizeResult realizeStreamingPlatformIfNeededAudioAndVideo(
        @NonNull AVType avType,
        @NonNull MediaObject mediaObject,
        @NonNull Platform platform,
        @NonNull Predicate<Location> locationPredicate,
        @NonNull Instant now) {
        final String pubOptie =   avType == AVType.VIDEO ? "nep" : "nepaudio";
        if (platform == Platform.INTERNETVOD) {
            Optional<Prediction> webonly = createWebOnlyPredictionIfNeeded(mediaObject);
            log.debug("Webonly : {}", webonly);
        }
        final Prediction existingPredictionForPlatform = mediaObject.getPrediction(platform);
        Encryption encryption;

        final StreamingStatus streamingPlatformStatus = mediaObject.getStreamingPlatformStatus();

        final List<Location> authorityLocations = new ArrayList<>();
        if (existingPredictionForPlatform != null) {
            if (!existingPredictionForPlatform.isPlannedAvailability()) {
                log.debug("Can't realize {} for {} because no availability planned", mediaObject, platform);
                existingPredictionForPlatform.setState(Prediction.State.NOT_ANNOUNCED);
                return AuthorityLocations.RealizeResult.builder()
                    .needed(false)
                    .program(mediaObject)
                    .reason("NEP status is " + streamingPlatformStatus + " but no availability planned ")
                    .build();
            }
            if (!streamingPlatformStatus.matches(existingPredictionForPlatform.getEncryption())) {
                log.debug("Can't realize {} for {} because incorrect encryption", mediaObject, platform);
                if (existingPredictionForPlatform.getEncryption() != Encryption.NONE) {
                    return AuthorityLocations.RealizeResult.builder()
                        .needed(false)
                        .program(mediaObject)
                        .reason("NEP status is " + streamingPlatformStatus + " but request encryption is " + existingPredictionForPlatform.getEncryption())
                        .build();
                } else {
                    // None or Null
                    if (existingPredictionForPlatform.getEncryption() != Encryption.DRM) {
                        createDrmImplicitly(avType, mediaObject, platform, authorityLocations, locationPredicate, now, null);
                        if (authorityLocations.isEmpty()) {
                            return AuthorityLocations.RealizeResult.builder()
                                .needed(false)
                                .program(mediaObject)
                                .reason("NEP status is " + streamingPlatformStatus + " but request encryption is " + existingPredictionForPlatform.getEncryption())
                                .build();
                        } else {
                             return AuthorityLocations.RealizeResult.builder()
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
                log.info("Existing prediction {} has no encryption, falling back to {} ", existingPredictionForPlatform, encryption);
            }
        } else {
            log.debug("No prediction found for platform {} in {} ", platform, mediaObject);
            return AuthorityLocations.RealizeResult.builder()
                .needed(false)
                .program(mediaObject)
                .reason("NEP status is " + streamingPlatformStatus + " but no prediction found for platform " + platform)
                .build();
        }
        Location authorityLocation = getOrCreateAuthorityLocation(avType, mediaObject, platform, encryption, "For " + encryption, locationPredicate, null);
        if (authorityLocation != null) {
            authorityLocations.add(authorityLocation);
            updateLocationAndPredictions(authorityLocation, mediaObject, platform, getAVAttributes(pubOptie).orElseThrow(() -> new RuntimeException("not found nep puboptie")), OwnerType.AUTHORITY, new HashSet<>(), now);
        }

        //MSE-3992
        if (encryption != Encryption.DRM) {
            createDrmImplicitly(avType, mediaObject, platform, authorityLocations, locationPredicate, now, null);
        }

        if (authorityLocations.isEmpty()) {
            return AuthorityLocations.RealizeResult.builder()
                .needed(false)
                .program(mediaObject)
                .reason("NEP status is " + streamingPlatformStatus + " but no existing locations or predictions matched")
                .extraTasks(null)
                .build();
        }

        return RealizeResult.builder()
            .needed(true)
            .locations(authorityLocations)
            .program(mediaObject)
            .build();
    }

    private void createDrmImplicitly(
        AVType avType,
        MediaObject mediaObject,
        Platform platform,
        List<Location> authorityLocations,
        Predicate<Location> locationPredicate,
        Instant now,
        Consumer<Runnable> extra) {
        if (avType == AVType.VIDEO) {
            Location authorityLocation2 = getOrCreateAuthorityLocation(avType, mediaObject, platform, Encryption.DRM, "Encryption is not drm, so make one with DRM too", locationPredicate, extra);
            if (authorityLocation2 != null) {
                authorityLocations.add(authorityLocation2);
                updateLocationAndPredictions(authorityLocation2, mediaObject, platform, getAVAttributes("nep").orElseThrow(() -> new RuntimeException("Not found nep puboptie")), OwnerType.AUTHORITY, new HashSet<>(), now);
            }
        } else {
            log.debug("DRM only supported for video");
        }
    }

    HttpClient client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .connectTimeout(Duration.ofSeconds(3))
        .build();
    private Location getOrCreateAuthorityLocation(AVType avType, MediaObject mediaObject, Platform platform, Encryption encryption, String reason, Predicate<Location> locationPredicate, Consumer<Runnable> consumer) {
        String locationUrl;
        Long byteSize = null;
        if (avType == AVType.VIDEO) {
            locationUrl = createLocationVideoUrl(mediaObject, platform, encryption, "nep");
        } else {
            locationUrl = String.format(audioTemplate, mediaObject.getMid());
            HttpRequest head = HttpRequest.newBuilder()

                .uri(URI.create(locationUrl))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build(); // .HEAD() in java 18
            try {
                HttpResponse<Void> send = client.send(head, HttpResponse.BodyHandlers.discarding());
                if (send.statusCode() == 200) {
                    byteSize = send.headers().firstValue("Content-Length").map(Long::valueOf).orElse(null);
                } else {
                    log.warn("HEAD {} returned {}", locationUrl, send.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                log.warn(e.getMessage(), e);
            }


        }
        if (locationUrl == null) {
            return null;
            // I think this cannot happen
        }

        // Checks if this exact url is available already with correct owner?
        Location authorityLocation = mediaObject.findLocation(locationUrl, OwnerType.AUTHORITY);
        final Prediction existingPredictionForPlatform = mediaObject.getPrediction(platform);

        // What if only owner is wrong?
        if (authorityLocation == null) {
            authorityLocation = mediaObject.findLocation(locationUrl);
            if (authorityLocation != null) {
                log.warn("Location {} had wrong owner. Setting it to authority now", authorityLocation);
                authorityLocation.setOwner(OwnerType.AUTHORITY);
            }
        }

        if (authorityLocation == null) {
            // no, just check platform then.
            authorityLocation = getAuthorityLocationsForPlatform(mediaObject, platform).stream()
                .filter(l -> getEncryptionFromProgramUrl(l) == encryption)
                .filter(locationPredicate)
                .findFirst().
                orElse(null);
        }
        if (authorityLocation == null) {
            authorityLocation = createLocation(mediaObject, existingPredictionForPlatform, locationUrl);
            log.info("Creating new streaming platform location {} {} for mediaObject {} because {}", locationUrl, platform, mediaObject.getMid(), reason);
            Embargos.copy(existingPredictionForPlatform, authorityLocation);
        } else {
            if (!locationUrl.equals(authorityLocation.getProgramUrl())) {
                log.info("Updating location {} {} for mediaObject {}", locationUrl, platform, mediaObject.getMid());
                authorityLocation.setProgramUrl(locationUrl);
            } else {
                log.debug("Location {} {} for mediaObject {} already exists", locationUrl, platform, mediaObject.getMid());
            }
            authorityLocation.setPlatform(platform);
            authorityLocation.setOwner(OwnerType.AUTHORITY);
        }
        if (byteSize != null) {
            authorityLocation.setByteSize(byteSize);
        }
        Instant streamingOffline =  mediaObject.getStreamingPlatformStatus().getOffline(authorityLocation.hasDrm());
        return authorityLocation;

    }



    @NonNull
    private static Location createLocation(final MediaObject mediaObject, final Prediction prediction, final String locationUrl){
        Location platformAuthorityLocation = new Location(locationUrl, OwnerType.AUTHORITY, prediction.getPlatform());
        platformAuthorityLocation.setPublishStartInstant(prediction.getPublishStartInstant());
        platformAuthorityLocation.setPublishStopInstant(prediction.getPublishStopInstant());
        mediaObject.addLocation(platformAuthorityLocation);
        return platformAuthorityLocation;
    }


    private Program addLocation(
        @NonNull Program program,
        @NonNull Platform platform,
        Encryption encryption,
        @NonNull String pubOptie, OwnerType owner,
        @NonNull Set<OwnerType> replaces) {
        final String locationUrl = createLocationVideoUrl(program, platform, encryption, pubOptie);
        if (locationUrl == null) {
            return program;
        }
        Optional<AVAttributes> avAttributes = getAVAttributes(pubOptie);
        if (avAttributes.isPresent()) {
            Location location = createOrFindLocation(program, locationUrl, owner, platform);
            updateLocationAndPredictions(location, program, platform, avAttributes.get(), owner, replaces, instant());
        } else {
            log.warn("Puboption {} is explicitly ignored, not adding location for {}", pubOptie, program);
        }
        return program;
    }


    private void updateLocationAndPredictions(Location location, MediaObject program, Platform platform, AVAttributes avAttributes, OwnerType owner, Set<OwnerType> replaces, Instant now) {
        location.setAvAttributes(avAttributes);
        if (replaces != null) {
            if (replaces.contains(location.getOwner())) {
                location.setOwner(owner);
            }
        }
        updatePredictionStates(program, platform, now);
    }

    /**
     * Create a new location url. Doesn't change the mediaobject.
     *
     * @param pubOptie Originally we got notifies with different puboptions. Now we get from NEP, and pubotion then is 'nep'.
     */
    private static String createLocationVideoUrl(MediaObject program, Platform platform, Encryption encryption, String pubOptie) {
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


    private Location createOrFindLocation(
        @NonNull Program program,
        @NonNull String locationUrl,
        @NonNull OwnerType owner,
        @NonNull Platform platform) {
        Location location = program.findLocation(locationUrl);
        if (location == null) {
            log.info("Creating new location {} {} {} for mediaObject {}", locationUrl, owner, platform, program.getMid());
            location = new Location(locationUrl, owner, platform);
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



    public void removeLocationForPlatformIfNeeded(MediaObject mediaObject, Platform platform, Predicate<Location> locationPredicate, Instant now){
        final List<Location> existingPlatformLocations = getAuthorityLocationsForPlatform(mediaObject, platform);
        final Prediction existingPredictionForPlatform = mediaObject.getPrediction(platform);
        final StreamingStatus streamingPlatformStatus = mediaObject.getStreamingPlatformStatus();
        final List<Encryption> encryptions = streamingPlatformStatus.getEncryptionsForPrediction(existingPredictionForPlatform);
        for (Location existingPlatformLocation : existingPlatformLocations) {
            if (! locationPredicate.test(existingPlatformLocation)) {
                log.info("Skipped for consideration {}", existingPlatformLocation);
                continue;
            }
             if (! encryptions.contains(getEncryptionFromProgramUrl(existingPlatformLocation))) {
                 mediaObject.removeLocation(existingPlatformLocation);
                 log.info("Removing {}", existingPlatformLocation);
            } else {
                 log.debug("Letting {}", existingPlatformLocation);
             }

        }
        updatePredictionStates(mediaObject, platform, now);
    }

    private static Encryption getEncryptionFromProgramUrl(Location location) {
        String url = location.getProgramUrl();
        if (url.startsWith("npo+drm")) {
            return Encryption.DRM;
        } else {
            return Encryption.NONE;
        }
    }


    private String getAudioPrefix() {
        URI uri = URI.create(String.format(audioTemplate, "MID"));
        return uri.getScheme() + "://" + uri.getHost();
    }
    /**
     * Creates a prediction because of a NEP notification.
     * <p>
     * If a mediaobject has INTERNETVOD locations (which are not deleted) (which were not created because of NEP)
     * <p>
     * then we need to have INTERNETVOD prediction which can be set to 'REALIZED'.
     * <p>
     * This is not always the case, this method can correct that.
     */
    public  Optional<Prediction> createWebOnlyPredictionIfNeeded(MediaObject mediaObject) {
        String audioPrefix = getAudioPrefix();
        Set<Location> existingWebonlyLocations = mediaObject.getLocations().stream()
            .filter(l -> Platform.INTERNETVOD.matches(l.getPlatform())) // l == null || l == internetvod
            .filter(l -> ! l.getProgramUrl().startsWith("npo:")) // not created because of NEP itself.
            .filter(l -> ! l.getProgramUrl().startsWith(audioPrefix)) // not created because of NEP itself.
            .filter(l -> ! l.isDeleted())// ignore deleted of course
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

    private  static List<Location> getAuthorityLocationsForPlatform(MediaObject mediaObject, Platform platform){
        return mediaObject.getLocations().stream()
            .filter(l -> l.getOwner() == OwnerType.AUTHORITY && l.getPlatform() == platform)
            .collect(Collectors.toList());
    }


    /**
     * After locations are added or removed, this may have effect on the state of the {@link MediaObject#getPredictions() prediction records}
     */
    public static boolean updatePredictionStates(MediaObject object, Instant now) {
        boolean change = false;
        for (Prediction prediction : object.getPredictions()) {
            change |= updatePredictionStates(object, prediction.getPlatform(), now);
        }
        return change;
    }

    public static boolean updatePredictionStates(MediaObject mediaObject, Platform platform, Instant now) {
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
                    //log.debug("Location has no explicit platform. Taking it {} implicitly", Platform.INTERNETVOD);
                    //locationPlatform = Platform.INTERNETVOD;
                }
                if (locationPlatform == platform) {
                    if (location.isPublishable(now) && ! location.isDeleted()) {
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

    private static Optional<AVAttributes> getAVAttributes(String pubOption) {
        return getAVAttributes(pubOption, "");
    }


    private static Optional<AVAttributes> getAVAttributes(String pubOption, String overrideFile) {

        Properties properties = new Properties();
        try {
            properties.load(AuthorityLocations.class.getResourceAsStream("/authority.puboptions.properties"));
            if (StringUtils.isNotBlank(overrideFile)) {
                final InputStream inputStream;
                if (overrideFile.startsWith("classpath:")) {
                    inputStream = AuthorityLocations.class.getResourceAsStream(overrideFile.substring("classpath:".length()));
                } else {
                    URL url = URI.create(overrideFile).toURL();
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
        if (config.isEmpty()) {
            return Optional.empty();
        } else {
            String[] split = config.split(",", 2);

            return Optional.of(AVAttributes.builder()
                .avFileFormat(AVFileFormat.valueOf(split[0]))
                .bitrate(split.length > 1 ? Integer.valueOf(split[1]) : null)
                .build());
        }
    }

    @AllArgsConstructor
    @lombok.Builder
    @Data
    public static class RealizeResult {
        final MediaObject program;
        final boolean needed;
        final String reason;
        final List<Location> locations;
        final AVType avType;
        @lombok.Builder.Default
        CompletableFuture<?> extraTasks = CompletableFuture.completedFuture(null);
    }
}
