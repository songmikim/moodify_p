window.addEventListener("DOMContentLoaded", function () {
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