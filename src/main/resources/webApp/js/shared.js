
const Shared = {
    copyQueryParamsToMenu: () => {
        const params = new URLSearchParams(window.location.search);
        $('#nav-monitor').attr('href', `index.html?${params}`);
        $('#nav-preview').attr('href', `preview.html?${params}`);
    }
}