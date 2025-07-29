document.addEventListener('DOMContentLoaded', function () {
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
                    body: formData
                });

                if (res.ok) {
                    const newImagePath = await res.text();
                    this.previousElementSibling.src = newImagePath;
                    alert('이미지가 변경되었습니다.');
                } else {
                    alert('업로드 실패');
                }
            } catch (error) {
                console.error('업로드 중 오류 발생:', error);
                alert('업로드 중 오류가 발생했습니다.');
            }
        });
    });
});
