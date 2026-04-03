/**
 * 公共 JavaScript 工具函数
 * 职责:全局拦截、独有工具函数
 * 注意:本文件在 api.js 之后加载
 */

// ==================== 动态加载 SweetAlert2 (兜底机制) ====================
if (typeof Swal === 'undefined') {
    var swalScript = document.createElement('script');
    swalScript.src = 'https://cdn.jsdelivr.net/npm/sweetalert2@11';
    swalScript.onload = function() {
        console.log('SweetAlert2 已加载');
    };
    document.head.appendChild(swalScript);
    
    var swalStyle = document.createElement('link');
    swalStyle.rel = 'stylesheet';
    swalStyle.href = 'https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css';
    document.head.appendChild(swalStyle);
}

// ==================== 全局 AJAX 错误处理 (401/403) ====================
$(document).ajaxError(function(event, jqxhr, settings, thrownError) {
    // 401 未登录处理
    if (jqxhr.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        
        if (!window.location.pathname.includes('login.html')) {
            if (typeof Swal !== 'undefined') {
                Swal.fire({
                    icon: 'warning',
                    title: '登录已过期',
                    text: '请重新登录',
                    confirmButtonText: '确定',
                    confirmButtonColor: '#667eea'
                }).then(function() {
                    window.location.href = '/login.html';
                });
            } else {
                window.location.href = '/login.html';
            }
        }
    }
    
    // 403 无权限处理
    if (jqxhr.status === 403) {
        if (typeof showError === 'function') {
            showError('无权限访问');
        } else {
            alert('无权限访问');
        }
    }
});

// ==================== 通用工具函数 (独有) ====================

// 获取 URL 参数
function getUrlParam(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURIComponent(r[2]);
    return null;
}

// 防抖函数
function debounce(func, wait) {
    var timeout;
    return function() {
        var context = this, args = arguments;
        clearTimeout(timeout);
        timeout = setTimeout(function() {
            func.apply(context, args);
        }, wait);
    };
}

// 节流函数
function throttle(func, limit) {
    var inThrottle;
    return function() {
        var context = this, args = arguments;
        if (!inThrottle) {
            func.apply(context, args);
            inThrottle = true;
            setTimeout(function() {
                inThrottle = false;
            }, limit);
        }
    };
}

// 数字格式化 (千分位)
function formatNumber(num) {
    if (num === null || num === undefined) return '0';
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}
