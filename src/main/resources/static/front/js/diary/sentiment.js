window.addEventListener("DOMContentLoaded", function () {
    const { ajaxLoad } = commonLib;
    const contentInput = document.getElementById('content');

    // 2초마다 감정 분석을 위한 데이터 전송
    window.intervalId = setInterval(() => {
        if (contentInput.disabled) return;

        const formData = new FormData(frmSave);
        ajaxLoad(`/diary/sentiment`, null, null, "POST", formData, null, true);
    }, 2000);

    // 현재 감정을 실시간 출력하기 위한 데이터 조회
    const gid = frmSave.gid.value;
    const el = document.querySelector(".current-sentiment");
    let prevText = "";  // 일단 임시로 텍스트. 추후에 이미지로 바꾸자.
    setInterval(() => {
        ajaxLoad(`/diary/sentiment/${gid}`, (items) => {
            let text = "";
            let timeoutId = null;
            if (items.length > 0) {
                const item = items.pop();
                text = item.startsWith("<img") ? item : item.split(" ")[0];  // 감정 단어 중 대분류 6개만 사용

                console.log("text");
                if (text && text !== prevText) {
                    el.classList.add("show");
                    el.innerHTML = text;
                    timeoutId = setTimeout(() => {
                        el.classList.remove("show");
                        clearTimeout(timeoutId);
                    }, 3500);

                    prevText = text;
                }
            }
        });
    }, 3500);
});