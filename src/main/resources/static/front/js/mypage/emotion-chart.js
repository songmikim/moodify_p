window.addEventListener('DOMContentLoaded', function() {
    const monthEl = document.getElementById('month');
    const ctx = document.getElementById('emotionChart').getContext('2d');
    let chart;

    const emotionColors = {
        '행복': '#fcbf49',
        '기쁨': '#fcbf49',
        '슬픔': '#4e79a7',
        '분노': '#e15759',
        '불안': '#76b7b2',
        '상처': '#a05195',
        '당황': '#59a14f'
    };

    const loadData = (year, month) => {
        const { ajaxLoad } = commonLib;
        ajaxLoad(`/api/mypage/emotion?year=${year}&month=${month}`, (res) => {
            const labels = Object.keys(res.data);
            const data = Object.values(res.data);
            const colors = labels.map(l => emotionColors[l] || '#4e79a7');

            if (chart) chart.destroy();
            chart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '감정 수',
                        backgroundColor: colors,
                        data: data
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        });
    };

    const now = new Date();
    monthEl.value = now.toISOString().slice(0,7);
    const [y, m] = monthEl.value.split('-');
    loadData(y, m);

    monthEl.addEventListener('change', function() {
        const [year, month] = this.value.split('-');
        loadData(year, month);
    });
});