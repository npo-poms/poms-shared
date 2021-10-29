// This is a jasmine tests for 'MediaObjects.js'.
// It is run by mvn -Pnpm test (and on github actions, with mvn -DskipTests=false)
// To run in intellij:
//   - install jasmine plugin
//   - install jasmine: sudo npm install -g jasmine
//   - run jasmine with jasmine package /usr/local/lib/node_modules/jasmine/

describe('MediaObjects', function() {
    // This is the object we are testing
    require("../../main/resources/META-INF/resources/media-domain/js/MediaObjects")
    var target = nl_vpro_domain_media_MediaObjects;

    // make sure the tests are entirely predictable. Some cases tests 'embargo'.
    target.setClock(function() {return 1635253200000});


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

    // Following are parameterized tests, which get their specifications from all files in directory that is determined below
    var fs = require('fs');
    var path = require('path');
    var directory = path.dirname(__filename) + '/cases/'

    describe("nowPlayable", function() {
        var files = fs.readdirSync(directory);
        files.forEach(file => {
            const json = require(directory + '/' + file);
            it(json.description, function () {
                expect(target.nowPlayable(json.mediaObject)).toEqual(json.nowExpectedPlatforms);
            });
        });
    });

    describe("wasPlayable", function() {
        var files = fs.readdirSync(directory);
        files.forEach(file => {
            const json = require(directory + '/' + file);
            it(json.description, function () {
                expect(target.wasPlayable(json.mediaObject)).toEqual(json.wasExpectedPlatforms);
            });
        });
    });

    describe("willBePlayable", function() {
        var files = fs.readdirSync(directory);
        files.forEach(file => {
            const json = require(directory + '/' + file);
            it(json.description, function () {
                expect(target.willBePlayable(json.mediaObject)).toEqual(json.willExpectedPlatforms);
            });
        });

    });
});
