const Template = {
    renderTimelapsePreview: (timelapseSource) =>
        `<a target="_blank" href="${timelapseSource}"><img id="timelapse-preview" alt="timelapse preview" class="responsive" src="${timelapseSource}"/></a>`,
    renderImagePath: (path) =>
        `<p><a target="_blank" href="/image${path}">${path}</a></p>`,
    renderDirectoryPathInDropdown: (path) =>
        `<option value="${path}">${path.length > 40 ? `...${path.substring(path.length - 37, path.length)}` : path}</option>`
};

const Page = {
    setProgressbar: (progress) => {
        $('#timelapse-progressbar > div').html(`${Math.round(progress * 100)}%`);
        $('#timelapse-progressbar > div').attr('style', `width: ${progress * 100}%;`);
        $('#timelapse-progressbar > div').attr('aria-valuenow', progress * 100);
    },
    createTimelapse: () => {
        const loadImage = (imagePath, success) =>
            $("<img/>").attr('src', `/image${imagePath}`).on('load', success);

        const waitForImagesLoaded = (imageURLs, callback) => {
            let imageElements = [];
            let remaining = imageURLs.length;
            const onEachImageLoad = function() {
                if (--remaining === 0 && callback) {
                    callback(imageElements);
                }
            };

            imageURLs.forEach(imageUrl => {
                const img = new Image();
                img.onload = onEachImageLoad;
                img.src = imageUrl;
                imageElements.push(img);
            });
        };

        const createImgArr = (imagePaths) => {
            let images = [];
            imagePaths.forEach(imagePath => {
                images.push(`/image${imagePath}`);
            });
            return images;
        };

        Page.setProgressbar(0);
        Page.showLoadingScreen();
        const frameDelay = $('#frame-delay').val();
        const quickMode = $('#quick-mode-input').is(':checked');
        const baseDir = $('#images-directory option:selected').val();

        Api.getFileNamesOfAllImages(baseDir, quickMode, imagePathsObj => {
            const imagePaths = imagePathsObj.map(p => p.latestImageName);
            if (imagePaths.length > 0) {
                $('#timelapse-paths-section').html(imagePaths.map(p => Template.renderImagePath(p)));
                loadImage(imagePaths[0], function() {
                    const gif = Page.createGif(this.height, this.width);
                    waitForImagesLoaded(createImgArr(imagePaths), function(images) {
                        images.forEach(image => {
                            gif.addFrame(image, {delay: frameDelay});
                        });
                        gif.render();
                    });
                });
            }
        });
    },
    createGif: (h, w, quickMode) => {
        const newGif = new GIF({
            workers: 4,
            height: h,
            width: w,
            quality: quickMode ? 99 : 1,
            workerScript: 'js/gif.worker.js',
            dither: 'FloydSteinberg-serpentine',
            debug: true
        });
        newGif.on('finished', function (blob) {
            Page.hideLoadingScreen();
            $('#timelapse-section').html(Template.renderTimelapsePreview(URL.createObjectURL(blob)));
        });
        newGif.on('progress', function(p) {
           Page.setProgressbar(p)
        });
        return newGif;
    },
    loadImageDirectories: (success) => {
        Api.getImageDirectories(paths => {
            if (paths.length > 0) {
                $('#images-directory').html(paths.map(Template.renderDirectoryPathInDropdown));
                Page.createTimelapse();
                success();
            }
        });
    },
    hideLoadingScreen: () => {
        $('#timelapse-progressbar').addClass('visually-hidden');
    },
    showLoadingScreen: () => {
        $('#timelapse-progressbar').removeClass('visually-hidden');
    }
};

$(function () {
    Shared.copyQueryParamsToMenu();
    Page.loadImageDirectories(_ => {
        $('#images-directory').on('change', Page.createTimelapse);
        $('#quick-mode-input').on('change', Page.createTimelapse);
        $('#frame-delay').on('change', Page.createTimelapse);
    });
});