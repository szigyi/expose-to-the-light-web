
const Api = {
    setConfig: (rawDirectoryPath, logDirectoryPath, rawFileExtension, logLevel, success) =>
        $.post('/config', JSON.stringify({rawDirectoryPath: rawDirectoryPath, logDirectoryPath: logDirectoryPath, rawFileExtension: rawFileExtension, logLevel: logLevel}), success),
    getLatestLogFile: (success) =>
        $.get('/log', success),
    getLogsSince: (ts, success) =>
        $.post('/log', JSON.stringify({since: ts}), success),
    getImageDirectories: (success) =>
        $.post('/images/directories', success),
    getFileNameOfLatestImage: (success) =>
        $.post('/images/convert', success),
    getFileNamesOfAllImages: (baseDir, quickMode, success) =>
        $.post('/images', JSON.stringify({ directory: baseDir, quickMode: quickMode }), success),
    getLatestMetrics: (success) =>
        $.get('/metrics', success),
    getLatestMetricsSince: (ts, success) =>
        $.post('/metrics', JSON.stringify({since: ts}), success),
    runEttl: (dummyCamera, setSettings, numberOfCaptures, intervalSeconds, success) =>
        $.post('/ettl/start', JSON.stringify(
            {
                dummyCamera: dummyCamera,
                setSettings: setSettings,
                numberOfCaptures: numberOfCaptures,
                intervalSeconds: intervalSeconds
            }), success),
    stopEttl: (success) =>
        $.post('/ettl/stop', success),
    isEttlRunning: (success) =>
        $.post('/ettl/running', success)
};