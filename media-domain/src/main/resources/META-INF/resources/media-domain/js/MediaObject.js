class MediaObject {
    constructor(object) {
        Object.assign(this, object)
    }
    getMainTitle() {
        return this.titles.find(e => e.type === 'MAIN').value;
    }
}
module.exports = MediaObject;

