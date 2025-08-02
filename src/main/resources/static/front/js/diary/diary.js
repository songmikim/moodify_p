window.addEventListener("DOMContentLoaded", function () {
    const editBtn = document.getElementById('btn-edit');
    const deleteBtn = document.getElementById('btn-delete');
    const saveBtn = document.getElementById('btn-save');
    const dateInput = document.getElementById('date');
    const editable = document.querySelectorAll('#title, #weather, #content, #btn-save');

    const form = document.querySelector("form");
    const gid = document.querySelector('input[name="gid"]')?.value;
    const date = document.querySelector('input[name="date"]')?.value;
    let originalDate = dateInput?.value ?? null;

    window.isSaved = false;
    window.gid = gid;
    window.date = date;

    // 저장 버튼 클릭 시 저장 완료 처리
    form?.addEventListener("submit", function () {
        window.isSaved = true;
        clearInterval(window.intervalId);
    });

    // 날짜 변경 시 수동 확인 후 이동 or 취소 처리
    if (dateInput) {
        dateInput.addEventListener("change", function () {
            const selectedDate = this.value;
            if (selectedDate === originalDate) return;

            const confirmMessage = "작성 중인 내용을 저장하지 않고 나가시겠습니까?";
            const proceed = window.confirm(confirmMessage);

            if (proceed) {
                isSaved = true; // 이탈 허용 → 삭제 안 하도록 플래그 변경
                window.location.href = `/diary/${selectedDate}`;
            } else {
                this.value = originalDate;
            }
        });
    }

    // 페이지 이탈 시 경고
    window.addEventListener("beforeunload", function (e) {
        if (!isSaved) {
            const message = "작성 중인 내용을 저장하지 않고 나가시겠습니까?";
            e.preventDefault();
            e.returnValue = message;
            return message;
        }
    });

    // 삭제 버튼 클릭 시 감정 분석 중단
    if (deleteBtn) {
        deleteBtn.addEventListener('click', () => {
            clearInterval(window.intervalId);
        });
    }

    // 페이지 이탈 시 감정 분석 중단 및 삭제 요청
    window.addEventListener("beforeunload", (e) => {
        clearInterval(window.intervalId);

        // 저장 중이라면 삭제하지 않음
        if (!window.isSaved && window.gid && window.date) {
            const payload = JSON.stringify({ gid: window.gid });
            navigator.sendBeacon(`/diary/delete`, new Blob([payload], {
                type: "application/json"
            }));
        }
    });

    if (editBtn) {
        editBtn.addEventListener('click', () => {
            editable.forEach(el => {
                el.removeAttribute('disabled');

                // 같은 name을 가진 hidden input 제거
                const name = el.getAttribute('name');
                if (name) {
                    const hiddenInput = document.querySelector(`input[type="hidden"][name="${name}"]`);
                    if (hiddenInput) {
                        hiddenInput.remove();
                    }
                }
            });
            saveBtn.textContent = '수정하기';  // 저장하기를 수정하기로 바꾸고
            editBtn.remove();                // 수정하기 버튼을 지움
            deleteBtn.style.right = 'calc(9.5% + 160px)';  // 삭제 버튼 위치를 기존 수정 버튼 위치로 옮김
        });
    }

    if (deleteBtn) {
        deleteBtn.addEventListener('click', () => {
            if (confirm('정말 삭제하시겠습니까?')) {

                if (!gid || !date) return;

                commonLib.ajaxLoad(
                    `/diary/delete/${gid}`,
                    () => {
                        location.href = `/diary/${date}`;
                    },
                    (err) => {
                        console.error(err);
                        alert('삭제에 실패했습니다.');
                    },
                    'POST',
                    JSON.stringify({ date }),
                    { 'Content-Type': 'application/json' }
                );
            }
        });
    }
});