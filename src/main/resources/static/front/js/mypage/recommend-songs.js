window.addEventListener('DOMContentLoaded', function() {

    // JS 내 샘플 추천 곡 데이터 (컨트롤러에서 받을 때까지 임시 사용)
    const SAMPLE_SONGS = {
        happiness: ["Happy - Pharrell Williams", "Lovely Day - Bill Withers"],
        sadness:   ["Someone Like You - Adele", "Fix You - Coldplay"],
        anger:     ["Break Stuff - Limp Bizkit", "Killing In The Name - Rage Against The Machine"],
        fear:      ["Creep - Radiohead", "Thriller - Michael Jackson"],
        hurt:      ["Hurt - Johnny Cash", "Everybody Hurts - R.E.M."],
        surprise:  ["Surprise Yourself - Jack Garratt", "What A Wonderful World - Louis Armstrong"]
    };


    // 이미지 변경 예정..
    const emotions = [
        { emotion: 'happiness', imagePath: '/common/images/sentiments/happiness.png', altText: '기쁨' },
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

            // 버튼 클릭 시 탭 활성화 및 곡 로드
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

    // 곡 로드 함수 (컨트롤러가 준비되면 주석 해제)
    function loadSongs(emotion) {
        const songs = SAMPLE_SONGS[emotion] || [];

        // --- 컨트롤러 연동 시 여기를 주석 해제하세요 ---
        /*
        const { ajaxLoad } = commonLib;
        ajaxLoad(`/api/mypage/recommend-songs?emotion=${emotion}`, (res) => {
            const songs = Array.isArray(res.data) ? res.data : [];
            renderSongs(songs);
        }, (err) => console.error(err));
        return;
        */
        // -------------------------------------------

        // 샘플 데이터 렌더링
        renderSongs(songs);
    }

    // 화면에 곡 리스트 렌더링
    function renderSongs(songs) {
        contents.innerHTML = '';
        const ul = document.createElement('ul');
        songs.forEach(song => {
            const li = document.createElement('li');
            li.textContent = song;
            ul.appendChild(li);
        });
        contents.appendChild(ul);
    }
});