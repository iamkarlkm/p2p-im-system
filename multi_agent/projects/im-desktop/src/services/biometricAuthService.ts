import { apiClient } from '../utils/apiClient';
import { BiometricCredential, BiometricRegistrationOptions, AuthenticationResult, Fido2RegistrationOptions, Fido2AuthenticationOptions } from '../types/biometric';

export class BiometricAuthService {
    private static instance: BiometricAuthService;
    private readonly baseUrl = '/api/v1/biometric-auth';
    
    private constructor() {}
    
    public static getInstance(): BiometricAuthService {
        if (!BiometricAuthService.instance) {
            BiometricAuthService.instance = new BiometricAuthService();
        }
        return BiometricAuthService.instance;
    }
    
    // Platform detection
    public detectPlatform(): string {
        const userAgent = navigator.userAgent.toLowerCase();
        const platform = navigator.platform.toLowerCase();
        
        if (platform.includes('mac')) {
            return 'macos';
        } else if (platform.includes('win')) {
            return 'windows';
        } else if (platform.includes('linux')) {
            return 'linux';
        } else if (/iphone|ipad|ipod/.test(userAgent)) {
            return 'ios';
        } else if (/android/.test(userAgent)) {
            return 'android';
        }
        return 'unknown';
    }
    
    public detectBiometricCapability(): string[] {
        const capabilities: string[] = [];
        const platform = this.detectPlatform();
        
        switch (platform) {
            case 'macos':
                capabilities.push('TOUCH_ID', 'FACE_ID');
                break;
            case 'ios':
                capabilities.push('TOUCH_ID', 'FACE_ID');
                break;
            case 'windows':
                capabilities.push('WINDOWS_HELLO');
                break;
            case 'android':
                capabilities.push('FINGERPRINT', 'FACE');
                break;
        }
        
        // Check for WebAuthn/FIDO2 support
        if (typeof PublicKeyCredential !== 'undefined') {
            capabilities.push('FIDO2');
        }
        
        return capabilities;
    }
    
    // Biometric registration
    public async registerBiometric(options: BiometricRegistrationOptions): Promise<BiometricCredential> {
        try {
            const response = await apiClient.post(`${this.baseUrl}/register`, options);
            return response.data.data;
        } catch (error) {
            console.error('Failed to register biometric:', error);
            throw new Error(`Biometric registration failed: ${error.message}`);
        }
    }
    
    // FIDO2 registration
    public async registerFido2(options: Fido2RegistrationOptions): Promise<any> {
        try {
            // Create FIDO2 credential using WebAuthn API
            const credential = await navigator.credentials.create({
                publicKey: options.publicKey
            }) as PublicKeyCredential;
            
            // Send credential to server
            const registrationData = {
                credentialId: this.arrayBufferToBase64(credential.rawId),
                publicKey: this.arrayBufferToBase64(credential.response.getPublicKey()),
                attestationObject: this.arrayBufferToBase64((credential.response as any).attestationObject),
                clientDataJSON: this.arrayBufferToBase64(credential.response.clientDataJSON),
                transports: options.publicKey.extensions?.transports || []
            };
            
            const response = await apiClient.post(`${this.baseUrl}/fido2/register`, {
                ...options,
                ...registrationData
            });
            
            return response.data.data;
        } catch (error) {
            console.error('FIDO2 registration failed:', error);
            throw new Error(`FIDO2 registration failed: ${error.message}`);
        }
    }
    
    // Biometric authentication
    public async authenticate(options: any): Promise<AuthenticationResult> {
        try {
            const response = await apiClient.post(`${this.baseUrl}/authenticate`, options);
            return response.data.data;
        } catch (error) {
            console.error('Biometric authentication failed:', error);
            throw new Error(`Authentication failed: ${error.message}`);
        }
    }
    
    // FIDO2 authentication
    public async authenticateFido2(options: Fido2AuthenticationOptions): Promise<AuthenticationResult> {
        try {
            // Get credential from WebAuthn API
            const credential = await navigator.credentials.get({
                publicKey: options.publicKey
            }) as PublicKeyCredential;
            
            // Send authentication data to server
            const authenticationData = {
                credentialId: this.arrayBufferToBase64(credential.rawId),
                signature: this.arrayBufferToBase64((credential.response as any).signature),
                authenticatorData: this.arrayBufferToBase64((credential.response as any).authenticatorData),
                clientDataJSON: this.arrayBufferToBase64(credential.response.clientDataJSON),
                userHandle: options.userHandle
            };
            
            const response = await apiClient.post(`${this.baseUrl}/fido2/authenticate`, {
                ...options,
                ...authenticationData
            });
            
            return response.data.data;
        } catch (error) {
            console.error('FIDO2 authentication failed:', error);
            throw new Error(`FIDO2 authentication failed: ${error.message}`);
        }
    }
    
