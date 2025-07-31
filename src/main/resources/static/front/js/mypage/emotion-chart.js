window.addEventListener('DOMContentLoaded', function() {
    const monthEl = document.getElementById('month');
    const ctx = document.getElementById('emotionChart').getContext('2d');
    let chart;

    // 감정별 색상 매핑
    const emotionColors = {
        '행복': '#fcbf49',
        '기쁨': '#fcbf49',
        '슬픔': '#4e79a7',
        '분노': '#e15759',
        '불안': '#76b7b2',
        '상처': '#a05195',
        '당황': '#59a14f'
    };


    /**
     * API에서 받은 데이터를 차트에 필요한 형태로 변환
     * {"2025-06-01": {"불안":1, "상처":1}, ...}
     * -> { labels: ["2025-06", "2025-07"...], datasets: [{label:"불안", data:[1,2...]}, ...] }
     */
    const convertData = (raw) => {
        const months = Object.keys(raw).sort();
        const emotions = new Set();

        // 모든 감정 키 수집
        months.forEach(date => {
            const stat = raw[date] || {};
            Object.keys(stat).forEach(e => emotions.add(e));
        });

        const labels = months.map(d => d.substring(0,7));
        const datasets = [];

        emotions.forEach(emotion => {
            const data = months.map(date => {
                const stat = raw[date] || {};
                return stat[emotion] || 0;
            });
            datasets.push({
                label: emotion,
                backgroundColor: emotionColors[emotion] || '#4e79a7',
                data
            });
        });

        return { labels, datasets };
    };

    const loadData = (sDate, eDate) => {
        const { ajaxLoad } = commonLib;

        const params = new URLSearchParams({ type: 'MONTHLY' });
        if (sDate) params.append('sDate', sDate);
        if (eDate) params.append('eDate', eDate);

        ajaxLoad(`/api/mypage/emotion?${params.toString()}`, (res) => {
            const { labels, datasets } = convertData(res);

            if (chart) chart.destroy();
            chart = new Chart(ctx, {
                type: 'bar',
                data: { labels, datasets },
                options: {
                    responsive: true,
                    scales: {
                        x: { stacked: true },
                        y: {
                            stacked: true,
                            beginAtZero: true
                        }
                    }
                }
            });
        });
    };

    const now = new Date();
    monthEl.value = now.toISOString().slice(0,7);

    // 최근 1년 데이터를 기본 로드
    loadData();

    monthEl.addEventListener('change', function() {
        const [year, month] = this.value.split('-');
        const start = `${year}-${month}-01`;
        const endDate = new Date(year, parseInt(month, 10), 0).getDate();
        const end = `${year}-${month}-${endDate}`;
        loadData(start, end);
    });
});