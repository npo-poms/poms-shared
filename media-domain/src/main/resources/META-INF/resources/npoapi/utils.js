

nl_vpro_domain_media_MediaObjects = (function() {


    function playabilityCheck(platform, mediaObject,  predictionPredicate, locationPredicate) {
        return true;
    }

    function playability(mediaObject, predicationPredicate, locationPredicate) {

    }
    platforms = {
            INTERNETVOD: "INTERNETVOD",
            TVVOD: "TVVOD",
            PLUSVOD: "PLUSVOD",
            NPOPLUSVOD: "PLUSVOD"
    };
    Object.freeze(platforms);

   return  Object.freeze({
        Platform:  platforms,

        /**
         *
         * @param  {[type]} mediaObject
         * @return {[type]}      list of platforms the given mediaobject is not playable on
         */
        nowPlayable: function (mediaObject) {
            return playability(mediaObject,
                // look out with arrow expression, can't be tested with phantomjs
                function(p) { return true},
                function(l) { return true;}
            );
        },

        /**
         *
         * @param  {[type]} mediaObject
         * @return {[type]}      list of platforms the given mediaobject is not playable on
         */
        wasPlayable: function (mediaObject) {
            return playability(mediaobject,
                function(p) { return true},
                function(l) { return true;}
            );

        },

       willBePlayable: function (mediaObject) {
            return playability(mediaobject,
                function(p) { return true;},
                function(l) { return true;}
            );
        }
    });
}).apply();

