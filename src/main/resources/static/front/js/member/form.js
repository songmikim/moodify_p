/* 파일 업로드 후속 처리 */
function fileUploadCallback(items) {
    if (!items || items.length === 0) return;
    const { seq } = items[0];
    let html = document.getElementById("image_tpl").innerHTML;
    const targetEl = document.querySelector(".profile-image .inner")
    const imageUrl = commonLib.getUrl(`/uploads/thumbs?seq=${seq}&width=250&height=250&crop=true`)
    html = html.replace(/\[seq\]/g, seq)
                .replace(/\[imageUrl\]/g, imageUrl)
    const domParser = new DOMParser();
    const dom = domParser.parseFromString(html, "text/html");
    const el = dom.querySelector(".file-image");
    targetEl.append(el);
    const removeEl = el.querySelector(".remove");
    removeEl.addEventListener("click", function () {

    });
}