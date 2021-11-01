const  nl_vpro_domain_media_MediaObjects = (function() {

    let clock = function() {
        return Date.now();
    }

    function debug() {
        console.log.apply(null, arguments);
    }
    function info() {
        console.log.apply(null, arguments);
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
        if(urlLowerCase.includes("adaptive")) {
            return "HASP";
        }
        if(urlLowerCase.includes("h264")) {
            return "H264";
        }
        if(urlLowerCase.includes("wmv") || urlLowerCase.includes("wvc1")) {
            return "WM";
        }
        if(urlLowerCase.startsWith("http://player.omroep.nl/")) {
            return "HTML";
        }
        if(urlLowerCase.endsWith(".asf") ||
            urlLowerCase.endsWith(".wmv") ||
            urlLowerCase.endsWith(".wma") ||
            urlLowerCase.endsWith(".asx")) {
            return "WM";
        }
        if(urlLowerCase.endsWith(".m4v") ||
            urlLowerCase.endsWith(".m4a") ||
            urlLowerCase.endsWith(".mov") ||
            urlLowerCase.endsWith(".mp4")
            ) {
            return "MP4";
        }
        if(urlLowerCase.endsWith(".ra") ||
            urlLowerCase.endsWith(".rm") ||
            urlLowerCase.endsWith(".ram") ||
            urlLowerCase.endsWith(".smil")) {
            return "RM";
        }
        if(urlLowerCase.endsWith(".mp3")) {
            return "MP3";
        }
        if(urlLowerCase.endsWith(".3gp") ||
            urlLowerCase.endsWith(".3gpp")) {
            return "DGPP";
        }
        if(urlLowerCase.endsWith(".flv") ||
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

    function locationFilter(l) {
        if (l.workflow === 'DELETED') {
            return false;
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
        debug(l, format, "accepted");
        return true;
    }

    function platformMatches(source, actual) {
        return source === actual || (source === 'INTERNETVOD' && actual === undefined);
    }

    /**
     *
     * @param {string} platform
     * @param {Object} mediaObject
     * @param {function} predictionPredicate Filter for the predictions
     * @param {function} locationPredicate Filter for the location
     * @return {boolean}
     */
    function playabilityCheck(platform, mediaObject,  predictionPredicate, locationPredicate) {
        const matchedByPrediction = mediaObject.predictions && mediaObject.predictions.some(p => platform === p.platform && predictionPredicate(p));
        if (matchedByPrediction) {
            debug("Matched", mediaObject, platform, "on prediction");
            return true;
        }
        // fall back to location only
        const matchedOnLocation = mediaObject.locations &&
            mediaObject.locations
                .filter(locationFilter)
                .some(l => platformMatches(platform, l.platform) && locationPredicate(l));
        if (matchedOnLocation) {
            debug("Matched", mediaObject.locations, platform, "on location", matchedOnLocation);
        }
        return matchedOnLocation;
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

    /**
     *
     * @param {Object} mediaObject
     * @param {function} predictionPredicate Filter for the predictions
     * @param {function} locationPredicate Filter for the location
     * @return {array}   list of platforms
     */
    function playability(mediaObject, predictionPredicate, locationPredicate) {
        return Object.values(platforms).filter(p => playabilityCheck(p, mediaObject, predictionPredicate, locationPredicate));
    }

    function inPublicationWindow(object) {
        const now = clock()
        const stop = object.publishStop;
        if (stop != null && ! now < stop) {
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
        return stop != null && ! now < stop;
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
         *
         * @param  {Object} mediaObject
         * @return {array}     list of platforms the given mediaobject is now playable on
         */
        nowPlayable: function (mediaObject) {
            return playability(mediaObject,
                s => s.state === this.State.REALIZED && inPublicationWindow(s),
                inPublicationWindow
            );
        },

        /**
         *
         * @param  {Object} mediaObject
         * @return {array}  list of platforms the given mediaobject is was playable on
         */
        wasPlayable: function (mediaObject) {
            return playability(mediaObject,
                s => s.state === this.State.REVOKED,
                wasUnderEmbargo
            );
        },

        /**
         *
         * @param  {Object} mediaObject
         * @return {array}  list of platforms the given mediaobject will be playable on
         */
        willBePlayable: function (mediaObject) {
            return playability(mediaObject,
                s => s.state === this.State.ANNOUNCED,
                willBePublished
            );
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
} catch (e) {}
