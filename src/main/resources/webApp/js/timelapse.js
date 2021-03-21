const Template = {
    renderTimelapsePreview: (timelapseSource) =>
        `<img id="timelapse-preview" class="responsive" src="${timelapseSource}"/>`,
    renderImagePath: (path) =>
        `<p>${path}</p>`
};

const Page = {
    createTimelapse: () => {
        Api.getFileNamesOfAllImages(imagePaths => {
            console.log('Got back image paths:', imagePaths.length);
            if (imagePaths.length > 0) {
                $("<img/>").attr('src', `/image${imagePaths[0].latestImageName}`)
                    .on('load', function () {
                        console.log('First image is loaded, we know its size from now');
                        $('#timelapse-paths-section').html(imagePaths.map(p => Template.renderImagePath(p.latestImageName)));
                        const gif = Page.createGif(this.height, this.width);
                        imagePaths.forEach(imgPath => {
                            const img = document.createElement("img");
                            img.src = `/image${imgPath.latestImageName}`;
                            gif.addFrame(img);
                        });
                        gif.render();
                    });
            }
        });
    },
    createGif: (h, w) => {
        const newGif = new GIF({
            workers: 2,
            height: h,
            width: w,
            quality: 1,
            workerScript: 'js/gif.worker.js'
        });
        newGif.on('finished', function (blob) {
            console.log('GIF render is finished');
            let src = URL.createObjectURL(blob);
            let img = Template.renderTimelapsePreview(src);
            console.log(src);
            console.log(img);
            $('#timelapse-section').html(img);
        });
        return newGif;
    }
};

$(function () {
    Shared.copyQueryParamsToMenu();
    Page.createTimelapse();
});