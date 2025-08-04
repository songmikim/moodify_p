window.addEventListener('DOMContentLoaded', function() {

    // 이미지 변경 예정..
    const emotions = [
        { emotion: 'happiness', imagePath: '/common/images/sentiments/happiness.png', altText: '기쁨' },
        { emotion: 'sadness', imagePath: '/common/images/sentiments/sadness.png', altText: '슬픔' },
        { emotion: 'anger', imagePath: '/common/images/sentiments/anger.png', altText: '분노' },
        { emotion: 'fear', imagePath: '/common/images/sentiments/fear.png', altText: '불안' },
        { emotion: 'hurt', imagePath: '/common/images/sentiments/hurt.png', altText: '상처' },
        { emotion: 'surprise', imagePath: '/common/images/sentiments/surprise.png', altText: '당황' }
    ];

    const tabs = document.getElementById('songTabs');
    const contents = document.getElementById('tabContents');

    if (tabs) {
        emotions.forEach(({ emotion, imagePath, altText }) => {
            const button = document.createElement('button');
            button.type = 'button';
            button.dataset.emotion = emotion; // 영문 코드 보관
            button.dataset.sentiment = altText; // 한글 감정명 저장

            const img = document.createElement('img');
            img.src = imagePath;
            img.alt = altText;
            img.title = altText;
            button.title = altText;

            button.appendChild(img);
            tabs.appendChild(button);

            // 버튼 클릭 시 탭 활성화 및 곡 로드
            button.addEventListener('click', function() {
                tabs.querySelectorAll('button').forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                loadSongs(this.dataset.sentiment);
            });
        });

        // 첫 번째 버튼 활성화 및 곡 로드
        const firstButton = tabs.querySelector('button');
        if (firstButton) {
            firstButton.classList.add('active');
            loadSongs(firstButton.dataset.sentiment);
        }
    }

    // 곡 로드 함수
    function loadSongs(emotion) {
        const { ajaxLoad } = commonLib;
        ajaxLoad(`/api/mypage/recommend-songs?emotion=${emotion}`, (res) => {
            const songs = Array.isArray(res.data) ? res.data : [];
            renderSongs(songs, res.message);
        }, (err) => console.error(err));
    }

    // 화면에 곡 리스트 렌더링
    function renderSongs(songs, message) {
        contents.innerHTML = '';
        const ul = document.createElement('ul');
        const { modal } = commonLib;

        if (songs.length === 0) {
            const li = document.createElement('li');
            li.textContent = message || '추천된 곡이 없습니다.';
            ul.appendChild(li);
        } else {
            songs.forEach(song => {
                const li = document.createElement('li');
                li.classList.add('modal-open');
                li.dataset.url = `/diary/recommend/${song.seq}`;
                li.dataset.title = `${song.song} - ${song.artist}`;
                li.dataset.width = '500';
                li.dataset.height = '500';
                li.textContent = `${song.song} - ${song.artist}`;
                li.addEventListener('click', function() {
                    modal.open(null, this.dataset.url, this.dataset.width, this.dataset.height, this.dataset.title);
                });
                ul.appendChild(li);
            });
        }

        contents.appendChild(ul);
    }
});