window.addEventListener("DOMContentLoaded", function () {
    const editBtn = document.getElementById('btn-edit');
    const deleteBtn = document.getElementById('btn-delete');
    const saveBtn = document.getElementById('btn-save');
    const dateInput = document.getElementById('date');
    const editable = document.querySelectorAll('#title, #weather, #content, #btn-save');

    if (dateInput) {
        dateInput.addEventListener('change', function () {
            const selectedDate = this.value;
            if (selectedDate) {
                window.location.href = `/diary/${selectedDate}`;
            }
        });
    }

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
                const gid = document.querySelector('input[name="gid"]')?.value;
                const date = document.querySelector('input[name="date"]')?.value;
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