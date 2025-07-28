document.addEventListener('DOMContentLoaded', function () {
    let timer = null;

    function startAutoSave(gid) {
        const textarea = document.getElementById("content");

        timer = setInterval(() => {
            const content = textarea.value;

            fetch(`/sentiment/update-content?gid=${gid}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": /*[[${_csrf.token}]]*/ ''
                },
                body: JSON.stringify({ content: content })
            }).then(response => {
                if (!response.ok) {
                    console.error("일기 내용 저장 실패");
                }
            });
        }, 3000);
    }

    const gid = /*[[${gid}]]*/ '';
    if (gid) {
        startAutoSave(gid);
    }
});
