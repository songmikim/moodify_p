var commonLib = commonLib ?? {}

// 모달
commonLib.modal = {
    // 최초 로딩될 부분 정의, 쓰고 있는 페이지에 동적으로 적용
    init() {
        // 모달 배경 (창이 뜰때 배경을 어둡게)
        const modalBg = document.getElementById("modal-bg");
        if (modalBg) return;

        // 동적 추가
        const div = document.createElement("div");
        div.id = 'modal-bg';

        // 일단 모달창 안보이게 해두기 (나중에 지워야함)
        div.className = 'dn';

        document.body.append(div);

        // 클릭하면 안보이게
        div.addEventListener("click", () => this.close());

    },
    /**
    * 팝업 열기
    *
    */
    open(targetId, url, _width, _height, _title) {
        let modalContent = null;
        if (url) { // iframe을 추가한 modalContent 요소로 생성
            const el = document.querySelector(".modal-content.iframe");
            if (el) el.parentElement.removeChild(el);

            modalContent = document.createElement("div");
            modalContent.className = "modal-content iframe";
            document.body.append(modalContent);

            // 모달 오류 수정 부분
            url = url + (url.includes("?") ? "&" : "?");
            url = commonLib.getUrl(url + "popup=true");
            iframe = document.createElement("iframe");
            iframe.src = url;
            iframe.frameBorder = 0;
            iframe.width = _width - 40;
            iframe.height = _height - 80;
            iframe.scrolling = 'auto';

            modalContent.append(iframe);

        } else if(targetId) {
            modalContent = document.getElementById(targetId);
             // 모달 내용 영역의 너비 높이, 제목 처리
             let { width, height, title } = modalContent.dataset;
             _width = width;
             _height = height
             _title = title;
        }

        if (!modalContent) return;

        const modalBg = document.getElementById("modal-bg");

        modalBg.classList.remove("dn");
        modalContent.classList.remove("on");
        modalContent.classList.add("on");


        _width = !_width || _width < 1 ? 350 : _width;
        _height = !_height || _height < 1 ? 350 : _height;

        modalContent.style.width = `${_width}px`;
        modalContent.style.height = `${_height}px`;

//        const xpos = Math.round((innerWidth - _width) / 2);
        const ypos = Math.round((innerHeight - _height) / 2);

        modalContent.style.top = `${ypos}px`;
        modalContent.style.left = `50vw`;
        modalContent.style.transform = `translateX(-50%)`;

        // 팝업 상단 제목
        if (_title) {
            const titleDiv = document.createElement("div");
            titleDiv.className = "modal-title";
            titleDiv.innerHTML = _title;
            modalContent.prepend(titleDiv);
        }
    },

    /**
    * close 정의
    * 모달 배경 레이어 - #modal-bg
    * 모달 컨텐츠 레이어 - .modal-content
    */
    close() {
        const modalBg = document.getElementById("modal-bg");
        modalBg.classList.remove("dn");
        modalBg.classList.add("dn");

        const iframeContentEl = document.querySelector(".modal-content.iframe");
        if (iframeContentEl) iframeContentEl.parentElement.removeChild(iframeContentEl);

        const contentEls = document.querySelectorAll(".modal-content.on")
        contentEls.forEach(el => {
            el.classList.remove("on");
            el.style = "";

            const titleEl = el.querySelector(".modal-title");
            if (titleEl) {
                el.removeChild(titleEl);
            }
        });

    }
};

window.addEventListener("DOMContentLoaded", function() {
    const { modal } = commonLib;
    modal.init();

    const modalOpenEls = document.getElementsByClassName("modal-open");
    for (const el of modalOpenEls) {
        el.addEventListener("click", function() {
            const { targetId, url, width, height, title } = this.dataset;
            modal.open(targetId, url, width, height, title);
        });
    }
});


/**
iframe으로 로딩된 컨텐츠 높이에 따른 모달창 사이즈 조정
*/
function resizeModalHeight(height) {
    height = Math.ceil(height);
    const modalHeight = height + 150;
    const ypos = (innerHeight - modalHeight) / 2;

    const modalContent = document.querySelector(".modal-content.iframe");
    const iframeEl = modalContent.querySelector("iframe");
    iframeEl.height = height;

    modalContent.height

    modalContent.style.height = modalHeight + "px";
    modalContent.style.top = ypos + "px"

}