    // Management
    public async getUserBiometrics(userId: string): Promise<BiometricCredential[]> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/user/${userId}`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get user biometrics:', error);
            throw new Error(`Failed to get biometrics: ${error.message}`);
        }
    }
    
    public async getEnabledBiometrics(userId: string): Promise<BiometricCredential[]> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/user/${userId}/enabled`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get enabled biometrics:', error);
            throw new Error(`Failed to get enabled biometrics: ${error.message}`);
        }
    }
    
    public async enableBiometric(id: string, enable: boolean): Promise<boolean> {
        try {
            const response = await apiClient.put(`${this.baseUrl}/${id}/enable?enable=${enable}`);
            return response.data.success;
        } catch (error) {
            console.error('Failed to update biometric status:', error);
            throw new Error(`Failed to update biometric: ${error.message}`);
        }
    }
    
    public async deleteBiometric(id: string): Promise<boolean> {
        try {
            const response = await apiClient.delete(`${this.baseUrl}/${id}`);
            return response.data.success;
        } catch (error) {
            console.error('Failed to delete biometric:', error);
            throw new Error(`Failed to delete biometric: ${error.message}`);
        }
    }
    
    public async deleteAllUserBiometrics(userId: string): Promise<number> {
        try {
            const response = await apiClient.delete(`${this.baseUrl}/user/${userId}`);
            return response.data.data.count;
        } catch (error) {
            console.error('Failed to delete user biometrics:', error);
            throw new Error(`Failed to delete user biometrics: ${error.message}`);
        }
    }
    
    // Security
    public async getClonedBiometrics(userId: string): Promise<BiometricCredential[]> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/user/${userId}/cloned`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get cloned biometrics:', error);
            throw new Error(`Failed to get cloned biometrics: ${error.message}`);
        }
    }
    
    public async markAsCloned(id: string, cloned: boolean): Promise<boolean> {
        try {
            const response = await apiClient.put(`${this.baseUrl}/${id}/clone-warning?cloned=${cloned}`);
            return response.data.success;
        } catch (error) {
            console.error('Failed to mark biometric as cloned:', error);
            throw new Error(`Failed to update clone warning: ${error.message}`);
        }
    }
    
    public async disableAllUserBiometrics(userId: string): Promise<boolean> {
        try {
            const response = await apiClient.put(`${this.baseUrl}/user/${userId}/disable-all`);
            return response.data.success;
        } catch (error) {
            console.error('Failed to disable user biometrics:', error);
            throw new Error(`Failed to disable biometrics: ${error.message}`);
        }
    }
    
    // Statistics
    public async countUserBiometrics(userId: string): Promise<number> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/user/${userId}/count`);
            return response.data.data.count;
        } catch (error) {
            console.error('Failed to count biometrics:', error);
            throw new Error(`Failed to count biometrics: ${error.message}`);
        }
    }
    
    public async getUserBiometricTypes(userId: string): Promise<string[]> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/user/${userId}/types`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get biometric types:', error);
            throw new Error(`Failed to get biometric types: ${error.message}`);
        }
    }
    
    // FIDO2 specific
    public async getFido2Credential(credentialId: string): Promise<BiometricCredential> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/fido2/credential/${credentialId}`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get FIDO2 credential:', error);
            throw new Error(`Failed to get FIDO2 credential: ${error.message}`);
        }
    }
    
    public async getResidentKeyCredentials(userId: string): Promise<BiometricCredential[]> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/user/${userId}/fido2/resident-keys`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get resident key credentials:', error);
            throw new Error(`Failed to get resident key credentials: ${error.message}`);
        }
    }
    
    // Platform-specific biometric APIs
    public async usePlatformBiometric(action: 'authenticate' | 'register'): Promise<any> {
        const platform = this.detectPlatform();
        
        switch (platform) {
            case 'macos':
            case 'ios':
                return this.useAppleBiometric(action);
            case 'windows':
                return this.useWindowsHello(action);
            case 'android':
                return this.useAndroidBiometric(action);
            default:
                throw new Error(`Biometric not supported on platform: ${platform}`);
        }
    }
    
    private async useAppleBiometric(action: string): Promise<any> {
        // Apple biometric API would go here
        // This is a placeholder for actual implementation
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (action === 'authenticate') {
                    resolve({ success: true, userId: 'test-user' });
                } else {
                    resolve({ success: true, credentialId: 'apple-credential-123' });
                }
            }, 1000);
        });
    }
    
    private async useWindowsHello(action: string): Promise<any> {
        // Windows Hello API would go here
        // This is a placeholder for actual implementation
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (action === 'authenticate') {
                    resolve({ success: true, userId: 'test-user' });
                } else {
                    resolve({ success: true, credentialId: 'windows-hello-credential-123' });
                }
            }, 1000);
        });
    }
    
    private async useAndroidBiometric(action: string): Promise<any> {
        // Android biometric API would go here
        // This is a placeholder for actual implementation
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (action === 'authenticate') {
                    resolve({ success: true, userId: 'test-user' });
                } else {
                    resolve({ success: true, credentialId: 'android-credential-123' });
                }
            }, 1000);
        });
    }
    
    // Utility methods
    private arrayBufferToBase64(buffer: ArrayBuffer): string {
        const bytes = new Uint8Array(buffer);
        let binary = '';
        for (let i = 0; i < bytes.byteLength; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return btoa(binary);
    }
    
    private base64ToArrayBuffer(base64: string): ArrayBuffer {
        const binary = atob(base64);
        const bytes = new Uint8Array(binary.length);
        for (let i = 0; i < binary.length; i++) {
            bytes[i] = binary.charCodeAt(i);
        }
        return bytes.buffer;
    }
    
    // Health check
    public async healthCheck(): Promise<any> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/health`);
            return response.data.data;
        } catch (error) {
            console.error('Biometric service health check failed:', error);
            throw new Error(`Service health check failed: ${error.message}`);
        }
    }
}

export default BiometricAuthService.getInstance();