package nl.vpro.domain.media;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.functional.TriConsumer;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.domain.Changeables;
import nl.vpro.domain.Embargos;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.logging.simple.Level;
import nl.vpro.util.HttpConnectionUtils;

import static nl.vpro.domain.Changeables.instant;
import static nl.vpro.domain.media.Encryption.DRM;
import static nl.vpro.domain.media.Platform.INTERNETVOD;

/**
 * Utilities related to poms 'authoritative locations'. I.e. {@link MediaObject#getLocations() locations} that are implicitly added (because of some notification from an external system, currently NEP), with {@link OwnerType owner} {@link OwnerType#AUTHORITY}
 *
 * @author Michiel Meeuwissen
 * @since 5.7
 */
@Slf4j
public class AuthorityLocations {

    /**
     * These are the relevant 'stream types' that are relevant for POMS.
     * Basically it is like {@link AVType} but it is more about {@link Location}. For now only used to pass the information
     * about the CDN notification around. It would make sense to something like in {@link Location}or {@link AVAttributes} as well.
     *
     * @since 7.7
     */
    public enum StreamType {
        VOD(AVType.VIDEO, true),
        AOD(AVType.AUDIO, false);

        @Getter
        final AVType defaultAVType;

        final boolean supportsDRM;

        StreamType(AVType defaultAVType, boolean supportsDRM) {
            this.defaultAVType = defaultAVType;
            this.supportsDRM = supportsDRM;
        }

        public boolean supportsDRM() {
            return supportsDRM;
        }
    }


    private final String audioTemplate;
    @Getter(AccessLevel.PRIVATE)
    private final String audioPrefix;

    @Inject
    public AuthorityLocations(
        @Value("${authority.locations.audioTemplate:https://entry.cdn.npoaudio.nl/handle/%s.mp3}") String audioTemplate) {
        this.audioTemplate = audioTemplate == null ? "https://entry.cdn.npoaudio.nl/handle/%s.mp3" : audioTemplate;
        URI uri = URI.create(String.format(this.audioTemplate, "MID"));
        this.audioPrefix =  uri.getScheme() + "://" + uri.getHost();
    }



    /**
     * This will be called per platform if an NEP notify is received.
     */
    public RealizeResult realizeStreamingPlatformIfNeeded(
        @NonNull MediaObject mediaObject,
        @NonNull Platform platform) {
        if (platform == INTERNETVOD) {
            final Optional<Prediction> webonly = createWebOnlyPredictionIfNeeded(mediaObject);
            log.debug("Webonly : {}", webonly);
        }

        final Prediction existingPredictionForPlatform = mediaObject.getPrediction(platform);


        if (existingPredictionForPlatform == null) {
            log.debug("No prediction found for platform {} in {} ", platform, mediaObject);
            return RealizeResult.builder()
                .needed(false)
                .program(mediaObject)
                .reason("NEP status is " + mediaObject.getStreamingPlatformStatus() + " but no prediction found for platform " + platform)
                .build();
        } else {
            Optional<RealizeResult> realizeResult = checkExistingPrediction(existingPredictionForPlatform, mediaObject, platform);
            if (realizeResult.isPresent()) { // no need to realize
                return realizeResult.get();
            }
        }
        final List<Location> authorityLocations = getOrCreateAuthorityLocations(mediaObject, existingPredictionForPlatform.getEncryption());

        mediaObject.correctPredictions();
        return RealizeResult.builder()
            .needed(true)
            .locations(authorityLocations)
            .program(mediaObject)
            .build();
     }


