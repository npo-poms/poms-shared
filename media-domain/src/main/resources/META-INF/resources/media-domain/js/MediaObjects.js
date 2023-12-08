/**
 * Utilities related to 'mediaobjects' from POMS/NPO media api.
 *
 * Available as https://rs[-test|-acc|].poms.omroep.nl/v1/media-domain/js/MediaObjects.js
 */
const nl_vpro_domain_media_MediaObjects = (function() {

    let clock = function () {
        return Date.now();
    }

    function debug() {
        // console && console.log.apply(null, arguments);
    }

    function info() {
        console && console.log.apply(null, arguments);
    }

    /**
     * Some legacy location don't have there av-format properly filled in. This guesses it from the programUrlField
     *
     * This code is copied from java nl.vpro.domain.media.AVFileFormat#forProgramUrl
     * @param {object} location The location object
     * @return {string}
     */
    function avFormatForProgramUrl(location) {
        const url = location.programUrl;
        if (url == null) return "UNKNOWN";
        const urlLowerCase = url.toLowerCase();
        if (urlLowerCase.includes("adaptive")) {
            return "HASP";
        }
        if (urlLowerCase.includes("h264")) {
            return "H264";
        }
        if (urlLowerCase.includes("wmv") || urlLowerCase.includes("wvc1")) {
            return "WM";
        }
        if (urlLowerCase.startsWith("http://player.omroep.nl/")) {
            return "HTML";
        }
        if (urlLowerCase.endsWith(".asf") ||
            urlLowerCase.endsWith(".wmv") ||
            urlLowerCase.endsWith(".wma") ||
            urlLowerCase.endsWith(".asx")) {
            return "WM";
        }
        if (urlLowerCase.endsWith(".m4v") ||
            urlLowerCase.endsWith(".m4a") ||
            urlLowerCase.endsWith(".mov") ||
            urlLowerCase.endsWith(".mp4")
        ) {
            return "MP4";
        }
        if (urlLowerCase.endsWith(".ra") ||
            urlLowerCase.endsWith(".rm") ||
            urlLowerCase.endsWith(".ram") ||
            urlLowerCase.endsWith(".smil")) {
            return "RM";
        }
        if (urlLowerCase.endsWith(".mp3")) {
            return "MP3";
        }
        if (urlLowerCase.endsWith(".3gp") ||
            urlLowerCase.endsWith(".3gpp")) {
            return "DGPP";
        }
        if (urlLowerCase.endsWith(".flv") ||
            urlLowerCase.endsWith(".swf") ||
            urlLowerCase.endsWith(".f4v") ||
            urlLowerCase.endsWith(".f4p") ||
            urlLowerCase.endsWith(".f4a") ||
            urlLowerCase.endsWith(".f4b")) {
            return "FLV";
        }
        return "UNKNOWN";

    }

    const ACCEPTABLE_FORMATS = ["MP3", "MP4", "M4V", "H264"];

    const ACCEPTABLE_SCHEMES = ["npo+drm", "npo"];


    function locationFilter(l) {
        if (l.workflow === 'DELETED') {
            return false;
        }
        let scheme = new URL(l.programUrl).protocol.slice(0, -1);

        if (ACCEPTABLE_SCHEMES.includes(scheme)) {
            debug(l, scheme, "is acceptable");
            return true;
        }


        // legacy filter on av type
        let format = l.avAttributes ? l.avAttributes.avFileFormat : null
        if (format == null || format === "UNKNOWN") {
            format = avFormatForProgramUrl(l);
        }
        if (format != null && format !== "UNKNOWN") {
            const acceptable = ACCEPTABLE_FORMATS.includes(format);
            if (!acceptable) {
                debug(l, format, "is not acceptable");
                return false;
            }
        }
        //debug(l, format, "accepted");
        return true;
    }

    function platformMatches(source, actual) {
        return source === actual || (source === 'INTERNETVOD' && actual === undefined);
    }


    function undefinedIsNull(arg) {
        return (typeof arg !== 'undefined') ? arg : null;
    }

    function undefinedIsEmpty(arg) {
        return (typeof arg !== 'undefined') ? arg : [];
    }

    /**
     * @param {string} platform
     * @param {Object} mediaObject
     * @param {function} predictionPredicate Filter for the predictions
     * @param {function} locationPredicate Filter for the location
     * @return {array} 2 dates, or null
     */
    function playability(
        platform,
        mediaObject,
        predictionPredicate, locationPredicate) {

        const matchedByPrediction =
            mediaObject.predictions &&
            mediaObject.predictions.find(
                prediction => platform === prediction.platform &&
                    predictionPredicate(platform, prediction)
            );
        if (matchedByPrediction) {
            debug("Matched by prediction", mediaObject, "on prediction", matchedByPrediction.platform);
            return [undefinedIsNull(matchedByPrediction.publishStart), undefinedIsNull(matchedByPrediction.publishStop)];
        }
        // fall back to location only
        const matchedOnLocation = mediaObject.locations &&
            mediaObject.locations
                .filter(locationFilter)
                .find(location => platformMatches(platform, location.platform) && locationPredicate(platform, location));
        if (matchedOnLocation) {
            debug("Matched location ", mediaObject.locations, platform, "on location", matchedOnLocation.programUrl);
            return [undefinedIsNull(matchedOnLocation.publishStart), undefinedIsNull(matchedOnLocation.publishStop)];
        } else {
            return null;
        }
    }


    const platforms = Object.freeze({
        INTERNETVOD: "INTERNETVOD",
        TVVOD: "TVVOD",
        PLUSVOD: "PLUSVOD",
        NPOPLUSVOD: "NPOPLUSVOD"
    });

    const states = Object.freeze({
        ANNOUNCED: "ANNOUNCED",
        REALIZED: "REALIZED",
        REVOKED: "REVOKED"
    });


    function inPublicationWindow(object) {
        const now = clock()
        const stop = object.publishStop;
        if (stop != null && ! (now < stop)) {
            debug(object, "not published")
            return false;
        }
        const start = object.publishStart;
        if (start != null && start > now) {
            debug(object, "not published")
            return false;
        }
        debug(object, "published")
        return true;
    }

    function wasUnderEmbargo(object) {
        const stop = object.publishStop;
        const now = clock()
        return stop != null && ! (now < stop);
    }

    function willBePublished(object) {
        const now = clock()
        const start = object.publishStart;
        return ! inPublicationWindow(now) && (start != null && now < start);
    }

    return Object.freeze({
       Platform:  platforms,
       State:  states,


        /**
         * @param {string} platform
         * @param  {Object} mediaObject
         * @return {boolean}
         */
        nowPlayableForPlatform: function (platform, mediaObject) {
            return undefinedIsEmpty(mediaObject.locations)
                .filter(locationFilter)
                .filter(inPublicationWindow)
                .filter(l => platformMatches(platform, l.platform)).length > 0;
        },

        /**
         *
         * @param  {Object} mediaObject
         * @return {array}     list of platforms the given mediaobject is now playable on
         */
        nowPlayable: function (mediaObject) {
            return  Object.values(platforms)
                .map(platform =>
                    [platform, this.nowPlayableForPlatform(platform, mediaObject)])
                .filter(([platform, hasLocation]) => hasLocation)
                .map(([platform, hasLocation]) => platform);
        },

        /**
         * @param  {string} platform
         * @param  {Object} mediaObject
         * @return {boolean}
         */
        wasPlayableForPlatform: function (platform, mediaObject) {
            if (this.nowPlayableForPlatform(platform, mediaObject)) {
                return false;
            }
            prediction = undefinedIsEmpty(mediaObject.predictions)
                .filter(p => platformMatches(platform, p.platform))

            return prediction.length === 1 && prediction[0].state === 'REVOKED';
        },

        /**
         *
         * @param  {Object} mediaObject
         * @return {array}  list of platforms the given mediaobject is was playable on
         */
        wasPlayable: function ( mediaObject) {
             return  Object.values(platforms)
                .map(platform =>
                    [platform, this.wasPlayableForPlatform(platform, mediaObject)])
                .filter(
                    ([platform, was]) => was)
                .map(([platform, was]) => platform);
        },

        /**
         *
         * @param  {string} platform
         * @param  {Object} mediaObject
         * @return {boolean}
         */
        willBePlayableForPlatform: function (platform, mediaObject) {
            if (this.nowPlayableForPlatform(platform, mediaObject)) {
                return false;
            }
            const prediction = undefinedIsEmpty(mediaObject.predictions)
                .filter(p => platformMatches(platform, p.platform))

            return prediction.length !== 0 && prediction[0].state === 'ANNOUNCED';
        },

        /**
         *
         * @param  {Object} mediaObject
         * @return {array}  list of platforms the given mediaobject will be playable on
         */
        willBePlayable: function ( mediaObject) {
              return  Object.values(platforms)
                .map(platform =>
                    [platform, this.willBePlayableForPlatform(platform, mediaObject)])
                .filter(
                    ([platform, will]) => will)
                .map(([platform, will]) => platform);
        },


        /**
         * Returns for a certain platform the range it which a mediaobject is playable.
         */
        playableRange: function(platform, mediaObject) {
            return playability(platform, mediaObject,
                (platform, prediction) => platform !== this.Platform.INTERNETVOD, // for internetvod _only_ check locations
                (platform, location) => locationFilter(location)
            );
        },


        /**
         */
        playableRanges: function(mediaObject) {
            const result = {}
            Object.values(platforms).forEach(platform => {
                const range = this.playableRange(platform, mediaObject);
                if (range) {
                    result[platform] = range;
                }
            });
            return result;
        },


        /**
         * For testing purposes the clock can be set
         */
        setClock: function(newclock) {
            clock = newclock;
        }

    });
})();

try {
    exports.MediaObjects = nl_vpro_domain_media_MediaObjects;
} catch (e) { /* ignored for unsupported browsers */}
