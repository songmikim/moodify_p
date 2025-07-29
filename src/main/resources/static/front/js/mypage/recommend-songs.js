window.addEventListener('DOMContentLoaded', function() {
    // 이미지 변경 예정..
    const emotions = [
        { emotion: 'happiness', imagePath: '/common/images/sentiments/happiness.png', altText: '행복' },
        { emotion: 'sadness', imagePath: '/common/images/sentiments/happiness.png', altText: '슬픔' },
        { emotion: 'anger', imagePath: '/common/images/sentiments/happiness.png', altText: '분노' },
        { emotion: 'fear', imagePath: '/common/images/sentiments/happiness.png', altText: '불안' },
        { emotion: 'hurt', imagePath: '/common/images/sentiments/happiness.png', altText: '상처' },
        { emotion: 'surprise', imagePath: '/common/images/sentiments/happiness.png', altText: '당황' }
    ];

    const tabs = document.getElementById('songTabs');
    const contents = document.getElementById('tabContents');

    if (tabs) {
        emotions.forEach(({ emotion, imagePath, altText }) => {
            const button = document.createElement('button');
            button.type = 'button';
            button.dataset.emotion = emotion;

            const img = document.createElement('img');
            img.src = imagePath;
            img.alt = altText;

            button.appendChild(img);
            tabs.appendChild(button);

            // 버튼 클릭 이벤트 추가
            button.addEventListener('click', function() {
                tabs.querySelectorAll('button').forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                loadSongs(emotion);
            });
        });

        // 첫 번째 버튼 활성화 및 곡 로드
        const firstButton = tabs.querySelector('button');
        if (firstButton) {
            firstButton.classList.add('active');
            loadSongs(firstButton.dataset.emotion);
        }
    }

    // 곡 로드 함수
    function loadSongs(emotion) {
        // 곡 데이터를 가져오는 로직을 추가 예정
        console.log(`Loading songs for emotion: ${emotion}`);
        contents.innerHTML = '<ul><li>Song 1</li><li>Song 2</li></ul>';
    }
});