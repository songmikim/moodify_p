window.addEventListener("DOMContentLoaded", function () {
    const autoResize = document.getElementById('autoResize');

    autoResize.addEventListener('input', function () {
      this.style.height = 'auto';           // 높이 초기화
      this.style.height = this.scrollHeight + 'px';  // 내용에 따라 높이 조절
    });
});