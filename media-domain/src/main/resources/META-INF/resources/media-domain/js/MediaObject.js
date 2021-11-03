class MediaObject {
    constructor(object) {
        Object.assign(this, object)
    }
    getMainTitle() {
        return this.titles.find(e => e.type === 'MAIN').value;
    }
}
try {
    module.exports = MediaObject;
} catch (e) {}


