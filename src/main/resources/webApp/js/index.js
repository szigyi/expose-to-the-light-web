const Template = {
    renderLog: (noAnimation) => (log) => {
        let logLevel;
        switch (log.logLevel) {
            case 'D':
                logLevel = 'bg-secondary';
                break;
            case 'E':
                logLevel = 'bg-danger';
                break;
            case 'W':
                logLevel = 'bg-warning';
                break;
            case 'T':
                logLevel = 'bg-dark';
                break;
            default:
                logLevel = 'bg-light';
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

let latestLogTimestamp = new Date('1995-12-17T00:00:00');
let latestMetricTimestamp = new Date('1995-12-17T00:00:00');
let logPollingRate = 900;
let imagePollingRate = 1100;
let metricPollingRate = 1200;
let isEttlRunningPollingRate = 1300;
let logsInterval;
let imagesInterval;
let metricsInterval;

const Page = {
    pollLogs: () =>
        logsInterval = setInterval(Page.loadLogsSince, logPollingRate),
    pollImages: () =>
        imagesInterval = setInterval(Page.loadLatestImage, imagePollingRate),
    pollMetrics: () =>
        metricsInterval = setInterval(Page.loadLatestMetricsSince, metricPollingRate),
    pollIsEttlRunning: () =>
        setInterval(Page.isEttlRunning, isEttlRunningPollingRate),
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
        Api.getLatestLogFile((logs) => Page.handleLogs(true)(logs));
    },
    loadLogsSince: () => {
        Api.getLogsSince(latestLogTimestamp.toISOString(), Page.handleLogs(false));
    },
    loadLatestImage: () => {
        let latestImage = $('#latest-image').data('name');
        Api.getFileNameOfLatestImage(response => {
            if (response.latestImageName && response.latestImageName !== latestImage) {
                $('#latest-image-section').html(Template.renderLatestImage(response.latestImageName));
                $('#latest-image').on('load', _ => {
                    const h = document.defaultView.getComputedStyle(document.getElementById('latest-image')).height;
                    $('#latest-image-section').attr('style', `height: ${h}px`);
                });
            }
        });
    },
    loadLatestMetrics: () => {
        Api.getLatestMetrics((metrics) => Page.handleMetrics(metrics));
    },
    loadLatestMetricsSince: () => {
        Api.getLatestMetricsSince(latestMetricTimestamp.toISOString(), Page.handleMetrics);
    },
    setConfigs: (success) => {
        Api.setConfig($('#raw-directory-path-input').val(), $('#log-directory-path-input').val(), $('#raw-file-extension-input').val(), $('#log-level').text(), success);
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
    stopEttl: () => {
        Api.stopEttl(Page.hideEttlStopper);
    },
    isEttlRunning: () => {
        Api.isEttlRunning(resp => {
            if (resp.isRunning) {
                Page.showEttlStopper()
                if (!logsInterval) Page.pollLogs();
                if (!imagesInterval) Page.pollImages();
                if (!metricsInterval) Page.pollMetrics();
            } else {
                Page.hideEttlStopper();
                if (logsInterval) {
                    clearInterval(logsInterval);
                    logsInterval = undefined;
                }
                if (imagesInterval) {
                    clearInterval(imagesInterval);
                    imagesInterval = undefined;
                }
                if (metricsInterval) {
                    clearInterval(metricsInterval);
                    metricsInterval = undefined;
                }
            }
        });
    },
    hideAppSettings: () => {
        $('#app-settings').removeClass('show');
    },
    showEttlStopper: () => {
        $('#timelapse-settings').removeClass('show');
        $('#timelapse-stop').addClass('show');
    },
    hideEttlStopper: () => {
        $('#timelapse-stop').removeClass('show');
        $('#timelapse-settings').addClass('show');
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
    },
    calculateTimelapseLength: () => {
        const timeLength = (seconds) => {
            const hours = Math.floor(seconds / 3600);
            seconds -= hours * 3600;
            const minutes = Math.floor(seconds / 60);
            seconds -= minutes * 60;
            return `${hours}:${('0' + minutes).slice(-2)}:${('0' + seconds).slice(-2)}`;
        };
        const numberOfCaptures = $('#number-of-captures').val();
        const intervalSeconds = $('#interval-seconds').val();
        const fps = 30;
        const playbackLength = numberOfCaptures / fps;
        const realtimeLength = numberOfCaptures * intervalSeconds;
        $('#playback-length').html(`${timeLength(playbackLength)}`);
        $('#realtime-length').html(`${timeLength(realtimeLength)}`);
    }
};

$(function () {
    Page.pollIsEttlRunning();
    Shared.copyQueryParamsToMenu();
    Page.fetchUrlParams();
    Page.calculateTimelapseLength();
    Page.setConfigs(_ => {
        Page.loadLatestImage();
        Page.loadLatestLogFile();
        Page.loadLatestMetrics();
    });
    $('#run-ettl').on('click', Page.runEttl);
    $('#stop-ettl').on('click', Page.stopEttl);
    $('#number-of-captures').on('change', Page.calculateTimelapseLength);
    $('#interval-seconds').on('change', Page.calculateTimelapseLength);
});