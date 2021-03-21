const Template = {
    renderLog: (noAnimation) => (log) => {
        let logLevel;
        switch (log.logLevel) {
            case 'D':
                logLevel = 'bg-secondary text-light'; break;
            case 'E':
                logLevel = 'bg-danger text-light'; break;
            case 'W':
                logLevel = 'bg-warning text-light'; break;
            default:
                logLevel = 'bg-light text-dark';
        };
        let highlight = noAnimation ? '' : 'highlight'
        return `
                <div class="${highlight}">
                    <span class="log-timestamp badge rounded-pill ${logLevel}" title="Log Level: ${log.logLevel}">${log.timestamp}</span>
                    <span>${log.message}</span>
                </div>`
    },
    renderLatestImage: (name) =>
        `<a href="/image${name}" target="_blank"><img class="responsive" id="latest-image" data-name="${name}" src="/image${name}"></a>`,
    renderRawDirectoryPathInput: (rawDirectoryPath) =>
        `<label for="raw-directory-path-input" class="visually-hidden">Location of Captured Images</label>
                <div class="input-group">
                    <div class="input-group-text">Captured Images</div>
                    <input type="text" class="form-control" id="raw-directory-path-input" value="${rawDirectoryPath}" placeholder="/home/pi/dev/ettl/captured-images">
                </div>`,
    renderLogDirectoryPathInput: (logDirectoryPath) =>
        `<label for="log-directory-path-input" class="visually-hidden">Location of Logs</label>
                <div class="input-group">
                    <div class="input-group-text">Logs</div>
                    <input type="text" class="form-control" id="log-directory-path-input" value="${logDirectoryPath}" placeholder="/home/pi/dev/ettl/logs">
                </div>`,
    renderRawFileExtensionInput: (rawFileExtension) =>
        `<label for="raw-file-extension-input" class="visually-hidden">RAW file's extension</label>
                <div class="input-group">
                    <div class="input-group-text">RAW type</div>
                    <input type="text" class="form-control" id="raw-file-extension-input" value="${rawFileExtension}" placeholder="CR2">
                </div>`,
    renderTimelapsePreview: (timelapseSource) =>
        `<img id="timelapse-preview" class="responsive" src="${timelapseSource}"/>`
};

let latestTimestamp = new Date(Date.now());
let timelapseImageSources = [];
let gif;

const Page = {
    pollLogs: () =>
        setInterval(Page.loadLogsSince, 500),
    pollImages: () =>
        setInterval(Page.loadLatestImage, 1000),
    handleLogs: (noAnimation) => (logs) => {
        if (logs.length > 0) {
            $('#logs').prepend(logs.map(log => Template.renderLog(noAnimation)(log)));
            let datePart = new Date(Date.now()).toISOString().split('T')[0];
            latestTimestamp = new Date(datePart + 'T' + logs[0].timestamp + 'Z'); // FIXME potential bug if user is not in UTC
        }
    },
    loadLatestLogFile: () => {
        Api.getLatestLogFile(Page.handleLogs(true))
    },
    loadLogsSince: () => {
        Api.getLogsSince(latestTimestamp.toISOString(), Page.handleLogs(false));
    },
    loadLatestImage: () => {
        let latestImage = $('#latest-image').data('name');
        Api.getFileNameOfLatestImage(response => {
            if (response.latestImageName && response.latestImageName !== latestImage) {
                Page.waitUntilExists(document.getElementById('latest-image-section'), () => {
                    Page.addImageAndRenderGif(document.getElementById("latest-image"));
                });
                $('#latest-image-section').html(Template.renderLatestImage(response.latestImageName));
            }
        });
    },
    setConfigs: () => {
        Api.setConfig($('#raw-directory-path-input').val(), $('#log-directory-path-input').val(), $('#raw-file-extension-input').val(), resp => {
            console.log("Set Config:", JSON.stringify(resp));
        });
    },
    runEttl: () => {
        let realCamera = $('#real-camera-input').is(':checked');
        let dummyCamera = !realCamera;
        let setSettings = $('#set-settings-input').is(':checked');
        let numberOfCaptures = $('#number-of-captures').val();
        let intervalSeconds = $('#interval-seconds').val();

        Page.setConfigs();
        Page.setUrlParams();
        Api.runEttl(dummyCamera, setSettings, numberOfCaptures, intervalSeconds, resp => {
            console.log("Running ettl:", JSON.stringify(resp));
        });
    },
    fetchUrlParams: () => {
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const useParamOrEmpty = (paramName, set) => {
            const value = urlParams.has(paramName) ? urlParams.get(paramName) : "";
            set(value);
        }
        useParamOrEmpty('raw', (raw) => $('#raw-directory-path-section').html(Template.renderRawDirectoryPathInput(raw)));
        useParamOrEmpty('log', (log) => $('#log-directory-path-section').html(Template.renderLogDirectoryPathInput(log)));
        useParamOrEmpty('ext', (ext) => $('#raw-file-extension-section').html(Template.renderRawFileExtensionInput(ext)));
    },
    setUrlParams: () => {
        const urlParams = new URLSearchParams();
        urlParams.set('raw', $('#raw-directory-path-input').val());
        urlParams.set('log', $('#log-directory-path-input').val());
        urlParams.set('ext', $('#raw-file-extension-input').val());
        window.history.replaceState('', '', `index.html?${urlParams.toString()}`);
    },
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
    },
    waitUntilExists: (targetNode, whenExists) => {
        const config = { childList: true };
        const callback = function(mutationsList, observer) {
            // Use traditional 'for loops' for IE 11
            for(const mutation of mutationsList) {
                if (mutation.type === 'childList') {
                    console.log('A child node has been added or removed.');
                    whenExists();
                    observer.disconnect();
                }
            }
        };
        const observer = new MutationObserver(callback);
        observer.observe(targetNode, config);
    }
};

$(function () {
    Page.fetchUrlParams();
    Page.setConfigs();
    Page.loadLatestLogFile();
    Page.pollLogs();
    Page.pollImages();
    $('#run-ettl').on('click', Page.runEttl);
});