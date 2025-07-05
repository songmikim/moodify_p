document.addEventListener('DOMContentLoaded', function () {

    /* 더보기 메뉴 클릭 처리 S */
    const moreMenus = document.getElementsByClassName("more-menu");
    for (const el of moreMenus) {
        el.addEventListener("click", function() {
            const menuEl = document.querySelector("body > aside");

            const el = document.getElementById("modal-bg");
            if (el) {
                setTimeout(function() {
                    el.parentElement.removeChild(el);
                }, 300)

            }

            if (!menuEl.classList.contains("on")) {
                // 열기
                const div = document.createElement("div");
                div.id = "modal-bg";
                document.body.append(div);
                setTimeout(() => {
                    div.classList.add("show");  // opacity 1 적용 → 트랜지션 발생
                }, 10);

                div.addEventListener("click", () => this.click());
                } else {
                    const existingModal = document.getElementById("modal-bg");
                    if (existingModal) {
                        existingModal.classList.remove("show"); // fade-out 시작
                        setTimeout(() => {
                            existingModal?.remove(); // 트랜지션 끝나고 제거
                        }, 300);
                    }
                }
            menuEl.classList.toggle("on");


        });
    }
    /* 더보기 메뉴 클릭 처리 E */
    /* textarea 자동 늘리기 */
    const textareas = document.querySelectorAll('textarea');
    const editBtn = document.getElementById('btn-edit');
    const dateInput = document.getElementById('date');
    const editable = document.querySelectorAll('#title, #weather, #content');

    textareas.forEach(function (textarea) {
        // 처음 로드 시에도 높이 조절
        textarea.style.height = 'auto';
        textarea.style.height = textarea.scrollHeight + 'px';

        // 입력 시 자동 높이 조절
        textarea.addEventListener('input', function () {
            this.style.height = 'auto';               // 높이 초기화
            this.style.height = this.scrollHeight + 'px'; // 내용에 따라 높이 조절
        });
    });
});