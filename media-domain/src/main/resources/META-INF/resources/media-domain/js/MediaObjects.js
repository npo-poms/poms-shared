

nl_vpro_domain_media_MediaObjects = (function() {


    function playabilityCheck(platform, mediaObject,  predictionPredicate, locationPredicate) {
        return true;
    }

    /**
     *
     * @param {Object} mediaObject
     * @param {function} predictionPredicate Filter for the predictions
     * @return {array}   list of platforms
     */
    function playability(mediaObject, predictionPredicate, locationPredicate) {

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
         * @param  {Object} mediaObject
         * @return {array}     list of platforms the given mediaobject is now playable on
         */
        nowPlayable: function (mediaObject) {
            return playability(mediaObject,
                p => true,
                l => true
            );
        },

        /**
         *
         * @param  {Object} mediaObject
         * @return {array}  list of platforms the given mediaobject is was playable on
         */
        wasPlayable: function (mediaObject) {
            return playability(mediaobject,
                p => true,
                l => true
            );

        },

       willBePlayable: function (mediaObject) {
            return playability(mediaobject,
                p => true,
                l => true
            );
        }
    });
}).apply();

