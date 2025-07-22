const sideMenu = document.getElementsByClassName('sideMenu');
const openBtn = document.getElementsByClassName('openBtn');
const closeBtn = document.getElementsByClassName('openBtn');

// 메뉴 열기
openBtn.addEventListener('click', () => {
  sideMenu.style.display = 'block';
});

// 메뉴 닫기
closeBtn.addEventListener('click', () => {
  sideMenu.style.display = 'none';
});