    private Optional<AuthorityLocations.RealizeResult> checkExistingPrediction(Prediction  existingPredictionForPlatform, MediaObject mediaObject, Platform platform) {
        final StreamingStatus streamingPlatformStatus = mediaObject.getStreamingPlatformStatus();
        final Encryption encryption = existingPredictionForPlatform.getEncryption();

        if (!existingPredictionForPlatform.isPlannedAvailability()) {
            log.debug("Can't realize {} for {} because no availability planned", mediaObject, platform);
            existingPredictionForPlatform.setState(Prediction.State.NOT_ANNOUNCED);
            return Optional.of(AuthorityLocations.RealizeResult.builder()
                .needed(false)
                .program(mediaObject)
                .reason("NEP status is " + streamingPlatformStatus + " but no availability planned ")
                .build());
        }
        if (!streamingPlatformStatus.matches(encryption)) {
            if (encryption != null && encryption != Encryption.NONE) {
                log.debug("Can't realize {} for {} because incorrect encryption", mediaObject, platform);
                // DRM
                return Optional.of(AuthorityLocations.RealizeResult.builder()
                    .needed(false)
                    .program(mediaObject)
                    .reason("NEP status is " + streamingPlatformStatus + " but request encryption is " + existingPredictionForPlatform.getEncryption())
                    .build());
            }
        }
        // no realize
        return Optional.empty();
    }


    private static void makeLocationPublishable(Location authorityLocation) {
        authorityLocation.setPublishStopInstant(null);
        if (authorityLocation.isDeleted()) {
            authorityLocation.setWorkflow(Workflow.FOR_PUBLICATION);
        }
    }

    private List<Location> getOrCreateAuthorityLocations(MediaObject mediaObject, Encryption encryption) {

        final List<Location> result = new ArrayList<>();
        final StreamingStatus streamingPlatformStatus = mediaObject.getStreamingPlatformStatus();

        if (streamingPlatformStatus.hasAudio() && ! (encryption == DRM)) {
            if (mediaObject.getMid() != null) {
                String locationUrl = audioTemplate.formatted(mediaObject.getMid());
                Location authorityLocation = findCreateOrUpdateAutorityLocation(mediaObject, Platform.INTERNETAOD, locationUrl, "nepaudio");
                log.debug("matched {}", authorityLocation);
                makeLocationPublishable(authorityLocation);
                result.add(authorityLocation);
            } else {
                log.warn("{} has no mid, cannot make audio url", mediaObject);
            }
        }
        if (streamingPlatformStatus.hasDrm() || encryption == DRM) {
            for (Platform platform: Platform.values()) {
                Prediction prediction = mediaObject.getPrediction(platform);
                if (prediction != null && prediction.isPlannedAvailability()) { // drms are always made
                    if (mediaObject.getMid() != null) {
                        String locationUrl = createLocationVideoUrl(mediaObject.getStreamingPlatformStatus(), mediaObject.getMid(), platform, DRM, "nep");
                        Location authorityLocation = findCreateOrUpdateAutorityLocation(mediaObject, platform, locationUrl, "nep");
                        log.debug("matched {}", authorityLocation);
                        makeLocationPublishable(authorityLocation);
                        result.add(authorityLocation);
                    } else {
                        log.warn("{} has no mid, cannot make location url", mediaObject);
                    }
                }
            }
        }
        if (streamingPlatformStatus.hasWithoutDrm()) {
            for (Platform platform: Platform.values()) {
                Prediction prediction = mediaObject.getPrediction(platform);
                if (prediction != null && prediction.isPlannedAvailability() && (prediction.getEncryption() == null || prediction.getEncryption() == Encryption.NONE)) {
                    if (mediaObject.getMid() != null) {

                        String locationUrl = createLocationVideoUrl(mediaObject.getStreamingPlatformStatus(), mediaObject.getMid(), platform, Encryption.NONE, "nep");
                        Location authorityLocation = findCreateOrUpdateAutorityLocation(mediaObject, platform, locationUrl, "nep");
                        log.debug("matched {}", authorityLocation);
                        makeLocationPublishable(authorityLocation);
                        result.add(authorityLocation);
                    } else {
                        log.warn("{} has no mid, cannot make location url", mediaObject);

                    }
                }
            }
        }


        //Instant streamingOffline =  mediaObject.getStreamingPlatformStatus().getOffline(authorityLocation.hasDrm());
        return result;

    }

