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
        // TODO: 실제로는 서버에서 비밀번호 검증해야 함
        if (action === 'update') {
            location.href = '/board/update/' + seq;
        } else if (action === 'delete') {
            if (confirm('정말 삭제하시겠습니까?')) {
                location.href = '/board/delete/' + seq;
            }
        }
    }
}