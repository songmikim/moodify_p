window.addEventListener("DOMContentLoaded", function() {
    // 위지윅 에디터 로드 S
    const { loadEditor } = commonLib;
    const el = document.getElementById("content");
    loadEditor(el);
    // 위지윅 에디터 로드 E

});

function checkGuestPassword(action, seq) {
    const password = prompt('비밀번호를 입력하세요:');
    if (password) {
        // Spring Boot로 AJAX 요청
        fetch('/board/check-guest-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `seq=${seq}&password=${encodeURIComponent(password)}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                if (action === 'update') {
                    location.href = '/board/update/' + seq;
                } else if (action === 'delete') {
                    deletePost(seq);
                }
            } else {
                alert(data.message); // "비밀번호가 틀렸습니다"
            }
        })
        .catch(error => {
            alert('오류가 발생했습니다.');
        });
    }
}

function deletePost(seq) {
    if (confirm('정말 삭제하시겠습니까?')) {
        location.href = '/board/delete/' + seq;
    }
}