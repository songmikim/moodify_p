window.addEventListener("DOMContentLoaded", function() {
    const selectDays = document.getElementsByClassName("select-day");
    for (const el of selectDays) {
        el.addEventListener("click", function() {
            if (typeof parent.calendarSelectCallback === 'function') {
                const {date} = this.dataset;
                parent.calendarSelectCallback(date);
            }
        });
    }
});