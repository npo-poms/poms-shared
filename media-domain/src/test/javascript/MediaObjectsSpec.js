// This is a jasmine tests for 'MediaObjects.js'.
// It is run by mvn -Pnpm test (and on github actions, with mvn -DskipTests=false)
// To run in intellij:
//   - install jasmine plugin
//   - install jasmine: sudo npm install -g jasmine
//   - run jasmine with jasmine package /usr/local/lib/node_modules/jasmine/

describe('MediaObjects', function() {
    // This is the object we are testing
    const target = require("../../main/resources/META-INF/resources/media-domain/js/MediaObjects").MediaObjects;

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
    const path = require('path');
    const directory = path.dirname(__filename) + '/cases/playability/'
    const fs = require('fs');
    const files = fs.readdirSync(directory);

    describe("nowPlayable", function() {
        files.forEach(file => {
            const json = require(directory + '/' + file);
            it(json.description, function () {
                expect(target.nowPlayable(json.publishedMediaObject)).toEqual(json.publishedNowExpectedPlatforms);
                expect(target.nowPlayable(json.mediaObject)).toEqual(json.nowExpectedPlatforms);
            });
        });
    });

    describe("wasPlayable", function() {
        files.forEach(file => {
            const json = require(directory + '/' + file);
            it(json.description, function () {
                expect(target.wasPlayable(json.publishedMediaObject)).toEqual(json.publishedWasExpectedPlatforms);
                expect(target.wasPlayable(json.mediaObject)).toEqual(json.wasExpectedPlatforms);
            });
        });
    });

    describe("willBePlayable", function() {
        files.forEach(file => {
            const json = require(directory + '/' + file);
            it(json.description, function () {
                expect(target.willBePlayable(json.publishedMediaObject)).toEqual(json.publishedWillExpectedPlatforms);
                expect(target.willBePlayable(json.mediaObject)).toEqual(json.willExpectedPlatforms);
            });
        });

    });
});
