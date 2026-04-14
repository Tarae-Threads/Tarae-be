// 관리자 페이지 공통 유틸리티
// 행 추가/제거, 프리뷰 모달 동작은 페이지별 inline script 에서 호출한다.
// 페이지마다 form/필드 구조가 달라 추상화보다는 페이지 안에서 직접 다루는 편이 단순하다.

window.confirmSubmit = function (message, formSelector) {
    if (!confirm(message)) return false;
    const form = document.querySelector(formSelector);
    if (form) form.submit();
    return true;
};
