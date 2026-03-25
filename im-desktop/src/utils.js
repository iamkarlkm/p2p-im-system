// IM Desktop - Utilities Module
// 工具函数集合

/**
 * 生成 UUID
 */
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

/**
 * 生成随机字符串
 */
function generateRandomString(length = 16) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

/**
 * 格式化日期
 */
function formatDate(timestamp, format = 'YYYY-MM-DD HH:mm:ss') {
    if (!timestamp) return '';
    
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hours)
        .replace('mm', minutes)
        .replace('ss', seconds);
}

/**
 * 格式化时间（相对时间）
 */
function formatRelativeTime(timestamp) {
    if (!timestamp) return '';
    
    const now = Date.now();
    const diff = now - timestamp;
    
    const second = 1000;
    const minute = second * 60;
    const hour = minute * 60;
    const day = hour * 24;
    const week = day * 7;
    const month = day * 30;
    const year = day * 365;
    
    if (diff < second) {
        return '刚刚';
    } else if (diff < minute) {
        return Math.floor(diff / second) + '秒前';
    } else if (diff < hour) {
        return Math.floor(diff / minute) + '分钟前';
    } else if (diff < day) {
        return Math.floor(diff / hour) + '小时前';
    } else if (diff < week) {
        return Math.floor(diff / day) + '天前';
    } else if (diff < month) {
        return Math.floor(diff / week) + '周前';
    } else if (diff < year) {
        return Math.floor(diff / month) + '个月前';
    } else {
        return formatDate(timestamp, 'YYYY-MM-DD');
    }
}

/**
 * 格式化文件大小
 */
function formatFileSize(bytes) {
    if (!bytes || bytes === 0) return '0 B';
    
    const units = ['B', 'KB', 'MB', 'GB', 'TB'];
    const k = 1024;
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + units[i];
}

/**
 * 格式化时长
 */
function formatDuration(seconds) {
    if (!seconds || seconds < 0) return '0:00';
    
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = Math.floor(seconds % 60);
    
    if (hours > 0) {
        return `${hours}:${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
    } else {
        return `${minutes}:${String(secs).padStart(2, '0')}`;
    }
}

/**
 * 获取文件名扩展名
 */
function getFileExtension(filename) {
    if (!filename) return '';
    const parts = filename.split('.');
    return parts.length > 1 ? parts[parts.length - 1].toLowerCase() : '';
}

/**
 * 获取文件类型
 */
function getFileType(filename) {
    const ext = getFileExtension(filename);
    
    const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg'];
    const videoExts = ['mp4', 'avi', 'mov', 'wmv', 'flv', 'mkv', 'webm'];
    const audioExts = ['mp3', 'wav', 'ogg', 'flac', 'aac', 'm4a'];
    const documentExts = ['doc', 'docx', 'pdf', 'txt', 'xls', 'xlsx', 'ppt', 'pptx'];
    const archiveExts = ['zip', 'rar', '7z', 'tar', 'gz'];
    
    if (imageExts.includes(ext)) return 'image';
    if (videoExts.includes(ext)) return 'video';
    if (audioExts.includes(ext)) return 'audio';
    if (documentExts.includes(ext)) return 'document';
    if (archiveExts.includes(ext)) return 'archive';
    
    return 'file';
}

/**
 * 验证邮箱
 */
function isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

/**
 * 验证手机号
 */
function isValidPhone(phone) {
    const regex = /^1[3-9]\d{9}$/;
    return regex.test(phone);
}

/**
 * 验证用户名
 */
function isValidUsername(username) {
    const regex = /^[a-zA-Z0-9_]{3,20}$/;
    return regex.test(username);
}

/**
 * 验证密码强度
 */
function checkPasswordStrength(password) {
    if (!password) return { level: 0, message: '' };
    
    let level = 0;
    let message = '';
    
    if (password.length >= 8) level++;
    if (password.length >= 12) level++;
    if (/[a-z]/.test(password)) level++;
    if (/[A-Z]/.test(password)) level++;
    if (/[0-9]/.test(password)) level++;
    if (/[^a-zA-Z0-9]/.test(password)) level++;
    
    if (level <= 2) {
        message = '弱';
    } else if (level <= 4) {
        message = '中等';
    } else {
        message = '强';
    }
    
    return { level, message };
}

/**
 * HTML 转义
 */
function escapeHtml(text) {
    if (!text) return '';
    
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    
    return text.replace(/[&<>"']/g, char => map[char]);
}

/**
 * HTML 解码
 */
function unescapeHtml(text) {
    if (!text) return '';
    
    const map = {
        '&amp;': '&',
        '&lt;': '<',
        '&gt;': '>',
        '&quot;': '"',
        '&#039;': "'"
    };
    
    return text.replace(/&(amp|lt|gt|quot|#039);/g, entity => map[entity]);
}

/**
 * 移除 HTML 标签
 */
function stripHtml(html) {
    if (!html) return '';
    return html.replace(/<[^>]*>/g, '');
}

/**
 * 字符串截断
 */
function truncate(str, maxLength, ellipsis = '...') {
    if (!str || str.length <= maxLength) return str;
    return str.substring(0, maxLength - ellipsis.length) + ellipsis;
}

/**
 * 首字母大写
 */
function capitalize(str) {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

/**
 * 驼峰转下划线
 */
function camelToSnake(str) {
    if (!str) return '';
    return str.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`);
}

