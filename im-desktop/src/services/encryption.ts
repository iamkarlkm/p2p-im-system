/**
 * 端到端加密服务
 * 提供消息加密、解密、密钥生成和交换功能
 */

// 加密算法配置
const ALGORITHM = 'AES-GCM';
const KEY_LENGTH = 256;
const IV_LENGTH = 12;
const TAG_LENGTH = 128;

// 存储密钥
let keyCache = new Map();

/**
 * 生成RSA密钥对
 */
export async function generateKeyPair() {
    const keyPair = await window.crypto.subtle.generateKey(
        {
            name: 'RSA-OAEP',
            modulusLength: 2048,
            publicExponent: new Uint8Array([1, 0, 1]),
            hash: 'SHA-256',
        },
        true,
        ['encrypt', 'decrypt']
    );
    return keyPair;
}

/**
 * 导出公钥为JWK格式
 */
export async function exportPublicKey(key) {
    const exported = await window.crypto.subtle.exportKey('jwk', key);
    return exported;
}

/**
 * 从JWK导入公钥
 */
export async function importPublicKey(jwk) {
    const key = await window.crypto.subtle.importKey(
        'jwk',
        jwk,
        {
            name: 'RSA-OAEP',
            hash: 'SHA-256',
        },
        true,
        ['encrypt']
    );
    return key;
}

/**
 * 导出私钥为JWK格式
 */
export async function exportPrivateKey(key) {
    const exported = await window.crypto.subtle.exportKey('jwk', key);
    return exported;
}

/**
 * 从JWK导入私钥
 */
export async function importPrivateKey(jwk) {
    const key = await window.crypto.subtle.importKey(
        'jwk',
        jwk,
        {
            name: 'RSA-OAEP',
            hash: 'SHA-256',
        },
        true,
        ['decrypt']
    );
    return key;
}

/**
 * 生成AES会话密钥
 */
export async function generateSessionKey() {
    const key = await window.crypto.subtle.generateKey(
        {
            name: 'AES-GCM',
            length: KEY_LENGTH,
        },
        true,
        ['encrypt', 'decrypt']
    );
    return key;
}

/**
 * 导出会话密钥
 */
export async function exportSessionKey(key) {
    const exported = await window.crypto.subtle.exportKey('raw', key);
    return arrayBufferToBase64(exported);
}

/**
 * 导入会话密钥
 */
export async function importSessionKey(base64Key) {
    const keyData = base64ToArrayBuffer(base64Key);
    const key = await window.crypto.subtle.importKey(
        'raw',
        keyData,
        {
            name: 'AES-GCM',
            length: KEY_LENGTH,
        },
        true,
        ['encrypt', 'decrypt']
    );
    return key;
}

/**
 * 使用RSA公钥加密会话密钥
 */
export async function encryptSessionKey(sessionKey, publicKey) {
    const keyData = sessionKey.export
        ? await window.crypto.subtle.exportKey('raw', sessionKey)
        : sessionKey;

    const encrypted = await window.crypto.subtle.encrypt(
        {
            name: 'RSA-OAEP',
        },
        publicKey,
        keyData
    );

    return arrayBufferToBase64(encrypted);
}

/**
 * 使用RSA私钥解密会话密钥
 */
export async function decryptSessionKey(encryptedKey, privateKey) {
    const keyData = base64ToArrayBuffer(encryptedKey);

    const decrypted = await window.crypto.subtle.decrypt(
        {
            name: 'RSA-OAEP',
        },
        privateKey,
        keyData
    );

    return await window.crypto.subtle.importKey(
        'raw',
        decrypted,
        {
            name: 'AES-GCM',
            length: KEY_LENGTH,
        },
        true,
        ['encrypt', 'decrypt']
    );
}

/**
 * 加密消息
 */
export async function encryptMessage(plainText, sessionKey) {
    // 生成随机IV
    const iv = window.crypto.getRandomValues(new Uint8Array(IV_LENGTH));

    // 编码消息
    const encoder = new TextEncoder();
    const messageData = encoder.encode(plainText);

    // 加密
    const cipherText = await window.crypto.subtle.encrypt(
        {
            name: ALGORITHM,
            iv: iv,
            tagLength: TAG_LENGTH,
        },
        sessionKey,
        messageData
    );

    return {
        cipherText: arrayBufferToBase64(cipherText),
        iv: arrayBufferToBase64(iv),
    };
}

/**
 * 解密消息
 */
export async function decryptMessage(encryptedMessage, sessionKey) {
    const cipherText = base64ToArrayBuffer(encryptedMessage.cipherText);
    const iv = base64ToArrayBuffer(encryptedMessage.iv);

    const decrypted = await window.crypto.subtle.decrypt(
        {
            name: ALGORITHM,
            iv: iv,
            tagLength: TAG_LENGTH,
        },
        sessionKey,
        cipherText
    );

    const decoder = new TextDecoder();
    return decoder.decode(decrypted);
}

