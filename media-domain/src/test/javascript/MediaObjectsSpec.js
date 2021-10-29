// This is a jasmine tests for 'utils.js'.
// It is run by jasmine maven plugin.
// To run in intellij:
//   - install jasmine plugin
//   - install jasmine: sudo npm install -g jasmine
//   - run jasmine with jasmine package /usr/local/lib/node_modules/jasmine/
// I didn't figure out yet how to run in intellij

describe('MediaObjects', function() {
    console.log("hallo")
    require("../../main/resources/META-INF/resources/media-domain/js/MediaObjects")
    var target = nl_vpro_domain_media_MediaObjects;

    var fs = require('fs');
    var path = require('path');
    var directory = path.dirname(__filename) + '/cases/'

    console.log("hoi", target);

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
