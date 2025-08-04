window.addEventListener("DOMContentLoaded", function() {
    const { fileManager } = commonLib;
    const removeEl = document.querySelector(".profile-image .remove");
    if (removeEl) {
        removeEl.addEventListener("click", function() {
            if (confirm('정말 삭제하겠습니까?')) {
                const { seq } = this.dataset;
                fileManager.delete(seq);
            }
        });
    }
});

/* 파일 업로드 후속 처리 */
function fileUploadCallback(items) {
    if (!items || items.length === 0) return;
    const { seq } = items[0];
    let html = document.getElementById("image_tpl").innerHTML;
    const targetEl = document.querySelector(".profile-image .inner")
    const imageUrl = commonLib.getUrl(`/file/thumb?seq=${seq}&width=250&height=250&crop=true`)
    html = html.replace(/\[seq\]/g, seq)
                .replace(/\[imageUrl\]/g, imageUrl)
    const domParser = new DOMParser();
    const dom = domParser.parseFromString(html, "text/html");
    const el = dom.querySelector(".file-image");
    targetEl.append(el);
    targetEl.parentElement.classList.add("uploaded")
    el.querySelector(".file-upload-btn").va;
    const removeEl = el.querySelector(".remove");
    const { fileManager } = commonLib;
    removeEl.addEventListener("click", function () {
            if (confirm('정말 삭제하겠습니까?')) {
                fileManager.delete(seq);
            }
    });
}

/**
* 삭제 후속 처리
*
*/
function fileDeleteCallback() {
    const el = document.querySelector(".profile-image .inner");
    if (el) {
        el.innerHTML = "";
        document.querySelector(".profile-image").classList.remove("uploaded");
    }
}