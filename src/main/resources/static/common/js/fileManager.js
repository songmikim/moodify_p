var commonLib = commonLib ?? {}


window.addEventListener("DOMContentLoaded", function() {
    const fileUploadButtons = document.getElementsByClassName("file-upload-btn");

    // (HTML)코드가 없더라도 <input type="file">인 (Document) 객체를 생성
    const fileEl = document.createElement("input");
    fileEl.type = 'file';

    for (const el of fileUploadButtons) {
        el.addEventListener("click", function() { // 파일 업로드 버튼을 클릭한 경우
            const { gid, location, single, imageOnly } = this.dataset;

            fileEl.gid = gid;
            fileEl.location = location;
            fileEl.multiple = single !== 'true';
            fileEl.imageOnly = imageOnly === 'true';
            fileEl.single = single === 'true';

            fileEl.click(); // 파일 선택창 클릭

        });
    }



    /* 파일 선택시 처리 S */
    fileEl.addEventListener("change", function() {
        const files = this.files;
        const { gid, location, single, imageOnly } = fileEl; // fileEl에 있는 프로퍼티를 다시 꺼내서 할당

        const { fileManager } = commonLib; // common.js에서 정의한 commonLib을

        fileManager.upload(files, gid, location, imageOnly, single);
        fileManager.callback = () => {
            fileEl.value = ""; // 파일 업로드 완료 후 초기화
        }

    });
    /* 파일 선택시 처리 E */

    /* 드래그앤 드롭 파일 업로드 처리 S */
    const dragDropEls = document.getElementsByClassName("drag-drop-upload");
    for (const el of dragDropEls) {
        el.addEventListener("dragover", function(e) {
            e.preventDefault();
        });

        el.addEventListener("drop", function(e) {
            e.preventDefault();

            const { gid, location, single, imageOnly } = this.dataset;
            let files = e.dataTransfer.files;
            if (single === 'true') { // 한개의 파일만 업로드
                files = [files[0]];
            }

            const { fileManager } = commonLib;
            fileManager.upload(files, gid, location, imageOnly, single);
        });
    }
    /* 드래그앤 드롭 파일 업로드 처리 E */
});

commonLib.fileManager = {
    callback: null,
    /**
    * 파일 업로드
    *
    * files : 업로드한 파일 정보 / fielEl에 의해 저장 처리
    * gid : 그룹 ID
    * location : 그룹 내에서의 위치
    * imageOnly : 파일 형식을 이미지로만 되게함
    * single : 단일 파일
    */
    upload(files, gid, location, imageOnly, single) {
        try {
            // 파일 업로드 여부 체크
            if (!files || files.length === 0) {
                throw new Error("파일을 업로드 하세요.")
            }

            // 이미지 형식의 파일로 제한
            if (imageOnly) {
                for (const file of files) {
                    // 이미지가 아닌 파일이 포함된 경우
                    if (!file.type.startsWith("image/")) {
                        throw new Error("이미지 형식의 파일만 업로드 하세요.")
                    }
                }
            }

            // gid 필수 여부 체크
            if (!gid || !('' +gid).trim()) {
                throw new Error("gid 없어요");
            }

            // 파일 업로드 양식 동적 생성
            const formData = new FormData();
            for (const file of files) {
                formData.append("file", file);
            }

            formData.append("gid", gid);
            if (location && ('' + location).trim()) {
                formData.append("location", location);
            }

            formData.append("single", Boolean(single));
            formData.append("imageOnly", Boolean(imageOnly))

            // ajax로 파일 업로드 요청 처리
            const { ajaxLoad } = commonLib;
            ajaxLoad('/file/upload',(items) => {
               // 성공시 후속 처리
               if (typeof fileUploadCallback === 'function') {
                    fileUploadCallback(items); // 사용자 콜백함수 사용할때 만들어줘야함.
               }

               if (typeof commonLib.fileManager.callback === 'function') {
                    commonLib.fileManager.callback();
               }
            }, (e) => {
                // 실패시 후속 처리
                alert('파일 업로드 실패');
                console.error(e);
            }, 'POST', formData)

        } catch (e) {
            console.error(e);
            alert(e.message);
        }
    },

    // 파일 삭제
    delete(seq) {
        const { ajaxLoad } = commonLib; // delete 기능을 사용하기 위해 가져오기 (구조 분할 할당)

        ajaxLoad(`/file/delete/${seq}`,
        (item) => {
            if (typeof fileDeleteCallback === 'function') {
                fileDeleteCallback(item);
            }
        },
        (e) => {
            alert('파일삭제 실패하였습니다.');
            console.error(e);
        }, 'DELETE')
    },

    // 이미지 출력
    showImage(seq) {
        const url = commonLib.getUrl(`/file/image/${seq}`);
        open(url);
    }
};