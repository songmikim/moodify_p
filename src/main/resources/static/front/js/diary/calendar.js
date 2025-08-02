function initCalendar(year, month) {
    const searchParams = new URLSearchParams(Location.search);
    searchParams.set("year", year);
    searchParams.set("month", month);

    location.search = searchParams.toString();
}

function calendarSelectCallback(date) {
    location.href = `/diary/${date}`;
}