document.addEventListener('DOMContentLoaded', function () {
    const deleteForm = document.getElementById('deleteForm');
    if (deleteForm) {
        deleteForm.addEventListener('submit', function (e) {
            e.preventDefault();
            Swal.fire({
                title: '탈퇴하시겠습니까?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: '확인',
                cancelButtonText: '취소'
            }).then((result) => {
                if (result.isConfirmed) {
                    deleteForm.submit();  // 실제 제출
                }
            });
        });
    }


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
});