    private Location findCreateOrUpdateAutorityLocation(MediaObject mediaObject, Platform platform, String locationUrl, String pubOption) {
        Location authorityLocation = mediaObject.findLocation(locationUrl, OwnerType.AUTHORITY);
        if (authorityLocation == null) {
            authorityLocation = mediaObject.findLocation(locationUrl);
            if (authorityLocation != null) {
                log.warn("Location {} had wrong owner. Setting it to authority now", authorityLocation);
                authorityLocation.setOwner(OwnerType.AUTHORITY);
            }
        }
        if (authorityLocation == null) {
            addLocation(mediaObject, platform, locationUrl, pubOption, OwnerType.AUTHORITY);
            authorityLocation = mediaObject.findLocation(locationUrl, OwnerType.AUTHORITY);
        }
        return authorityLocation;
    }

    private MediaObject addLocation(
        @NonNull MediaObject program,
        @NonNull Platform platform,
        String locationUrl,
        @NonNull String pubOptie,
        OwnerType owner) {
        if (locationUrl == null) {
            return program;
        }
        Optional<AVAttributes> avAttributes = getAVAttributes(pubOptie);
        if (avAttributes.isPresent()) {
            Location location = createOrFindLocation(program, locationUrl, owner, platform, avAttributes.get());
            updateLocationAndPredictions(
                location,
                program,
                platform,
                owner,
                new HashSet<>(),
                instant())
            ;
        } else {
            log.warn("Puboption {} is explicitly ignored, not adding location for {}", pubOptie, program);
        }
        return program;
    }

    private void updateLocationAndPredictions(Location location, MediaObject program, Platform platform, OwnerType owner, Set<OwnerType> replaces, Instant now) {
        if (replaces != null && replaces.contains(location.getOwner())) {
            location.setOwner(owner);
        }

        updatePredictionStates(program, platform, now);
    }

    /**
     * Create a new location url. Doesn't change the mediaobject.
     *
     * @param pubOptie Originally we got notifies with different puboptions. Now we get from NEP, and puboption then is 'nep'.
     */
    private static String createLocationVideoUrl(StreamingStatus streamingStatus,  @NonNull String mid, Platform platform, Encryption encryption, String pubOptie) {
        String baseUrl = getBaseVideoUrl(platform, encryption, pubOptie);
        return baseUrl + mid;
    }


    private static @NonNull String getBaseVideoUrl(Platform platform, Encryption encryption, String publicationOption) {
        if ("nep".equals(publicationOption)) {
            boolean drm = encryption == DRM;
            String scheme = drm ? "npo+drm" : "npo";
            return scheme + "://" + platform.name().toLowerCase() + ".omroep.nl/";
        } else if (platform == INTERNETVOD && "adaptive".equals(publicationOption)) {
            return "odip+http://odi.omroep.nl/video/" + publicationOption + "/";
        } else if (platform == INTERNETVOD) {
            return "odi+http://odi.omroep.nl/video/" + publicationOption + "/";
        } else if (platform == Platform.PLUSVOD) {
            return "sub+http://npo.npoplus.nl/video/" + publicationOption + "/";
        } else if (platform == Platform.TVVOD) {
            return "sub+http://tvvod.omroep.nl/video/" + publicationOption + "/";
        } else {
            throw new UnsupportedOperationException("Unsupported platform " + platform + " with puboption " + publicationOption);
        }
    }


