document.addEventListener('DOMContentLoaded', function () {
    const csrfToken = document.querySelector("meta[name='csrf_token']")?.content;
    const csrfHeader = document.querySelector("meta[name='csrf_header']")?.content || "X-CSRF-TOKEN";

    document.querySelectorAll('.image-input').forEach(input => {
        input.addEventListener('change', async function () {
            const sentiment = this.dataset.sentiment;
            const file = this.files[0];
            if (!file) return;

            const formData = new FormData();
            formData.append('file', file);
            formData.append('sentiment', sentiment);

            try {
                const res = await fetch('/admin/diary/image/upload', {
                    method: 'POST',
                    body: formData,
                    credentials: 'same-origin', // 세션 쿠키 포함
                    headers: {
                        [csrfHeader]: csrfToken // CSRF 토큰 포함
                        // Content-Type은 FormData 사용할 경우 자동 설정 (절대 수동 설정 금지!)
                    }
                });

                if (res.ok) {
                    const newImagePath = await res.text();
                    this.previousElementSibling.src = newImagePath;
                    alert('이미지가 변경되었습니다.');
                } else if (res.status === 401) {
                    alert('로그인이 필요합니다.');
                } else if (res.status === 403) {
                    alert('요청이 차단되었습니다. (CSRF 설정 확인)');
                } else {
                    alert('업로드 실패 (' + res.status + ')');
                }
            } catch (error) {
                console.error('업로드 중 오류 발생:', error);
                alert('업로드 중 오류가 발생했습니다.');
            }
        });
    });
});
