const Template = {

};

const Page = {
    createGif: (h, w) => {
        const newGif =new GIF({
            workers: 2,
            height: h,
            width: w,
            quality: 1
        });
        newGif.on('finished', function(blob) {
            console.log('GIF render is finished');
            let src = URL.createObjectURL(blob);
            let img = Template.renderTimelapsePreview(src);
            console.log(src);
            console.log(img);
            $('#timelapse-section').html(img);
        });
        return newGif;
    },
    addImageAndRenderGif: (imgElement) => {
        console.log('img element', imgElement);
        console.log('img src', imgElement.src);

        timelapseImageSources.push(imgElement.src);

        console.log(`Creating GIF h:${imgElement.naturalHeight}, w:${imgElement.naturalWidth} and Adding new image to the GIF`);
        gif = Page.createGif(imgElement.naturalHeight, imgElement.naturalWidth);

        timelapseImageSources.forEach(src => {
            let img = document.createElement("img");
            img.src = src;
            gif.addFrame(img);
        });
        gif.render();
    }
};

$(function () {
    Shared.copyQueryParamsToMenu();
});