    private Location createOrFindLocation(
        @NonNull MediaObject program,
        @NonNull String locationUrl,
        @NonNull OwnerType owner,
        @NonNull Platform platform,
        @NonNull AVAttributes avAttributes) {
        Location location = program.findLocation(locationUrl);
        if (location == null) {
            log.info("Creating new location {} {} {} for mediaObject {}", locationUrl, owner, platform, program.getMid());
            location = new Location(locationUrl, owner, platform, avAttributes);
            location.headRequest();
            program.addLocation(location);
            Prediction prediction = program.getPredictionWithoutFixing(platform);
            if (prediction.isNew()) {
                program.getPrediction(platform).setAuthority(Authority.SYSTEM);
                log.debug("Found new prediction, so made it SYSTEM {}", prediction);
            }
        } else {
            log.debug("updating location {} {} for mediaObject {}", locationUrl, owner, program.getMid());
            location.headRequest();
            location.setPlatform(platform);
        }
        return location;
    }



    public void removeLocationForPlatformIfNeeded(
        @NonNull final MediaObject mediaObject,
        Platform platform,
        Predicate<Location> locationPredicate,
        Instant now ){
        final List<Location> existingPlatformLocations = getAuthorityLocationsForPlatform(mediaObject, platform);
        final Prediction existingPredictionForPlatform = mediaObject.getPrediction(platform);
        final StreamingStatus streamingPlatformStatus = mediaObject.getStreamingPlatformStatus();
        final List<Encryption> encryptions = streamingPlatformStatus.getEncryptionsForPrediction(existingPredictionForPlatform);
        for (Location existingPlatformLocation : existingPlatformLocations) {
            if (! locationPredicate.test(existingPlatformLocation)) {
                log.info("Skipped for consideration {}", existingPlatformLocation);
                continue;
            }
            if (isAudioUrl(existingPlatformLocation)) {
                if (! streamingPlatformStatus.hasAudio() || !  encryptions.contains(Encryption.NONE)) {
                    existingPlatformLocation.setWorkflow(Workflow.DELETED);
                    log.info("Removing {}", existingPlatformLocation);
                }
                continue;
            } else {
                if (!encryptions.contains(getVideoEncryptionFromProgramUrl(existingPlatformLocation))) {
                    existingPlatformLocation.setWorkflow(Workflow.DELETED);
                    log.info("Removing {}", existingPlatformLocation);
                } else {
                    log.debug("Letting {}", existingPlatformLocation);
                }
            }
        }
        updatePredictionStates(mediaObject, platform, now);
    }


    public Program addVideoLocation(@NonNull Program program, @NonNull Platform platform, Encryption encryption, @NonNull String pubOptie, OwnerType owner, @NonNull Set<OwnerType> replaces) {
        String locationUrl = this.createLocationVideoUrl(program, platform, encryption, pubOptie);
        if (locationUrl == null) {
            return program;
        } else {
            Optional<AVAttributes> avAttributes = getAVAttributes(pubOptie);
            if (avAttributes.isPresent()) {
                Location location = this.createOrFindLocation(program, locationUrl, owner, platform, avAttributes.get());
                this.updateLocationAndPredictions(
                    location,
                    program,
                    platform,
                    owner,
                    replaces,
                    Changeables.instant()
                );
            } else {
                log.warn("Puboption {} is explicitly ignored, not adding location for {}", pubOptie, program);
            }

            return program;
        }
    }


    private static Encryption getVideoEncryptionFromProgramUrl(Location location) {
        String url = location.getProgramUrl();
        if (url.startsWith("npo+drm")) {
            return DRM;
        } else {
            return Encryption.NONE;
        }
    }

    private  boolean isAudioUrl(Location location) {
        String url = location.getProgramUrl();
        return url.startsWith(getAudioPrefix());
    }

    private  boolean isVideoUrl(Location location) {
        String url = location.getProgramUrl();
        return url.startsWith("npo");
    }

