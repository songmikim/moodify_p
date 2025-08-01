window.addEventListener("DOMContentLoaded", function() {
    // 위지윅 에디터 로드 S
    const { loadEditor } = commonLib;
    const el = document.getElementById("content");
    loadEditor(el);
    // 위지윅 에디터 로드 E
});

function checkGuestPassword(action, seq, comment = false) {
    const password = prompt('비밀번호를 입력하세요:');
    const path = comment ? '/board/comment/check-guest-password' : '/board/check-guest-password';

    if (password) {
        // 서버에 비밀번호 + 액션 정보 전송
        fetch(path, {
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
                if (comment === true) {
                    performCommentAction(action, seq);
                } else {
                    performAction(action, seq);
                }
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

function performCommentAction(action, seq) {
    if (action === 'update') {
        editComment(seq);
    } else if (action === 'delete') {
        if (confirm('댓글을 삭제하시겠습니까?')) {
            deleteComment(seq);
        }
    }
}

function editComment(seq) {
    // 댓글 인라인 수정 로직
    console.log('Edit comment:', seq);

    // 실제 인라인 수정 구현
    const commentItem = document.getElementById(`comment-${seq}`);
    if (commentItem) {
        const displayDiv = commentItem.querySelector('.comment-display');
        const editDiv = commentItem.querySelector('.comment-edit');

        if (displayDiv && editDiv) {
            displayDiv.style.display = 'none';
            editDiv.style.display = 'block';

            const textarea = editDiv.querySelector('textarea');
            if (textarea) {
                textarea.focus();
            }
        }
    }
}

function deleteComment(seq) {
    fetch(`/board/comment/delete/${seq}`, {
        method: 'POST'
    })
    .then(response => {
        if (response.ok) {
            location.reload();
        } else {
            alert('댓글 삭제에 실패했습니다.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('오류가 발생했습니다.');
    });
}