/**
 * 存储会话密钥到缓存
 */
export function cacheSessionKey(chatId, sessionKey) {
    keyCache.set(`session_${chatId}`, sessionKey);
}

/**
 * 从缓存获取会话密钥
 */
export function getSessionKey(chatId) {
    return keyCache.get(`session_${chatId}`);
}

/**
 * 清除会话密钥
 */
export function clearSessionKey(chatId) {
    keyCache.delete(`session_${chatId}`);
}

/**
 * 存储密钥对
 */
export function cacheKeyPair(userId, keyPair) {
    keyCache.set(`keypair_${userId}`, keyPair);
}

/**
 * 获取密钥对
 */
export function getKeyPair(userId) {
    return keyCache.get(`keypair_${userId}`);
}

/**
 * 辅助函数：ArrayBuffer转Base64
 */
function arrayBufferToBase64(buffer) {
    const bytes = new Uint8Array(buffer);
    let binary = '';
    for (let i = 0; i < bytes.byteLength; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return btoa(binary);
}

/**
 * 辅助函数：Base64转ArrayBuffer
 */
function base64ToArrayBuffer(base64) {
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
        bytes[i] = binary.charCodeAt(i);
    }
    return bytes.buffer;
}

/**
 * 加密服务类
 */
export class EncryptionService {
    constructor() {
        this.keyPair = null;
        this.sessionKeys = new Map();
    }

    /**
     * 初始化密钥对
     */
    async initialize(userId) {
        // 从本地存储加载密钥对或生成新的
        const storedKeyPair = localStorage.getItem(`keypair_${userId}`);
        if (storedKeyPair) {
            const keyPairData = JSON.parse(storedKeyPair);
            this.keyPair = {
                publicKey: await importPublicKey(keyPairData.publicKey),
                privateKey: await importPrivateKey(keyPairData.privateKey),
            };
        } else {
            this.keyPair = await generateKeyPair();
            const exportedPublicKey = await exportPublicKey(this.keyPair.publicKey);
            const exportedPrivateKey = await exportPrivateKey(this.keyPair.privateKey);
            localStorage.setItem(`keypair_${userId}`, JSON.stringify({
                publicKey: exportedPublicKey,
                privateKey: exportedPrivateKey,
            }));
        }
    }

    /**
     * 获取公钥
     */
    async getPublicKey() {
        return this.keyPair.publicKey;
    }

    /**
     * 获取公钥JWK
     */
    async getPublicKeyJWK() {
        return await exportPublicKey(this.keyPair.publicKey);
    }

    /**
     * 获取公钥Base64
     */
    async getPublicKeyBase64() {
        const jwk = await this.getPublicKeyJWK();
        return btoa(JSON.stringify(jwk));
    }

    /**
     * 从Base64导入公钥
     */
    async importPublicKeyFromBase64(base64Key) {
        const jwk = JSON.parse(atob(base64Key));
        return await importPublicKey(jwk);
    }

    /**
     * 生成会话密钥
     */
    async createSessionKey() {
        return await generateSessionKey();
    }

    /**
     * 加密会话密钥
     */
    async encryptSessionKeyForRecipient(sessionKey, recipientPublicKey) {
        return await encryptSessionKey(sessionKey, recipientPublicKey);
    }

    /**
     * 解密会话密钥
     */
    async decryptSessionKeyFromSender(encryptedSessionKey) {
        return await decryptSessionKey(encryptedSessionKey, this.keyPair.privateKey);
    }

    /**
     * 加密消息
     */
    async encrypt(plainText, chatId) {
        let sessionKey = this.sessionKeys.get(chatId);
        if (!sessionKey) {
            sessionKey = await generateSessionKey();
            this.sessionKeys.set(chatId, sessionKey);
        }
        return await encryptMessage(plainText, sessionKey);
    }

    /**
     * 解密消息
     */
    async decrypt(encryptedMessage, chatId) {
        const sessionKey = this.sessionKeys.get(chatId);
        if (!sessionKey) {
            throw new Error('Session key not found for chat: ' + chatId);
        }
        return await decryptMessage(encryptedMessage, sessionKey);
    }

    /**
     * 设置会话密钥
     */
    setSessionKey(chatId, sessionKey) {
        this.sessionKeys.set(chatId, sessionKey);
    }

    /**
     * 获取会话密钥
     */
    getSessionKey(chatId) {
        return this.sessionKeys.get(chatId);
    }

    /**
     * 清除会话密钥
     */
    clearSessionKey(chatId) {
        this.sessionKeys.delete(chatId);
    }

    /**
     * 清除所有密钥
     */
    clearAllKeys(userId) {
        localStorage.removeItem(`keypair_${userId}`);
        this.sessionKeys.clear();
    }
}

// 导出单例
export default new EncryptionService();
