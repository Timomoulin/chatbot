
    document.querySelectorAll(".message-content").forEach(element => {
    const markdown = element.textContent;

    // Conversion du Markdown en HTML
    const html = marked.parse(markdown);

    // Nettoyage du HTML pour éviter les injections XSS
    element.innerHTML = DOMPurify.sanitize(html);
});
