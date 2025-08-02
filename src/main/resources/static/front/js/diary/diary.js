window.addEventListener("DOMContentLoaded", function () {
    const editBtn = document.getElementById('btn-edit');
    const saveBtn = document.getElementById('btn-save');
    const dateInput = document.getElementById('date');
    const editable = document.querySelectorAll('#title, #weather, #content, #btn-save');

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
            editBtn.remove();                // 수정하기 버튼을 지운다
        });
    }

    if (dateInput) {
        dateInput.addEventListener('change', function () {
            const selectedDate = this.value;
            if (selectedDate) {
                window.location.href = `/diary/${selectedDate}`;
            }
        });
    }
});