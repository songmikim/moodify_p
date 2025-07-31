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
        // 서버에 비밀번호 + 액션 정보 전송
        fetch('/board/check-guest-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `seq=${seq}&password=${encodeURIComponent(password)}&action=${action}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // 성공시 해당 액션 실행
                performAction(action, seq);
            } else {
                alert(data.message || '비밀번호가 틀렸습니다');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생했습니다.');
        });
    }
}

function performAction(action, seq) {
    if (action === 'update') {
        location.href = '/board/update/' + seq;
    } else if (action === 'delete') {
        if (confirm('정말 삭제하시겠습니까?')) {
            location.href = '/board/delete/' + seq;
        }
    } else if (action === 'view') {
        location.href = '/board/view/' + seq;
    }
}

function editComment(seq) {
    // 댓글 수정 로직
    console.log('Edit comment:', seq);
}

function deleteComment(seq) {
    if (confirm('댓글을 삭제하시겠습니까?')) {
        // 댓글 삭제 요청
        fetch(`/board/comment/delete/${seq}`, {
            method: 'POST'
        }).then(() => {
            location.reload();
        });
    }
}