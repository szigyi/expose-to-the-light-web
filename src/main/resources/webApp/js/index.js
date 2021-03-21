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
        Shared.copyQueryParamsToMenu();
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
    }
};

$(function () {
    Shared.copyQueryParamsToMenu();
    Page.fetchUrlParams();
    Page.setConfigs();
    Page.loadLatestLogFile();
    Page.pollLogs();
    Page.pollImages();
    $('#run-ettl').on('click', Page.runEttl);
});