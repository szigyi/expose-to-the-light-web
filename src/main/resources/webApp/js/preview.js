const Template = {
    renderTimelapsePreview: (timelapseSource) =>
        `<img id="timelapse-preview" alt="latest image" class="responsive" src="${timelapseSource}"/>`,
    renderImagePath: (path) =>
        `<p>${path}</p>`
};

const Page = {
    createTimelapse: () => {
        Api.getFileNamesOfAllImages(imagePathsReversed => {
            const imagePaths = imagePathsReversed.reverse().map(p => p.latestImageName);
            if (imagePaths.length > 0) {
                $("<img/>").attr('src', `/image${imagePaths[0]}`)
                    .on('load', function () {
                        // First image is loaded, we know its size from now
                        $('#timelapse-paths-section').html(imagePaths.map(p => Template.renderImagePath(p)));
                        const gif = Page.createGif(this.height, this.width);
                        imagePaths.forEach(imgPath => {
                            const img = document.createElement("img");
                            img.src = `/image${imgPath}`;
                            console.log('Creating image for', img.src);
                            gif.addFrame(img);
                        });
                        gif.render();
                    });
            }
        });
    },
    createGif: (h, w) => {
        const newGif = new GIF({
            workers: 1, // increasing the number makes the equivalent amount of first images go black, maybe the worker never merges the result into the main?
            height: h,
            width: w,
            quality: 1,
            workerScript: 'js/gif.worker.js'
        });
        newGif.on('finished', function (blob) {
            console.log('GIF render is finished');
            $('#timelapse-section').html(Template.renderTimelapsePreview(URL.createObjectURL(blob)));
        });
        return newGif;
    }
};

$(function () {
    Shared.copyQueryParamsToMenu();
    Page.createTimelapse();
});