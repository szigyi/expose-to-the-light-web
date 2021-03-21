
const Shared = {
    renderMenu: (params) =>
        `<a href="index.html?${params}"><h1>ettl web</h1></a>
        <a href="timelapse.html?${params}"><h4>timelapse</h4></a>`,

    copyQueryParamsToMenu: () =>
        $('#title').html(Shared.renderMenu(new URLSearchParams(window.location.search)))
}