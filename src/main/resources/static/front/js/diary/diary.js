window.addEventListener("DOMContentLoaded", function () {
    const autoResizeContent = document.getElementById('content');
    const editBtn = document.getElementById('btn-edit');
    const dateInput = document.getElementById('date');
    const editable = document.querySelectorAll('#title, #weather, #content');

    autoResizeContent.addEventListener('input', function () {
      this.style.height = 'auto';           // 높이 초기화
      this.style.height = this.scrollHeight + 'px';  // 내용에 따라 높이 조절
    });

    if (editBtn) {
        editBtn.addEventListener('click', () => {
            editable.forEach(el => el.removeAttribute('disabled'));
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