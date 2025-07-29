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
    const sideMenu = Array.from(document.getElementsByClassName('sideMenu'));
    const openBtn = Array.from(document.getElementsByClassName('openBtn'));
    const closeBtn = Array.from(document.getElementsByClassName('closeBtn'));
    const showMenu = () => {
        sideMenu.forEach(menu => menu.style.display = 'block');
    };

    const hideMenu = () => {
        sideMenu.forEach(menu => menu.style.display = 'none');
    };

    openBtn.forEach(btn => btn.addEventListener('click', showMenu));
    closeBtn.forEach(btn => btn.addEventListener('click', hideMenu));
});