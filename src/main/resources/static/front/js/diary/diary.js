window.addEventListener("DOMContentLoaded", function () {
    const editBtn = document.getElementById('btn-edit');
    const deleteBtn = document.getElementById('btn-delete');
    const saveBtn = document.getElementById('btn-save');
    const dateInput = document.getElementById('date');
    const editable = document.querySelectorAll('#title, #weather, #content, #btn-save');

    const form = document.querySelector("form");
    const gid = document.querySelector('input[name="gid"]')?.value;
    const date = document.querySelector('input[name="date"]')?.value;
    const done = document.querySelector('input[name="done"]');
    let originalDate = dateInput?.value ?? null;

    window.isSaved = document.querySelector('main')?.dataset.saved === 'true';
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
                clearInterval(window.intervalId);
                done.value = true;

                // 생성된 sentiment 데이터 삭제가 필요한 경우
                if (!window.isSaved) {
                    window.deleteOrphanSentiments();

                    // 이탈은 허용
                    window.isSaved = true;
                    window.location.href = `/diary/${selectedDate}`;
                }
                // 이미 존재하는 일기면, done=true로 바꿔줌
                else {
                    window.updateDoneStatus(true, window.gid);
                    window.location.href = `/diary/${selectedDate}`;
                }
            } else {
                this.value = originalDate;
            }
        });
    }

    // 페이지 이탈 시 경고
    window.addEventListener("beforeunload", function (e) {
        if (!window.isSaved) {
            const message = "작성 중인 내용을 저장하지 않고 나가시겠습니까?";
            e.preventDefault();
            e.returnValue = message;
            return message;
        }
    });

    // 페이지 이탈 시 감정 분석 중단 및 삭제 요청
    window.addEventListener("beforeunload", (e) => {
        clearInterval(window.intervalId);

        // 저장 중이라면 삭제하지 않음
        if (!window.isSaved && window.gid && window.date) {
            window.deleteOrphanSentiments();
        }
        // 이미 존재하는 일기면, done=true로 바꿔줌
        if (window.isSaved && window.gid) {
            window.updateDoneStatus(true, window.gid);
        }
    });

    if (editBtn) {
        editBtn.addEventListener('click', () => {
            // 수정 가능한 상태로 만들기
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

            // done을 false로 설정
            if (done) {
                done.value = 'false';
            }

            if (window.gid) {
                window.updateDoneStatus(false, window.gid);
            }

            // 버튼 UI 처리
            saveBtn.textContent = '수정하기';  // 저장하기를 수정하기로 바꾸고
            editBtn.remove();                // 수정하기 버튼을 지움
            deleteBtn.style.right = 'calc(9.5% + 160px)';  // 삭제 버튼 위치를 기존 수정 버튼 위치로 옮김
        });
    }

    if (deleteBtn) {
        deleteBtn.addEventListener('click', () => {
            if (confirm('정말 삭제하시겠습니까?')) {

                if (!window.gid || !date) return;

                window.isSaved = true;  // 이탈 시 삭제 방지 + unload 알림 차단
                clearInterval(window.intervalId);  // 감정 분석 중단

                commonLib.ajaxLoad(
                    `/diary/delete/${window.gid}`,
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