// 관리자 페이지 공통 유틸리티
// 행 추가/제거, 프리뷰 모달 동작은 페이지별 inline script 에서 호출한다.
// 페이지마다 form/필드 구조가 달라 추상화보다는 페이지 안에서 직접 다루는 편이 단순하다.

window.confirmSubmit = function (message, formSelector) {
    if (!confirm(message)) return false;
    const form = document.querySelector(formSelector);
    if (form) form.submit();
    return true;
};

// 사이드바 현재 페이지 하이라이트: data-match 정규식과 현재 경로를 매칭
(function highlightActiveNav() {
    var path = window.location.pathname;
    document.querySelectorAll('.nav-item[data-match]').forEach(function (el) {
        var re = new RegExp(el.getAttribute('data-match'));
        if (re.test(path)) el.classList.add('active');
    });
})();

// 테이블 행 클라이언트 사이드 검색: 입력값이 data-name 부분 매칭
// 사용법: <input class="master-search" data-filter="{rowClass}">
//       <tr class="{rowClass}" data-name="...">
(function attachRowFilters() {
    document.querySelectorAll('.master-search').forEach(function (input) {
        input.addEventListener('input', function () {
            var q = input.value.trim().toLowerCase();
            var rows = document.querySelectorAll('.' + input.dataset.filter);
            rows.forEach(function (row) {
                var name = (row.dataset.name || '').toLowerCase();
                row.style.display = (q === '' || name.indexOf(q) !== -1) ? '' : 'none';
            });
        });
    });
})();
