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
        // 🔒 비밀번호와 함께 직접 이동
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/board/guest-' + action;

        form.innerHTML = `
            <input type="hidden" name="seq" value="${seq}">
            <input type="hidden" name="guestPw" value="${password}">
        `;

        document.body.appendChild(form);
        form.submit();
    }
}

function deletePost(seq) {
    if (confirm('정말 삭제하시겠습니까?')) {
        location.href = '/board/delete/' + seq;
    }
}