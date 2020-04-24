window.addEventListener('DOMContentLoaded', (event) => {
    console.log('DOM fully loaded and parsed');
    document.querySelector(".content").style.height = `${document.body.offsetHeight-document.querySelector("nav.navbar").offsetHeight}px`;
});
