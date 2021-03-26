
const Shared = {
    copyQueryParamsToMenu: () => {
        const params = new URLSearchParams(window.location.search);
        $('#nav-index').attr('href', `index.html?${params}`);
        $('#nav-timelapse').attr('href', `timelapse.html?${params}`);
    }
}