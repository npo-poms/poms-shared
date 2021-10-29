// This is a jasmine tests for 'utils.js'.
// It is run by jasmine maven plugin.
// To run in intellij:
//   - install jasmine plugin
//   - install jasmine: sudo npm install -g jasmine
//   - run jasmine with jasmine package /usr/local/lib/node_modules/jasmine/
// I didn't figure out yet how to run in intellij

describe('MediaObjects', function() {

    require("../../main/resources/META-INF/resources/media-domain/js/MediaObjects")
    var fs = require('fs');
    var directory = '/Users/michiel/github/npo-poms/poms-shared/media-domain/src/test/javascript/cases/'

    var target = nl_vpro_domain_media_MediaObjects;

    describe("platform", function() {
        it('must contain strings', function() {
            expect(target.Platform.INTERNETVOD).toBe("INTERNETVOD");
        });
        it('must be unmodifiable', function() {
            target.Platform.FOO = "BAR"
            expect(target.Platform.INTERNETVOD).toBeDefined();
            expect(target.Platform.FOO).toBeUndefined();
        });
    });

    describe("nowPlayable", function() {
        var files = fs.readdirSync(directory + 'now');
        files.forEach(nowCase => {
            const json = require(directory + 'now/' + nowCase);
            it('all', function () {
                expect(target.nowPlayable(json.mediaObject)).toBe(json.expectedPlatforms);
            });
        });

    });
});
