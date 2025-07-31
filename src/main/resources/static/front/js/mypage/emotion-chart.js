window.addEventListener('DOMContentLoaded', () => {
  const monthEl = document.getElementById('month');
  const ctx     = document.getElementById('emotionChart').getContext('2d');
  let chart;

  const colors = {
    '행복':'#fcbf49','기쁨':'#fcbf49','슬픔':'#4e79a7',
    '분노':'#e15759','불안':'#76b7b2','상처':'#a05195','당황':'#59a14f'
  };

  // 선택한 년·월에 해당하는 한 달치 감정 통계 불러오기
  const loadMonthData = async (year, month) => {
    const start  = `${year}-${month}-01`;
    const endDay = new Date(year, month, 0).getDate();
    const end    = `${year}-${month}-${endDay}`;

    const params = new URLSearchParams({
      type:  'MONTHLY',
      sDate: start,
      eDate: end
    });

    // 서버 호출 및 JSON 응답 파싱
    const res = await fetch(`/api/mypage/emotion?${params}`).then(r => r.json());

    const dates    = Object.keys(res).sort();
    // 모든 날짜에 등장하는 감정 이름을 중복 제거해 추출
    const emotions = [...new Set(dates.flatMap(d => Object.keys(res[d] || {})))];
    // 각 감정별로 한 달 동안의 총합 계산
    const totals   = emotions.map(e =>
      dates.reduce((sum, date) => sum + (res[date]?.[e] || 0), 0)
    );

    chart?.destroy();
    chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: emotions,  // X축: 감정 이름들
        datasets: [{
          label: `${year}-${month} 감정 합계`,  // 범례에 표시될 텍스트
          data: totals,  // Y축 값: 각 감정별 총합
          backgroundColor: emotions.map(e => colors[e] || '#4e79a7') // 색상 매핑, 없으면 기본 파랑
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

  // 이번 달 로 설정
  const now   = new Date();
  const currY = now.getFullYear();
  const currM = String(now.getMonth() + 1).padStart(2, '0');

  monthEl.value = `${currY}-${currM}`;
  loadMonthData(currY, currM);
  // 월 선택 입력값이 바뀔 때마다 데이터를 불러와 차트 갱신
  monthEl.addEventListener('change', () => {
    const [y, m] = monthEl.value.split('-');
    loadMonthData(y, m);
  });
});