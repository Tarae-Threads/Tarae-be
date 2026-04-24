// 관리자 페이지 공통 유틸리티
// 페이지별 inline script 는 필드 구조가 다르므로 데이터 수집만 inline 에서 처리하고,
// 공통 동작 (모달 열기/닫기, 인라인 편집 토글 등) 은 여기서 제공한다.

// ── 폼 submit 확인 ──
window.confirmSubmit = function (message, formSelector) {
    if (!confirm(message)) return false;
    const form = document.querySelector(formSelector);
    if (form) form.submit();
    return true;
};

// ── 마스터 데이터 삭제 확인 (이벤트 위임) ──
document.addEventListener('click', function (e) {
    var btn = e.target.closest('.btn--delete[data-place-count]');
    if (!btn) return;
    var count = parseInt(btn.dataset.placeCount, 10) || 0;
    var message = count > 0
        ? count + '개 장소에서 사용 중입니다. 연결이 해제됩니다. 정말 삭제하시겠습니까?'
        : '정말 삭제하시겠습니까?';
    if (!confirm(message)) e.preventDefault();
});

// ── 사이드바 현재 페이지 하이라이트 ──
(function highlightActiveNav() {
    var url = window.location.pathname + window.location.search;
    document.querySelectorAll('.nav-item[data-match]').forEach(function (el) {
        var re = new RegExp(el.getAttribute('data-match'));
        if (re.test(url)) el.classList.add('active');
    });
})();

// ── 테이블 행 클라이언트 사이드 검색 ──
// 사용법: <input class="master-toolbar__search" data-filter="{rowClass}">
//       <tr class="{rowClass}" data-name="...">
(function attachRowFilters() {
    document.querySelectorAll('.master-toolbar__search').forEach(function (input) {
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

// ── 인라인 편집 폼 토글 ──
// 사용법: <button data-toggle-target="{prefix}">수정</button>
//       <span id="{prefix}-name">...</span>
//       <form id="{prefix}-edit" class="inline-edit-form">...</form>
window.toggleEdit = function (target) {
    var nameEl = document.getElementById(target + '-name');
    var formEl = document.getElementById(target + '-edit');
    if (!nameEl || !formEl) return;
    var opening = !formEl.classList.contains('open');
    nameEl.style.display = opening ? 'none' : '';
    formEl.classList.toggle('open', opening);
};

document.addEventListener('click', function (e) {
    var btn = e.target.closest('[data-toggle-target]');
    if (btn) {
        e.preventDefault();
        window.toggleEdit(btn.dataset.toggleTarget);
    }
});

// ── 프리뷰 모달 공통 ──
// 페이지별 form 은 buildPreviewCards(body) 를 구현해 body 에 카드를 채워넣는다.
window.AdminPreview = {
    open: function (formId, buildCards) {
        var form = document.getElementById(formId);
        if (!form.reportValidity()) return;
        var body = document.getElementById('preview-body');
        body.innerHTML = '';
        buildCards(body);
        document.getElementById('preview-modal').classList.add('open');
    },
    close: function () {
        document.getElementById('preview-modal').classList.remove('open');
    },
    submit: function (formId) {
        var form = document.getElementById(formId);
        form.removeAttribute('onsubmit');
        form.submit();
    },
    escapeHtml: function (s) {
        return (s || '').replace(/[&<>"']/g, function (c) {
            return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c];
        });
    }
};
