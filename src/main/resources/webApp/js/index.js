const Template = {
    renderLog: (noAnimation) => (log) => {
        let logLevel;
        switch (log.logLevel) {
            case 'D':
                logLevel = 'bg-secondary text-light'; break;
            case 'E':
                logLevel = 'bg-danger text-light'; break;
            case 'W':
                logLevel = 'bg-warning text-dark'; break;
            case 'T':
                logLevel = 'bg-dark text-light'; break;
            default:
                logLevel = 'bg-light text-dark';
        }
        let highlight = noAnimation ? '' : 'highlight';
        return `
                <div class="${highlight}">
                    <span class="log-timestamp badge rounded-pill ${logLevel}" title="Log Level: ${log.logLevel}">${log.timestamp}</span>
                    <span>${log.message}</span>
                </div>`;
    },
    renderLatestImage: (name) =>
        `<a href="/image${name}" target="_blank"><img class="responsive" alt="latest image" id="latest-image" data-name="${name}" src="/image${name}"></a>`,
    renderMetric: (metric) => {
        let resLevel = 'alert alert-light';
        if (Math.abs(metric.difference) > 1000) resLevel = 'alert alert-danger';
        else if (Math.abs(metric.difference) > 200) resLevel = 'alert alert-warning';
        return `<span class="metric ${resLevel}" role="alert" title="Expected: ${metric.expected} - ${metric.actual} :Actual"><span class="order-number">[${metric.orderNumber}]</span>${metric.difference}ms</span>`;
    }
};

let latestLogTimestamp = new Date(Date.now());
let latestMetricTimestamp = new Date(Date.now());
let logPollingRate = 900;
let imagePollingRate = 1100;
let metricPollingRate = 1200;

const Page = {
    pollLogs: () =>
        setInterval(Page.loadLogsSince, logPollingRate),
    pollImages: () =>
        setInterval(Page.loadLatestImage, imagePollingRate),
    pollMetrics: () =>
        setInterval(Page.loadLatestMetricsSince, metricPollingRate),
    localTimeToDate: (time) => {
        let datePart = new Date(Date.now()).toISOString().split('T')[0];
        return new Date(datePart + 'T' + time + 'Z'); // FIXME potential bug if user is not in UTC
    },
    handleLogs: (noAnimation) => (logs) => {
        if (logs.length > 0) {
            $('#logs-section').prepend(logs.map(log => Template.renderLog(noAnimation)(log)));
            latestLogTimestamp = Page.localTimeToDate(logs[0].timestamp);
        }
    },
    handleMetrics: (metrics) => {
        if (metrics.length > 0) {
            $('#metrics-section').prepend(metrics.map(Template.renderMetric));
            latestMetricTimestamp = Page.localTimeToDate(metrics[0].actual);
        }
    },
    loadLatestLogFile: () => {
        Api.getLatestLogFile(Page.handleLogs(true))
    },
    loadLogsSince: () => {
        Api.getLogsSince(latestLogTimestamp.toISOString(), Page.handleLogs(false));
    },
    loadLatestImage: () => {
        let latestImage = $('#latest-image').data('name');
        Api.getFileNameOfLatestImage(response => {
            if (response.latestImageName && response.latestImageName !== latestImage) {
                $('#latest-image-section').html(Template.renderLatestImage(response.latestImageName));
            }
        });
    },
    loadLatestMetrics: () => {
        Api.getLatestMetrics(Page.handleMetrics);
    },
    loadLatestMetricsSince: () => {
        Api.getLatestMetricsSince(latestMetricTimestamp.toISOString(), Page.handleMetrics);
    },
    setConfigs: () => {
        Api.setConfig($('#raw-directory-path-input').val(), $('#log-directory-path-input').val(), $('#raw-file-extension-input').val(), $('#log-level').text(), resp => {
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
        Api.runEttl(dummyCamera, setSettings, numberOfCaptures, intervalSeconds, () => {
            Page.showEttlStopper();
        });
    },
    hideAppSettings: () => {
        $('#app-settings').removeClass('show');
    },
    showEttlStopper: () => {
        $('#timelapse-stop').addClass('show');
    },
    hideEttlStopper: () => {
        $('#timelapse-stop').removeClass('show');
    },
    fetchUrlParams: () => {
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const useParamIfExists = (paramName, set) => {
            if (urlParams.has(paramName)) set(urlParams.get(paramName));
        }
        useParamIfExists('raw', (raw) => $('#raw-directory-path-input').val(raw));
        useParamIfExists('log', (log) => $('#log-directory-path-input').val(log));
        useParamIfExists('ext', (ext) => $('#raw-file-extension-input').val(ext));
        useParamIfExists('level', (level) => $('#log-level').html(level));

        if (urlParams.has('raw') && urlParams.has('log') && urlParams.has('ext')) Page.hideAppSettings();
    },
    setUrlParams: () => {
        const urlParams = new URLSearchParams();
        urlParams.set('raw', $('#raw-directory-path-input').val());
        urlParams.set('log', $('#log-directory-path-input').val());
        urlParams.set('ext', $('#raw-file-extension-input').val());
        urlParams.set('level', $('#log-level').text());
        window.history.replaceState('', '', `index.html?${urlParams.toString()}`);
    }
};

$(function () {
    Shared.copyQueryParamsToMenu();
    Page.fetchUrlParams();
    Page.setConfigs();
    Page.loadLatestLogFile();
    Page.loadLatestMetrics();
    Page.pollLogs();
    Page.pollImages();
    Page.pollMetrics();
    $('#run-ettl').on('click', Page.runEttl);
    $('#stop-ettl').on('click', Page.hideEttlStopper);
});