    /**
     * Sometimes a {@link MediaObject mediaobject} already has locations, but no prediction which belongs to that.
     * This implicitly creates it then.
     * Existing embargos on the locations are copied to the prediction (least restricted)
     *
     */
    Optional<Prediction> createWebOnlyPredictionIfNeeded(MediaObject mediaObject) {
        final Prediction existingPrediction = mediaObject.getPrediction(INTERNETVOD);
        if (existingPrediction == null) {
            final String audioPrefix = this.getAudioPrefix();
            final Set<Location> existingWebonlyLocations = mediaObject.getLocations().stream().filter(
                    (l) -> INTERNETVOD.matches(l.getPlatform()))
                .filter((l) -> ! isVideoUrl(l)) // ignore locations though that are made because of notify itself
                .filter((l) -> ! isAudioUrl(l)) // also valid for this one, this is AOD
                .filter((l) -> ! l.isDeleted()) // deleted, so assume they shouldn't have existing in the first place
                .collect(Collectors.toSet());
            if (!existingWebonlyLocations.isEmpty()) {
                Prediction prediction = mediaObject.findOrCreatePrediction(INTERNETVOD);
                prediction.setPlannedAvailability(true);
                prediction.setEncryption(null);
                Iterator<Location> i = existingWebonlyLocations.iterator();
                Location first = i.next();
                Embargos.copyIfLessRestrictedOrTargetUnset(first.getOwnEmbargo(), prediction.getOwnEmbargo());
                i.forEachRemaining((l) -> {
                    Embargos.copyIfLessRestricted(l.getOwnEmbargo(), prediction.getOwnEmbargo());
                });
                return Optional.of(prediction);
            }
        }
        return Optional.ofNullable(existingPrediction);
    }

    private  static List<Location> getAuthorityLocationsForPlatform(
        final @NonNull MediaObject mediaObject, Platform platform){
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
        final boolean[] changes = new boolean[] {false};
        final Prediction prediction = MediaObjects.getPrediction(platform, mediaObject.getPredictions());
        if (prediction != null) {
            MediaObjects.correctPrediction(prediction, mediaObject, Level.DEBUG, now, (ps, p) -> {changes[0] = true;});
        } else {
            log.debug("No prediction for {} {}", platform, mediaObject);
        }
        return changes[0];
    }

    private static Optional<AVAttributes> getAVAttributes(String pubOption) {
        return getAVAttributes(pubOption, "");
    }


    private static Optional<AVAttributes> getAVAttributes(
        String pubOption,
        String overrideFile) {
        final Properties properties = new Properties();
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

            AVFileFormat avFileFormat = AVFileFormat.valueOf(split[0]);
            AVAttributes.Builder builder = AVAttributes.builder()
                .avFileFormat(avFileFormat)
                .bitrate(split.length > 1 ? Integer.valueOf(split[1]) : null);


            return Optional.of(builder.build());
        }
    }

    public static final TriConsumer<String, HttpResponse<Void>, Exception> BYTE_SIZE_CONSUMER = (locationUrl, response, exception) -> {
        if (exception != null) {
            log.warn("For {}: {} {}", locationUrl, exception.getClass().getName(), exception.getMessage());
        }
        if (response != null) {
            int statusCode = response.statusCode();

            org.slf4j.event.Level level = switch (statusCode) {
                case 200 -> org.slf4j.event.Level.DEBUG;
                case 451 -> org.slf4j.event.Level.INFO; // P0MS-244
                default -> org.slf4j.event.Level.WARN;
            };
            log.atLevel(level).log("HEAD {} returned {}", locationUrl, response.statusCode());

        }
        // do nothing
    };
    /**
     * Executes a HEAD request to determine the bytes size of given URL. For mp3's and such.
     * @since 7.7
     */
    public static OptionalLong getBytesize(String locationUrl) {
        return HttpConnectionUtils.getOptionalByteSize(locationUrl, BYTE_SIZE_CONSUMER.withArg1(locationUrl));
    }

     private String createLocationVideoUrl(MediaObject program, Platform platform, Encryption encryption, String pubOptie) {
        String baseUrl = this.getBaseVideoUrl(platform, encryption, pubOptie);
        return baseUrl == null ? null : baseUrl + program.getMid();
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

        public static class Builder {
            public Builder loggingReason(String reason, Consumer<String> logger) {
                logger.accept(reason);
                return reason(reason);
            }
        }
    }
}
