
const Api = {
    setConfig: (rawDirectoryPath, logDirectoryPath, rawFileExtension, success) =>
        $.post('/config', JSON.stringify({rawDirectoryPath: rawDirectoryPath, logDirectoryPath: logDirectoryPath, rawFileExtension: rawFileExtension}), success),
    getLatestLogFile: (success) =>
        $.get('/log', success),
    getLogsSince: (ts, success) =>
        $.post('/log', JSON.stringify({since: ts}), success),
    getFileNameOfLatestImage: (success) =>
        $.get('/convert', success),
    getFileNamesOfAllImages: (success) =>
        $.get('/images', success),
    getLatestMetrics: (intervalSeconds, success) =>
        $.get(`/metrics/${intervalSeconds}`, success),
    getLatestMetricsSince: (intervalSeconds, ts, success) =>
        $.post(`/metrics/${intervalSeconds}`, JSON.stringify({since: ts}), success),
    runEttl: (dummyCamera, setSettings, numberOfCaptures, intervalSeconds, success) =>
        $.post('/ettl', JSON.stringify(
            {
                dummyCamera: dummyCamera,
                setSettings: setSettings,
                numberOfCaptures: numberOfCaptures,
                intervalSeconds: intervalSeconds
            }), success)
};