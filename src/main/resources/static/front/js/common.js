document.addEventListener('DOMContentLoaded', function () {
    const deleteForm = document.getElementById('deleteForm');
    if (deleteForm) {
        deleteForm.addEventListener('submit', function (e) {
            e.preventDefault();
            Swal.fire({
                title: '탈퇴하시겠습니까?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: '확인',
                cancelButtonText: '취소'
            }).then((result) => {
                if (result.isConfirmed) {
                    deleteForm.submit();  // 실제 제출
                }
            });
        });
    }
    const sideMenu = document.getElementsByClassName('sideMenu');
    const openBtn = document.getElementsByClassName('openBtn');
    const closeBtn = document.getElementsByClassName('closeBtn');

    // 메뉴 열기
    openBtn.addEventListener('click', () => {
        sideMenu.style.display = 'block';
    });

    // 메뉴 닫기
    closeBtn.addEventListener('click', () => {
        sideMenu.style.display = 'none';
    });
});