/**
 * 下划线转驼峰
 */
function snakeToCamel(str) {
    if (!str) return '';
    return str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
}

/**
 * 深拷贝
 */
function deepClone(obj) {
    if (obj === null || typeof obj !== 'object') return obj;
    if (obj instanceof Date) return new Date(obj.getTime());
    if (obj instanceof Array) return obj.map(item => deepClone(item));
    if (obj instanceof Object) {
        const clonedObj = {};
        for (const key in obj) {
            if (obj.hasOwnProperty(key)) {
                clonedObj[key] = deepClone(obj[key]);
            }
        }
        return clonedObj;
    }
}

/**
 * 防抖
 */
function debounce(func, wait = 300) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * 节流
 */
function throttle(func, limit = 300) {
    let inThrottle;
    return function executedFunction(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

/**
 * 数组去重
 */
function unique(arr, key) {
    if (!arr || !Array.isArray(arr)) return [];
    
    if (key) {
        const seen = new Set();
        return arr.filter(item => {
            const value = item[key];
            if (seen.has(value)) return false;
            seen.add(value);
            return true;
        });
    }
    
    return [...new Set(arr)];
}

/**
 * 数组分组
 */
function groupBy(arr, key) {
    if (!arr || !Array.isArray(arr)) return {};
    
    return arr.reduce((result, item) => {
        const groupKey = typeof key === 'function' ? key(item) : item[key];
        if (!result[groupKey]) {
            result[groupKey] = [];
        }
        result[groupKey].push(item);
        return result;
    }, {});
}

/**
 * 数组排序
 */
function sortBy(arr, key, order = 'asc') {
    if (!arr || !Array.isArray(arr)) return [];
    
    return arr.sort((a, b) => {
        const aVal = typeof key === 'function' ? key(a) : a[key];
        const bVal = typeof key === 'function' ? key(b) : b[key];
        
        if (aVal < bVal) return order === 'asc' ? -1 : 1;
        if (aVal > bVal) return order === 'asc' ? 1 : -1;
        return 0;
    });
}

/**
 * 数组差集
 */
function difference(arr1, arr2) {
    if (!arr1 || !arr2) return arr1 || [];
    return arr1.filter(item => !arr2.includes(item));
}

/**
 * 数组交集
 */
function intersection(arr1, arr2) {
    if (!arr1 || !arr2) return [];
    return arr1.filter(item => arr2.includes(item));
}

/**
 * 数组并集
 */
function union(arr1, arr2) {
    if (!arr1) return arr2 || [];
    if (!arr2) return arr1;
    return [...new Set([...arr1, ...arr2])];
}

/**
 * 获取字节长度
 */
function getByteLength(str) {
    if (!str) return 0;
    let len = 0;
    for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        if (char > 0 && char < 128) {
            len++;
        } else {
            len += 2;
        }
    }
    return len;
}

/**
 * 字符串截断（按字节）
 */
function truncateByByte(str, maxBytes, ellipsis = '...') {
    if (!str || getByteLength(str) <= maxBytes) return str;
    
    let len = 0;
    let result = '';
    
    for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        if (char > 0 && char < 128) {
            len++;
        } else {
            len += 2;
        }
        
        if (len > maxBytes - ellipsis.length) {
            break;
        }
        
        result += str[i];
    }
    
    return result + ellipsis;
}

