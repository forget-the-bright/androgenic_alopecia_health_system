/**
 * 全局 AJAX 请求封装
 * 统一处理：
 * - Token 自动携带
 * - 401 未登录自动跳转
 * - 错误统一提示
 * - 加载状态管理
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
function getUserInfo(){
    return JSON.parse(localStorage.getItem('userInfo'));
}

/**
 * 封装的 AJAX 请求方法
 * @param {Object} options - 请求配置
 * @param {string} options.url - 请求地址
 * @param {string} options.type - 请求方法 (GET/POST/PUT/DELETE)
 * @param {Object} options.data - 请求数据
 * @param {boolean} options.showLoading - 是否显示加载动画 (默认 true)
 * @param {boolean} options.showError - 是否显示错误提示 (默认 true)
 * @param {Function} options.success - 成功回调
 * @param {Function} options.error - 失败回调
 * @returns {Promise} jQuery AJAX Promise 对象
 */
function apiRequest(options) {
    var defaultOptions = {
        type: 'GET',
        contentType: 'application/json',
        showLoading: true,
        showError: true,
        headers: {
            'satoken': getToken()
        }
    };
    var settings = $.extend({}, defaultOptions, options);
    // 显示加载动画
    if (settings.showLoading) {
        showGlobalLoading();
    }

    return $.ajax(settings)
        .done(function(res) {
            // 统一处理 401 未登录
            if (res.code === 401) {
                handleUnauthorized();
                return;
            }
            
            // 统一处理 403 无权限
            if (res.code === 403) {
                if (settings.showError) {
                    showError('无权限访问');
                }
                return;
            }

            // 调用成功回调
          /*  if (settings.success) {
                settings.success(res);
            }*/
        })
        .fail(function(xhr) {
            // 处理 HTTP 错误状态码
            if (xhr.status === 401) {
                handleUnauthorized();
                return;
            }
            
            if (xhr.status === 403) {
                if (settings.showError) {
                    showError('无权限访问');
                }
                return;
            }

            // 处理其他错误
            if (settings.showError) {
                var errorMsg = '请求失败';
                if (xhr.responseJSON && xhr.responseJSON.msg) {
                    errorMsg = xhr.responseJSON.msg;
                } else if (xhr.statusText) {
                    errorMsg = xhr.statusText;
                }
                showError(errorMsg);
            }

            // 调用失败回调
          /*  if (settings.error) {
                settings.error(xhr);
            }*/
        })
        .always(function() {
            // 隐藏加载动画
            if (settings.showLoading) {
                hideGlobalLoading();
            }
        });
}

/**
 * 处理未登录情况
 */
function handleUnauthorized() {
    // 清除本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    
    // 显示提示并跳转
    showWarning('登录已过期，请重新登录', function() {
        window.location.href = '/login.html';
    });
}

/**
 * GET 请求快捷方法
 */
function apiGet(url, success, error) {
    return apiRequest({
        url: url,
        type: 'GET',
        success: success,
        error: error
    });
}

/**
 * POST 请求快捷方法
 */
function apiPost(url, data, success, error) {
    return apiRequest({
        url: url,
        type: 'POST',
        data: JSON.stringify(data),
        success: success,
        error: error
    });
}

/**
 * PUT 请求快捷方法
 */
function apiPut(url, data, success, error) {
    return apiRequest({
        url: url,
        type: 'PUT',
        data: JSON.stringify(data),
        success: success,
        error: error
    });
}

/**
 * DELETE 请求快捷方法
 */
function apiDelete(url, success, error) {
    return apiRequest({
        url: url,
        type: 'DELETE',
        success: success,
        error: error
    });
}

// ==================== 美观弹窗封装 (SweetAlert2) ====================

/**
 * 显示成功提示
 */
function showSuccess(message, callback) {
    Swal.fire({
        icon: 'success',
        title: '成功',
        text: message,
        confirmButtonText: '确定',
        confirmButtonColor: '#667eea',
        timer: 2000,
        timerProgressBar: true
    }).then(function() {
        if (callback) callback();
    });
}

/**
 * 显示错误提示
 */
function showError(message, callback) {
    Swal.fire({
        icon: 'error',
        title: '错误',
        text: message,
        confirmButtonText: '确定',
        confirmButtonColor: '#dc3545'
    }).then(function() {
        if (callback) callback();
    });
}

/**
 * 显示警告提示
 */
function showWarning(message, callback) {
    Swal.fire({
        icon: 'warning',
        title: '警告',
        text: message,
        confirmButtonText: '确定',
        confirmButtonColor: '#ffc107'
    }).then(function() {
        if (callback) callback();
    });
}

/**
 * 显示信息提示
 */
