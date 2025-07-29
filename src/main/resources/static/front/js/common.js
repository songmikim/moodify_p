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
                }, 1000)

            }

            if (!menuEl.classList.contains("on")) {
                // 열기
                const div = document.createElement("div");
                div.id = "modal-bg";
                document.body.append(div);

                div.addEventListener("click", () => this.click());
            }

            menuEl.classList.toggle("on");


        });
    }
    /* 더보기 메뉴 클릭 처리 E */
});