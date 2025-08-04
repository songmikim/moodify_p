window.addEventListener('DOMContentLoaded', function() {

    const { ajaxLoad } = commonLib;
    const emotions = [
        { emotion: 'happiness', code: '기쁨' },
        { emotion: 'surprise', code: '당황' },
        { emotion: 'anger', code: '분노' },
        { emotion: 'fear', code: '불안' },
        { emotion: 'hurt', code: '상처' },
        { emotion: 'sadness', code: '슬픔' }
    ];

    const tabs = document.getElementById('songTabs');
    const contents = document.getElementById('tabContents');
    let currentEmotion;
    const images = window.emotionImages || {};
    if (tabs) {
        emotions.forEach(({ emotion, code }) => {
            const button = document.createElement('button');
            button.type = 'button';
            button.dataset.emotion = emotion;
            button.dataset.sentiment = code;

            const img = document.createElement('img');
            img.src = images[code];
            img.alt = code;

            button.appendChild(img);
            tabs.appendChild(button);

            button.addEventListener('click', function() {
                tabs.querySelectorAll('button').forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                loadSongs(this.dataset.sentiment);
            });
        });

        const firstButton = tabs.querySelector('button');
        if (firstButton) {
            firstButton.classList.add('active');
            loadSongs(firstButton.dataset.sentiment);
        }
    }

    // 곡 로드 함수
    function loadSongs(emotion, page = 1) {
        currentEmotion = emotion;
        const { ajaxLoad } = commonLib;
        ajaxLoad(`/api/mypage/recommend-songs?emotion=${emotion}&page=${page}`, (res) => {
            const data = res.data || {};
            const songs = Array.isArray(data.items) ? data.items : [];
            renderSongs(songs, res.message);
            renderPagination(data.pagination);
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


    function renderPagination(pagination) {
        if (!pagination) return;

        const wrapper = document.createElement('div');
        wrapper.className = 'pagination-wrapper';

        const div = document.createElement('div');
        div.className = 'pagination';

        const { page, pages = [], prevRangePage, nextRangePage } = pagination;

        const addLink = (p, text) => {
            if (!p) return;
            const a = document.createElement('a');
            a.href = '#';
            a.textContent = text || p;
            if (p === page) a.classList.add('on');
            a.addEventListener('click', function(e) {
                e.preventDefault();
                loadSongs(currentEmotion, p);
            });
            div.appendChild(a);
        };

        addLink(prevRangePage, '이전');
        pages.forEach(pg => addLink(parseInt(pg[0])));
        addLink(nextRangePage, '다음');

        wrapper.appendChild(div);
        contents.appendChild(wrapper);
    }
});