function showInfo(message, callback) {
    Swal.fire({
        icon: 'info',
        title: '提示',
        text: message,
        confirmButtonText: '确定',
        confirmButtonColor: '#17a2b8'
    }).then(function() {
        if (callback) callback();
    });
}

/**
 * 显示确认对话框
 */
function showConfirm(message, title, onConfirm, onCancel) {
    Swal.fire({
        title: title || '确认操作',
        text: message,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#667eea',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
    }).then(function(result) {
        if (result.isConfirmed) {
            if (onConfirm) onConfirm();
        } else {
            if (onCancel) onCancel();
        }
    });
}

/**
 * 显示加载动画
 */
function showLoading(message) {
    Swal.fire({
        title: message || '加载中...',
        allowOutsideClick: false,
        allowEscapeKey: false,
        showConfirmButton: false,
        willOpen: function() {
            Swal.showLoading();
        }
    });
}

/**
 * 关闭加载动画
 */
function hideLoading() {
    Swal.close();
}

/**
 * 全局加载动画（用于 AJAX 请求）
 */
var globalLoadingCount = 0;

function showGlobalLoading() {
    globalLoadingCount++;
    if (globalLoadingCount === 1) {
        // 可以在这里显示全局加载动画
        // 暂时不显示，避免频繁闪烁
    }
}

function hideGlobalLoading() {
    globalLoadingCount--;
    if (globalLoadingCount <= 0) {
        globalLoadingCount = 0;
    }
}

// ==================== 兼容旧代码的 Toast 提示 ====================

/**
 * Toast 提示（兼容旧代码）
 */
function showToast(message, type) {
    type = type || 'success';
    var iconMap = {
        'success': 'success',
        'error': 'error',
        'warning': 'warning',
        'info': 'info',
        'danger': 'error'
    };
    
    Swal.fire({
        icon: iconMap[type] || 'info',
        title: type === 'success' ? '成功' : type === 'error' || type === 'danger' ? '错误' : '提示',
        text: message,
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true
    });
}

// ==================== 全局 AJAX 错误处理 ====================

$(document).ajaxError(function(event, jqxhr, settings, thrownError) {
    // 401 未登录处理
    if (jqxhr.status === 401) {
        handleUnauthorized();
        return;
    }
    
    // 403 无权限处理
    if (jqxhr.status === 403) {
        showError('无权限访问');
        return;
    }
});

// ==================== 登录检查 ====================

/**
 * 检查登录状态（页面加载时调用，带验证）
 */
function checkLogin() {
    var token = getToken();
    if (!token) {
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

/**
 * 检查登录状态并验证 token（异步）
 */
function checkLoginAsync(callback) {
    var token = getToken();
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    // 验证 token 有效性
    apiGet('/api/user/checkLogin', function(res) {
        if (res.code === 200) {
            if (callback) callback(getUserInfo());
        } else {
            handleUnauthorized();
        }
    }, function() {
        handleUnauthorized();
    });
}

// ==================== 退出登录 ====================

function doLogout() {
    showConfirm('确定要退出登录吗？', '退出确认', function() {
        // 先清除本地存储
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');

        // 再调用后端退出接口
        apiPost('/api/logout', {}, function() {
            window.location.href = '/login.html';
        }, function() {
            // 即使接口失败也已经清除了本地存储
            window.location.href = '/login.html';
        });
    });
}

// ==================== 工具函数 ====================

/**
 * 格式化日期
 */
function formatDate(dateStr) {
    if (!dateStr) return '-';
    if (dateStr.length >= 10) {
        return dateStr.substring(0, 10);
    }
    return dateStr;
}

/**
 * 格式化日期时间 (完善版)
 */
function formatDateTime(dateStr) {
    if (!dateStr) return '-';
    try {
        if (dateStr.indexOf('T') > 0) {
            return dateStr.replace('T', ' ').substring(0, 16);
        }
        if (dateStr.length >= 16) {
            return dateStr.substring(0, 16);
        }
        return dateStr;
    } catch (e) {
        return dateStr;
    }
}

/**
 * 转换为 datetime-local 格式 (用于编辑表单)
 */
function toDateTimeLocal(dateStr) {
    if (!dateStr) return '';
    try {
        if (dateStr.indexOf(' ') > 0) {
            return dateStr.replace(' ', 'T').substring(0, 16);
        }
        if (dateStr.indexOf('T') > 0) {
            return dateStr.substring(0, 16);
        }
        return dateStr;
    } catch (e) {
        return '';
    }
}

/**
 * 格式化文件大小
 */
function formatFileSize(bytes) {
    if (bytes === 0) return '0 B';
    var k = 1024;
    var sizes = ['B', 'KB', 'MB', 'GB'];
    var i = Math.floor(Math.log(bytes) / Math.log(k));
    return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
}

/**
 * 本地存储封装
 */
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
