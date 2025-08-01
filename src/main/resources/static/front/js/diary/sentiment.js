window.addEventListener("DOMContentLoaded", function () {
    const { ajaxLoad } = commonLib;

    // 2초마다 감정 분석을 위한 데이터 전송
    setInterval(() => {
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

//    const gidInput = document.getElementById("gid");
//    const contentValue = document.getElementById("content");
//
//    const gid = gidInput?.value;
//    if (!gid || !contentValue) return;
//
//    const csrfToken = document.querySelector("meta[name='csrf_token']")?.content;
//    const csrfHeader = document.querySelector("meta[name='csrf_header']")?.content || "X-CSRF-TOKEN";
//
//    let isSaved = false;
//
//    // 3초마다 자동 저장
//    const autoSaveTimer = setInterval(() => {
//        if (isSaved) return;
//
//        const content = contentValue.value;
//
//        fetch(`/diary/update?gid=${gid}`, {
//            method: "POST",
//            credentials: "same-origin",
//            headers: {
//                "Content-Type": "application/json",
//                [csrfHeader]: csrfToken
//            },
//            body: JSON.stringify({ content })
//        }).catch(err => {
//            console.error("자동 저장 실패:", err);
//        });
//    }, 3000);
//
//    // 저장 버튼을 클릭할 때만 저장 완료 처리
//    document.querySelector("form")?.addEventListener("submit", function () {
//        isSaved = true;
//        clearInterval(autoSaveTimer);
//    });
//
//    // 페이지 이탈 시 경고창
//    window.addEventListener("beforeunload", function (e) {
//        if (!isSaved) {
//            const message = "작성 중인 내용을 저장하지 않고 나가시겠습니까?";
//            e.preventDefault();
//            e.returnValue = message;
//            return message;
//        }
//    });
//
//    // 모든 종료/이동 시점에 삭제 요청
//    window.addEventListener("beforeunload", (e) => {
//        if (!isSaved && gid) {
//            navigator.sendBeacon("/diary/delete", new URLSearchParams({ gid }));
//        }
//    });
//});