/**
 * 本地存储
 */
const storage = {
    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
            return true;
        } catch (e) {
            console.error('Storage set error:', e);
            return false;
        }
    },
    
    get(key, defaultValue = null) {
        try {
            const value = localStorage.getItem(key);
            return value ? JSON.parse(value) : defaultValue;
        } catch (e) {
            console.error('Storage get error:', e);
            return defaultValue;
        }
    },
    
    remove(key) {
        try {
            localStorage.removeItem(key);
            return true;
        } catch (e) {
            console.error('Storage remove error:', e);
            return false;
        }
    },
    
    clear() {
        try {
            localStorage.clear();
            return true;
        } catch (e) {
            console.error('Storage clear error:', e);
            return false;
        }
    }
};

/**
 * Cookie 操作
 */
const cookie = {
    set(name, value, days = 7, path = '/') {
        const expires = new Date(Date.now() + days * 24 * 60 * 60 * 1000).toUTCString();
        document.cookie = `${name}=${encodeURIComponent(value)};expires=${expires};path=${path}`;
    },
    
    get(name) {
        const nameEQ = name + '=';
        const cookies = document.cookie.split(';');
        for (let c of cookies) {
            c = c.trim();
            if (c.indexOf(nameEQ) === 0) {
                return decodeURIComponent(c.substring(nameEQ.length));
            }
        }
        return null;
    },
    
    remove(name, path = '/') {
        document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=${path}`;
    }
};

/**
 * 获取 URL 参数
 */
function getUrlParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

/**
 * 获取 URL 所有参数
 */
function getUrlParams() {
    const params = {};
    const urlParams = new URLSearchParams(window.location.search);
    for (const [key, value] of urlParams) {
        params[key] = value;
    }
    return params;
}

/**
 * 设置 URL 参数
 */
function setUrlParam(key, value) {
    const url = new URL(window.location.href);
    url.searchParams.set(key, value);
    window.history.pushState({}, '', url);
}

/**
 * 移除 URL 参数
 */
function removeUrlParam(key) {
    const url = new URL(window.location.href);
    url.searchParams.delete(key);
    window.history.pushState({}, '', url);
}

/**
 * 复制到剪贴板
 */
async function copyToClipboard(text) {
    try {
        if (navigator.clipboard) {
            await navigator.clipboard.writeText(text);
            return true;
        } else {
            // 兼容旧浏览器
            const textarea = document.createElement('textarea');
            textarea.value = text;
            textarea.style.position = 'fixed';
            textarea.style.opacity = '0';
            document.body.appendChild(textarea);
            textarea.select();
            const success = document.execCommand('copy');
            document.body.removeChild(textarea);
            return success;
        }
    } catch (e) {
        console.error('Copy to clipboard error:', e);
        return false;
    }
}

/**
 * 下载文件
 */
function downloadFile(url, filename) {
    const link = document.createElement('a');
    link.href = url;
    link.download = filename || '';
    link.click();
}

/**
 * 下载文本
 */
function downloadText(text, filename, mimeType = 'text/plain') {
    const blob = new Blob([text], { type: mimeType });
    const url = URL.createObjectURL(blob);
    downloadFile(url, filename);
    URL.revokeObjectURL(url);
}

/**
 * 下载 JSON
 */
function downloadJson(data, filename) {
    const json = JSON.stringify(data, null, 2);
    downloadText(json, filename, 'application/json');
}

/**
 * 加载脚本
 */
function loadScript(src, callback) {
    const script = document.createElement('script');
    script.src = src;
    script.onload = callback;
    script.onerror = () => console.error(`Failed to load script: ${src}`);
    document.head.appendChild(script);
}

/**
 * 加载样式
 */
function loadStylesheet(href, callback) {
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = href;
    link.onload = callback;
    link.onerror = () => console.error(`Failed to load stylesheet: ${href}`);
    document.head.appendChild(link);
}

/**
 * 滚动到顶部
 */
function scrollToTop(smooth = true) {
    if (smooth) {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
        window.scrollTo(0, 0);
    }
}

/**
 * 滚动到底部
 */
function scrollToBottom(element, smooth = true) {
    if (!element) return;
    if (smooth) {
        element.scrollTo({ top: element.scrollHeight, behavior: 'smooth' });
    } else {
        element.scrollTop = element.scrollHeight;
    }
}

/**
 * 获取元素在视图中的位置
 */
function getElementPosition(element) {
    const rect = element.getBoundingClientRect();
    return {
        top: rect.top + window.scrollY,
        left: rect.left + window.scrollX,
        bottom: rect.bottom + window.scrollY,
        right: rect.right + window.scrollX
    };
}

/**
 * 元素是否在视图中
 */
function isElementInViewport(element) {
    const rect = element.getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
}

/**
 * 淡入
 */
function fadeIn(element, duration = 300) {
    element.style.display = '';
    element.style.opacity = '0';
    
    let start = null;
    function animate(timestamp) {
        if (!start) start = timestamp;
        const progress = timestamp - start;
        const opacity = Math.min(progress / duration, 1);
        
        element.style.opacity = opacity;
        
        if (progress < duration) {
            requestAnimationFrame(animate);
        }
    }
    
    requestAnimationFrame(animate);
}

/**
 * 淡出
 */
function fadeOut(element, duration = 300) {
    element.style.opacity = '1';
    
    let start = null;
    function animate(timestamp) {
        if (!start) start = timestamp;
        const progress = timestamp - start;
        const opacity = Math.max(1 - progress / duration, 0);
        
        element.style.opacity = opacity;
        
        if (progress < duration) {
            requestAnimationFrame(animate);
        } else {
            element.style.display = 'none';
        }
    }
    
    requestAnimationFrame(animate);
}

/**
 * 显示加载动画
 */
function showLoading(element, options = {}) {
    const { size = 'medium', color = '#1890ff', text = '加载中...' } = options;
    
    const loader = document.createElement('div');
    loader.className = 'custom-loader';
    loader.innerHTML = `
        <div class="loader-spinner" style="border-color: ${color}"></div>
        ${text ? `<div class="loader-text">${text}</div>` : ''}
    `;
    
    element.appendChild(loader);
    return loader;
}

/**
 * 隐藏加载动画
 */
function hideLoading(loader) {
    if (loader && loader.parentNode) {
        loader.parentNode.removeChild(loader);
    }
}

/**
 * 显示确认对话框
 */
function showConfirm(title, message, onConfirm, onCancel) {
    const modal = document.createElement('div');
    modal.className = 'confirm-modal';
    modal.innerHTML = `
        <div class="confirm-overlay"></div>
        <div class="confirm-dialog">
            <div class="confirm-header">${title}</div>
            <div class="confirm-body">${message}</div>
            <div class="confirm-footer">
                <button class="confirm-cancel">取消</button>
                <button class="confirm-ok">确定</button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    const cancelBtn = modal.querySelector('.confirm-cancel');
    const okBtn = modal.querySelector('.confirm-ok');
    const overlay = modal.querySelector('.confirm-overlay');
    
    const close = () => {
        fadeOut(modal, 200);
        setTimeout(() => {
            if (modal.parentNode) {
                modal.parentNode.removeChild(modal);
            }
        }, 200);
    };
    
    cancelBtn.addEventListener('click', () => {
        close();
        if (onCancel) onCancel();
    });
    
    okBtn.addEventListener('click', () => {
        close();
        if (onConfirm) onConfirm();
    });
    
    overlay.addEventListener('click', close);
    
    fadeIn(modal, 200);
}

/**
 * 显示提示消息
 */
function showToast(message, type = 'info', duration = 3000) {
    // 移除已存在的 toast
    const existingToast = document.querySelector('.custom-toast');
    if (existingToast) {
        existingToast.remove();
    }
    
    const toast = document.createElement('div');
    toast.className = `custom-toast toast-${type}`;
    toast.textContent = message;
    
    document.body.appendChild(toast);
    
    // 显示
    setTimeout(() => {
        toast.classList.add('toast-show');
    }, 10);
    
    // 隐藏
    setTimeout(() => {
        toast.classList.remove('toast-show');
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }, duration);
}

/**
 * 获取文件图标
 */
function getFileIcon(filename) {
    const type = getFileType(filename);
    
    const icons = {
        image: '🖼️',
        video: '🎬',
        audio: '🎵',
        document: '📄',
        archive: '📦',
        file: '📁'
    };
    
    return icons[type] || icons.file;
}

/**
 * 获取消息类型图标
 */
function getMessageTypeIcon(msgType) {
    const icons = {
        1: '',       // 文本
        2: '🖼️',    // 图片
        3: '📎',    // 文件
        4: '🎤',    // 语音
        5: '📹',    // 视频
        6: '📍'     // 位置
    };
    
    return icons[msgType] || '';
}

/**
 * 解析表情文字
 */
function parseEmoji(text) {
    // 简单的 emoji 映射
    const emojiMap = {
        ':)': '😊',
        ':-)': '😊',
        ':(': '😢',
        ':-(': '😢',
        ':D': '😄',
        ':-D': '😄',
        ';)': '😉',
        ';-)': '😉',
        ':P': '😜',
        ':-P': '😜',
        '<3': '❤️',
        ':*': '😘',
        ':-*': '😘',
        ':O': '😮',
        ':-O': '😮',
        ':|': '😐',
        ':-|': '😐',
        ':/': '😕',
        ':-/': '😕'
    };
    
    let result = text;
    for (const [key, value] of Object.entries(emojiMap)) {
        result = result.replace(new RegExp(key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'g'), value);
    }
    
    return result;
}

// 导出模块
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        generateUUID,
        generateRandomString,
        formatDate,
        formatRelativeTime,
        formatFileSize,
        formatDuration,
        getFileExtension,
        getFileType,
        isValidEmail,
        isValidPhone,
        isValidUsername,
        checkPasswordStrength,
        escapeHtml,
        unescapeHtml,
        stripHtml,
        truncate,
        capitalize,
        camelToSnake,
        snakeToCamel,
        deepClone,
        debounce,
        throttle,
        unique,
        groupBy,
        sortBy,
        difference,
        intersection,
        union,
        getByteLength,
        truncateByByte,
        storage,
        cookie,
        getUrlParam,
        getUrlParams,
        setUrlParam,
        removeUrlParam,
        copyToClipboard,
        downloadFile,
        downloadText,
        downloadJson,
        loadScript,
        loadStylesheet,
        scrollToTop,
        scrollToBottom,
        getElementPosition,
        isElementInViewport,
        fadeIn,
        fadeOut,
        showLoading,
        hideLoading,
        showConfirm,
        showToast,
        getFileIcon,
        getMessageTypeIcon,
        parseEmoji
    };
}
