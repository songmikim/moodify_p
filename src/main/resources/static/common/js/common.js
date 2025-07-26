var commonLib = {
    /**
    * ajax 범용 기능
    * @param url : 요청 주소
    * @param successCallback : 성공시 처리 함수
    * @param failureCallback : 실패시 처리 함수
    * @param isText : false - json : text
    */
    ajaxLoad(url, successCallback, failureCallback, method = 'GET', body, header, isText = false) {
        url = commonLib.getUrl(url);

        const options = {
            method,
        }

        // body 데이터 처리, POST, PUT, PATH일때만 추가
        method = method.toUpperCase();
        if (typeof body === 'string') body = body.trim();

        if (['POST', 'PUT', 'PATH'].includes(method) && body) {
            options.body = body;
        }

        // 요청 헤더 처리
        header = header ?? {};

         // csrf 토큰 처리
        const csrfToken = document.querySelector("meta[name='csrf_token']").content;
        const csrfHeader = document.querySelector("meta[name='csrf_header']").content;
        header[csrfHeader] = csrfToken;
        options.headers = header;

        // ajax 요청 처리
        fetch(url, options)
            .then(res => isText ? res.text() : res.json())
            .then(data => {
                // 성공시 후속 처리
                if (typeof successCallback === 'function') {
                    successCallback(data);
                }
            })
            .catch(err => {
                // 실패시 후속 처리
                if (typeof failureCallback === 'function') {
                    failureCallback(err);
                }
            });
    },
    /**
    * ContextPath 기준 경로
    *
    */
    getUrl(url) {
        let baseUrl = document.querySelector("meta[name='base_url']").content;
        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/'));

        return url ? baseUrl + url : baseUrl;
    }
}

/* 에디터 공통 */
commonLib.loadEditor = function(el, height = 350) {
    if (!ClassicEditor || !el) {
        return Promise.resolve();
    }

    return new Promise((resolve, reject) => {
        (async () => {
            try {
                const editor = await ClassicEditor.create(el);
                resolve(editor);

                editor.editing.view.change((writer) => {
                    writer.setStyle(
                        "height", `${height}px`,
                        editor.editing.view.document.getRoot()
                    );
                });

            } catch (err) {
                console.error(err);
                reject(err);
            }
        })();
    });
};


/* window.alert를 SweetAlert2로 교체 */
window.alert = function(message, callback) {
    parent.Swal.fire({
      title: message,
      icon: "warning"
    }).then(() => {
        if (typeof callback === 'function') {
            callback();
        }
    })
};

window.addEventListener("DOMContentLoaded", function() {
    // 이미지 상세보기 처리 S
    const { fileManager } = commonLib;
    const showImages = document.getElementsByClassName("show-image");
    for (const el of showImages) {
        el.addEventListener("click", function() {
            const { seq } = this.dataset;
            fileManager.showImage(seq);
        });
    }
    // 이미지 상세보기 처리 E
});


/**
* 이메일 인증 메일 보내기
*
* @param email : 인증할 이메일
*/
commonLib.sendEmailVerify = function(email) {
    const { ajaxLoad } = commonLib;

    const url = `/api/email/verify?email=${email}`;

    ajaxLoad(url, (data) => {
        if (typeof callbackEmailVerify === 'function') {
            callbackEmailVerify(data);
        }

    }, (err) => console.error(err));
};

/**
* 인증 메일 코드 검증 처리
*
*/
commonLib.sendEmailVerifyCheck = function(authNum) {
    const { ajaxLoad } = commonLib;
    const url = `/api/email/auth_check?authNum=${authNum}`;
    ajaxLoad(url, (data) => {
        if (typeof callbackEmailVerifyCheck === 'function') {
            callbackEmailVerifyCheck(data);
        }
    }, (err) => console.error(err))
};