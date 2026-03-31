/**
 * 全局工具函数 - 主题、侧边栏、多语言
 */

// ==================== 主题切换 ====================

// 初始化主题
function initTheme() {
    var theme = localStorage.getItem('theme') || 'light';
    setTheme(theme);
}

// 设置主题
function setTheme(theme) {
    if (theme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
        localStorage.setItem('theme', 'dark');
    } else {
        document.documentElement.removeAttribute('data-theme');
        localStorage.setItem('theme', 'light');
    }
    updateThemeIcon();
}

// 切换主题
function toggleTheme() {
    var currentTheme = document.documentElement.getAttribute('data-theme');
    var newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    setTheme(newTheme);
}

// 更新主题图标
function updateThemeIcon() {
    var theme = document.documentElement.getAttribute('data-theme');
    var icon = document.querySelector('.theme-toggle .theme-icon');
    if (icon) {
        icon.textContent = theme === 'dark' ? '☀️' : '🌙';
    }
}

// ==================== 侧边栏折叠 ====================

// 初始化侧边栏
function initSidebar() {
    var collapsed = localStorage.getItem('sidebarCollapsed') === 'true';
    if (collapsed) {
        collapseSidebar();
    }
}

// 切换侧边栏
function toggleSidebar() {
    var sidebar = document.querySelector('.sidebar');
    var mainContent = document.querySelector('.main-content');
    
    if (sidebar) {
        sidebar.classList.toggle('collapsed');
        var isCollapsed = sidebar.classList.contains('collapsed');
        localStorage.setItem('sidebarCollapsed', isCollapsed);
        updateSidebarToggleIcon();
        
        // 触发布局重绘，确保过渡平滑
        if (mainContent) {
            mainContent.style.transition = 'max-width 0.3s ease';
        }
    }
}

// 折叠侧边栏
function collapseSidebar() {
    var sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.classList.add('collapsed');
        localStorage.setItem('sidebarCollapsed', 'true');
        updateSidebarToggleIcon();
    }
}

// 展开侧边栏
function expandSidebar() {
    var sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.classList.remove('collapsed');
        localStorage.setItem('sidebarCollapsed', 'false');
        updateSidebarToggleIcon();
    }
}

// 更新侧边栏切换按钮图标
function updateSidebarToggleIcon() {
    var sidebar = document.querySelector('.sidebar');
    var icon = document.querySelector('.sidebar-toggle .toggle-icon');
    if (icon) {
        if (sidebar && sidebar.classList.contains('collapsed')) {
            icon.textContent = '➡️';
        } else {
            icon.textContent = '⬅️';
        }
    }
}

// 移动端显示侧边栏
function showSidebarMobile() {
    var sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.classList.add('show');
    }
}

// 移动端隐藏侧边栏
function hideSidebarMobile() {
    var sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.classList.remove('show');
    }
}

// ==================== 多语言切换 ====================

// 语言配置
var i18nConfig = {
    'zh-CN': {
        name: '中文',
        flag: '🇨'
    },
    'en-US': {
        name: 'English',
        flag: '🇺🇸'
    }
};

// 初始化语言
function initLanguage() {
    var lang = localStorage.getItem('language') || 'zh-CN';
    setLanguage(lang);
}

// 设置语言
function setLanguage(lang) {
    localStorage.setItem('language', lang);
    document.documentElement.lang = lang;
    updateLanguageSelector();
    // 可以在这里添加更多语言切换逻辑
}

// 切换语言
function toggleLanguage() {
    var currentLang = localStorage.getItem('language') || 'zh-CN';
    var newLang = currentLang === 'zh-CN' ? 'en-US' : 'zh-CN';
    setLanguage(newLang);
    // 刷新页面应用新语言
    window.location.reload();
}

// 更新语言选择器
function updateLanguageSelector() {
    var lang = localStorage.getItem('language') || 'zh-CN';
    var selector = document.querySelector('.language-selector select');
    if (selector) {
        selector.value = lang;
    }
}

// ==================== 页面加载时初始化 ====================

$(document).ready(function() {
    initTheme();
    initSidebar();
    initLanguage();
    
    // 点击页面其他地方关闭移动端侧边栏
    $(document).click(function(e) {
        var sidebar = document.querySelector('.sidebar');
        var toggle = document.querySelector('.mobile-sidebar-toggle');
        
        if (window.innerWidth <= 768 && sidebar && sidebar.classList.contains('show')) {
            if (!$(e.target).closest('.sidebar, .mobile-sidebar-toggle').length) {
                hideSidebarMobile();
            }
        }
    });
});

// ==================== 导航栏模板 ====================

// 生成导航栏 HTML
function generateNavbar(activePage) {
    return `
        <nav class="navbar navbar-expand-lg">
            <div class="container-fluid">
                <div class="d-flex align-items-center">
                    <button class="sidebar-toggle me-2" onclick="toggleSidebar()" title="切换侧边栏">
                        <span class="toggle-icon">⬅️</span>
                    </button>
                    <a class="navbar-brand" href="/index.html">毛发健康管理系统</a>
                </div>
                
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>
                
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link ${activePage === 'home' ? 'active' : ''}" href="/index.html">📊 首页</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${activePage === 'hair' ? 'active' : ''}" href="/hair-data.html">📷 毛发数据</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${activePage === 'ai' ? 'active' : ''}" href="/ai-analysis.html">🤖 AI 分析</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${activePage === 'medicine' ? 'active' : ''}" href="/medicine.html">💊 用药管理</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${activePage === 'profile' ? 'active' : ''}" href="/profile.html">👤 个人中心</a>
                        </li>
                        <li class="nav-item admin-link" style="display: none;">
                            <a class="nav-link" href="/admin.html">⚙️ 管理后台</a>
                        </li>
                        <li class="nav-item">
                            <button class="theme-toggle ms-2" onclick="toggleTheme()" title="切换主题">
                                <span class="theme-icon">🌙</span>
                            </button>
                        </li>
                        <li class="nav-item">
                            <div class="language-selector ms-2">
                                <select onchange="setLanguage(this.value)">
                                    <option value="zh-CN">🇨🇳 中文</option>
                                    <option value="en-US">🇺🇸 English</option>
                                </select>
                            </div>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#" onclick="doLogout()">🚪 退出</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    `;
}

// 生成侧边栏 HTML
function generateSidebar(activePage) {
    return `
        <div class="sidebar">
            <nav class="nav flex-column">
                <a class="nav-link ${activePage === 'dashboard' ? 'active' : ''}" href="/index.html">
                    <span class="icon">📊</span>
                    <span class="text">数据概览</span>
                </a>
                <a class="nav-link ${activePage === 'hair' ? 'active' : ''}" href="/hair-data.html">
                    <span class="icon">📷</span>
                    <span class="text">毛发数据</span>
                </a>
                <a class="nav-link ${activePage === 'ai' ? 'active' : ''}" href="/ai-analysis.html">
                    <span class="icon">🤖</span>
                    <span class="text">AI 分析</span>
                </a>
                <a class="nav-link ${activePage === 'medicine' ? 'active' : ''}" href="/medicine.html">
                    <span class="icon">💊</span>
                    <span class="text">用药管理</span>
                </a>
                <a class="nav-link ${activePage === 'profile' ? 'active' : ''}" href="/profile.html">
                    <span class="icon">👤</span>
                    <span class="text">个人中心</span>
                </a>
            </nav>
        </div>
    `;
}

// 检查并显示管理员链接
function checkAdminLink() {
    var userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    if (userInfo.role === 1) {
        $('.admin-link').show();
    }
}
