window.addEventListener("DOMContentLoaded", function() {
    /* 캘린더 클릭 이벤트: 날짜 클릭 시 해당 날짜의 일기 페이지로 이동 S */

    /* 현재 페이지의 년, 월 값을 부모 창에 전달 */
    if (typeof parent.initCalendar === 'function') {
        const searchParams = new URLSearchParams(location.search);
        const date = new Date();
        const year = searchParams.get("year") ?? date.getFullYear();
        const month = searchParams.get("month") ?? date.getMonth() + 1;
        const from = searchParams.get("from");
        if (!from || from !== 'parent') {  // 무한로딩을 막기 위해 쿼리스트링에 parent가 없을 때만 실행
            parent.initCalendar(year, month);
        }
    }


    /* 캘린더 클릭 이벤트: 날짜 클릭 시 해당 날짜의 일기 페이지로 이동 E */


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