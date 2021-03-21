const Template = {
    renderTimelapsePreview: (timelapseSource) =>
        `<img id="timelapse-preview" class="responsive" src="${timelapseSource}"/>`
};

const Page = {
    createTimelapse: () => {
        Api.getFileNamesOfAllImages(imagePaths => {
            if (imagePaths.length > 0) {
                $("<img/>").attr('src', `/image${imagePaths[0].latestImageName}`)
                    .on('load', function () {
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