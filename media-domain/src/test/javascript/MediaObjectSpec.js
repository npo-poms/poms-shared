MediaObject = require("../../main/resources/META-INF/resources/media-domain/js/MediaObject")
describe('MediaObject', function() {

    describe('construct', function() {
        const mediaobject = new MediaObject(require("./cases/program-with-everything.json"));
        it("maintitle", function() {
            expect(mediaobject.getMainTitle()).toEqual("Main title");
        });
    })

});
