window.addEventListener('DOMContentLoaded', () => {
  const monthEl = document.getElementById('month');                         // <input type="month">
  const ctx     = document.getElementById('emotionChart').getContext('2d'); // 차트 그릴 컨텍스트
  let chart;                                                                // Chart.js 인스턴스

  // 감정별 색상 매핑
  const colors = {
    '행복':'#fcbf49','기쁨':'#fcbf49','슬픔':'#4e79a7',
    '분노':'#e15759','불안':'#76b7b2','상처':'#a05195','당황':'#59a14f'
  };

  //한 달치 통계 로드 → 차트 렌더링
  const loadChart = async monthStr => {
    // 시작일: YYYY-MM-01
    const start = `${monthStr}-01`;
    // 해당 월 마지막 일 계산해서 YYYY-MM-DD 생성
    const [year, month] = monthStr.split('-');
    const endDay        = new Date(year, month, 0).getDate();
    const end           = `${monthStr}-${endDay}`;

    // 서버 호출
    const res = await fetch(
      `/api/mypage/emotion?type=MONTHLY&sDate=${start}&eDate=${end}`
    ).then(r => r.json());

    // 날짜별 감정 키, 합계 계산
    const dates    = Object.keys(res).sort();
    const emotions = [...new Set(dates.flatMap(d => Object.keys(res[d] || {})))];
    const totals   = emotions.map(e =>
      dates.reduce((sum, d) => sum + (res[d]?.[e] || 0), 0)
    );

    // 기존 차트 제거 후 새로 그리기
    chart?.destroy();
    chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: emotions,
        datasets: [{
          label: `${monthStr} 감정 합계`,
          data: totals,
          backgroundColor: emotions.map(e => colors[e] || '#4e79a7')
        }]
      },
      options: {
        responsive: true,
        scales: {
          x: { title: { display: true, text: '감정' } },
          y: { beginAtZero: true, title: { display: true, text: '횟수' } }
        }
      }
    });
  };

  // 초기값: 이번 달로 세팅
  (() => {
    const now = new Date();
    const MM  = String(now.getMonth() + 1).padStart(2, '0');
    monthEl.value = `${now.getFullYear()}-${MM}`;
  })();

  // 첫 로드 & 변경 시 차트 갱신
  loadChart(monthEl.value);
  monthEl.addEventListener('change', () => loadChart(monthEl.value));
});