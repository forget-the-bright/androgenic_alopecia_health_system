/**
 * 公共 JavaScript 工具函数
 */

// API 基础路径
var API_BASE = '/api';

// 获取存储的 token（不带前缀）
function getToken() {
    var token = localStorage.getItem('token') || '';
    // 如果 token 有 Bearer 前缀，去掉它
    if (token.startsWith('Bearer ')) {
        return token.substring(7);
    }
    return token;
}

// 检查登录状态（页面加载时调用，带验证）
function checkLogin() {
    var token = getToken();
    if (!token) {
        // 未登录，跳转到登录页
        window.location.href = '/login.html';
        return false;
    }
    
    // 有 token，但不立即跳转，让页面正常加载
    // 如果接口请求返回 401，全局错误处理会跳转
    return true;
}

// 检查登录状态并验证 token（异步）
function checkLoginAsync(callback) {
    var token = getToken();
    if (!token) {
        window.location.href = '/login.html';
        return;
    }
    
    // 验证 token 有效性
    $.ajax({
        url: '/api/user/info',
        type: 'GET',
        headers: { 'satoken': token },
        success: function(res) {
            if (res.code === 200) {
                if (callback) callback(res.data);
            } else {
                localStorage.removeItem('token');
                localStorage.removeItem('userInfo');
                window.location.href = '/login.html';
            }
        },
        error: function() {
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
            window.location.href = '/login.html';
        }
    });
}

// 全局 AJAX 错误处理 - 处理 401 未登录情况
$(document).ajaxError(function(event, jqxhr, settings, thrownError) {
    if (jqxhr.status === 401) {
        // 未登录，清除本地存储并跳转到登录页
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        // 如果当前不在登录页，跳转
        if (!window.location.pathname.includes('login.html')) {
            window.location.href = '/login.html';
        }
    }
});

// 退出登录
function doLogout() {
    if (!confirm('确定要退出登录吗？')) {
        return;
    }

    // 先清除本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');

    // 再调用后端退出接口
    $.ajax({
        url: '/api/logout',
        type: 'POST',
        headers: { 'satoken': getToken() },
        success: function(res) {
            window.location.href = '/login.html';
        },
        error: function() {
            // 即使接口失败也已经清除了本地存储
            window.location.href = '/login.html';
        }
    });
}

// 格式化日期
function formatDate(dateStr) {
    if (!dateStr) return '-';
    // 如果是完整的日期时间格式，只取日期部分
    if (dateStr.length >= 10) {
        return dateStr.substring(0, 10);
    }
    return dateStr;
}

// 格式化日期时间（转换为北京时间 GMT+8）
function formatDateTime(dateStr) {
    if (!dateStr) return '-';
    try {
        // 如果日期字符串不包含时区信息，则直接返回
        if (dateStr.indexOf('T') > 0) {
            // 处理 ISO 格式 2026-04-02T10:30:00
            return dateStr.replace('T', ' ').substring(0, 16);
        }
        // 处理普通格式 2026-04-02 10:30:00
        if (dateStr.length >= 16) {
            return dateStr.substring(0, 16);
        }
        return dateStr;
    } catch (e) {
        return dateStr;
    }
}

// 转换为 datetime-local 格式（用于编辑）
function toDateTimeLocal(dateStr) {
    if (!dateStr) return '';
    try {
        // 将 2026-04-02 10:30:00 转换为 2026-04-02T10:30
        if (dateStr.indexOf(' ') > 0) {
            return dateStr.replace(' ', 'T').substring(0, 16);
        }
        // 如果已经是 ISO 格式
        if (dateStr.indexOf('T') > 0) {
            return dateStr.substring(0, 16);
        }
        return dateStr;
    } catch (e) {
        return '';
    }
}

// 显示加载提示
function showLoading() {
    $('#loadingModal').modal('show');
}

// 隐藏加载提示
function hideLoading() {
    $('#loadingModal').modal('hide');
}

// 显示成功提示
function showToast(message, type) {
    type = type || 'success';
    var toast = '<div class="toast-container position-fixed top-0 end-0 p-3">' +
                '<div class="toast align-items-center text-white bg-' + type + ' border-0" role="alert">' +
                '<div class="d-flex">' +
                '<div class="toast-body">' + message + '</div>' +
                '<button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>' +
                '</div></div></div>';
    $('body').append(toast);
    var toastEl = $('.toast-container .toast').last();
    var bsToast = new bootstrap.Toast(toastEl);
    bsToast.show();
    toastEl.on('hidden.bs.toast', function() {
        toastEl.remove();
    });
}

// AJAX 请求封装
function request(options) {
    var defaultOptions = {
        headers: {
            'satoken': getToken()
        },
        contentType: 'application/json',
        success: function(res) {
            if (res.code === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('userInfo');
                window.location.href = '/login.html';
                return;
            }
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('userInfo');
                window.location.href = '/login.html';
            }
        }
    };
    
    return $.ajax($.extend(defaultOptions, options));
}

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

// 数字格式化
function formatNumber(num) {
    if (num === null || num === undefined) return '0';
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// 文件大小格式化
function formatFileSize(bytes) {
    if (bytes === 0) return '0 B';
    var k = 1024;
    var sizes = ['B', 'KB', 'MB', 'GB'];
    var i = Math.floor(Math.log(bytes) / Math.log(k));
    return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
}

// 本地存储封装
var storage = {
    get: function(key) {
        var value = localStorage.getItem(key);
        if (value) {
            try {
                return JSON.parse(value);
            } catch (e) {
                return value;
            }
        }
        return null;
    },
    set: function(key, value) {
        if (typeof value === 'object') {
            localStorage.setItem(key, JSON.stringify(value));
        } else {
            localStorage.setItem(key, value);
        }
    },
    remove: function(key) {
        localStorage.removeItem(key);
    },
    clear: function() {
        localStorage.clear();
    }
};
