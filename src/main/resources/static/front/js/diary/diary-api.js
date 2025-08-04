/* Sentiment의 done 상태를 변경하도록 서버에 요청 */
window.updateDoneStatus = function(isDone, gid) {
    window.commonLib.ajaxLoad(
        `/diary/sentiment/updateDone/${gid}`,
        () => console.log(`✅ done=${isDone} 전송 완료`),
        (err) => {
            console.error(`❌ done=${isDone} 상태 업데이트 실패:`, err);
        },
        'POST',
        JSON.stringify({ done: isDone }),
        { 'Content-Type': 'application/json' }
    );
}

/* sentiment 테이블 내 고아 객체 모두를 제거하도록 서버에 요청  */
window.deleteOrphanSentiments = function() {
    window.commonLib.ajaxLoad(
        `/diary/delete/sentiments`,
        () => console.log(`✅ 고아 객체 삭제 요청 완료`),
        (err) => {
            console.error(`❌ 삭제 실패:`, err);
        },
        'POST',
        null, null
    );
}