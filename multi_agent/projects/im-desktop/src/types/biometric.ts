export interface BiometricCredential {
    id: string;
    userId: string;
    deviceId: string;
    biometricType: BiometricType;
    publicKey: string;
    keyHandle: string;
    credentialId: string;
    deviceName: string;
    deviceOS: string;
    deviceBrowser: string;
    securityLevel: SecurityLevel;
    isEnabled: boolean;
    lastUsedAt: string;
    createdAt: string;
    backupEligible: boolean;
    backupState: boolean;
    cloneWarning: boolean;
    rpId: string;
    origin: string;
    transports: string;
    userVerificationRequired: boolean;
    residentKeyRequired: boolean;
    signCount: number;
    flags: number;
}

export type BiometricType = 
    | 'TOUCH_ID' 
    | 'FACE_ID' 
    | 'WINDOWS_HELLO' 
    | 'FIDO2' 
    | 'FINGERPRINT' 
    | 'IRIS' 
    | 'STRONG_BIOMETRIC' 
    | 'WEAK_BIOMETRIC';

export type SecurityLevel = 'BASIC' | 'STANDARD' | 'HIGH';

export interface BiometricRegistrationOptions {
    userId: string;
    deviceId: string;
    biometricType: BiometricType;
    publicKey: string;
    keyHandle: string;
    credentialId: string;
    deviceName: string;
    deviceOS: string;
    deviceBrowser: string;
    securityLevel: SecurityLevel;
    attestationStatement?: string;
    backupEligible?: boolean;
    backupState?: boolean;
    flags?: number;
    rpId: string;
    origin: string;
    transports: string;
    userVerificationRequired: boolean;
    residentKeyRequired: boolean;
    cloneWarning?: boolean;
}

export interface Fido2RegistrationOptions {
    userId: string;
    deviceId: string;
    publicKey: PublicKeyCredentialCreationOptions;
    deviceName: string;
    rpId: string;
    origin: string;
    attestationStatement?: string;
    backupEligible?: boolean;
    backupState?: boolean;
    flags?: number;
    transports?: string[];
    userVerificationRequired?: boolean;
    residentKeyRequired?: boolean;
}

export interface BiometricAuthenticationRequest {
    credentialId: string;
    signature: string;
    challenge: string;
    authenticatorData?: string;
    clientDataJSON?: string;
    userHandle?: string;
}

export interface Fido2AuthenticationOptions {
    credentialId: string;
    publicKey: PublicKeyCredentialRequestOptions;
    userHandle?: string;
}

export interface AuthenticationResult {
    success: boolean;
    userId?: string;
    errorMessage?: string;
    warning?: string;
}

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
    timestamp: string;
}

export interface CountResponse {
    count: number;
}

export interface DeleteCountResponse {
    count: number;
}

export interface HealthCheckResponse {
    status: string;
    totalCredentials: number;
}

export interface Fido2RegistrationResponse {
    credentialId: string;
    publicKey: string;
    transports: string;
}

export interface Fido2AuthenticationResponse {
    success: boolean;
    userId?: string;
    warning?: string;
}

// Platform-specific interfaces
export interface AppleBiometricOptions {
    localizedReason: string;
    fallbackTitle?: string;
    cancelTitle?: string;
    devicePasscodeFallback?: boolean;
}

export interface WindowsHelloOptions {
    prompt: string;
    userId?: string;
}

export interface AndroidBiometricOptions {
    title: string;
    subtitle?: string;
    description?: string;
    negativeButtonText?: string;
    confirmationRequired?: boolean;
    deviceCredentialAllowed?: boolean;
}

// WebAuthn interfaces
export interface WebAuthnCredential {
    id: string;
    type: string;
    transports?: AuthenticatorTransport[];
    response: {
        clientDataJSON: string;
        attestationObject?: string;
        authenticatorData?: string;
        signature?: string;
        userHandle?: string;
    };
}

export interface WebAuthnRegistrationRequest {
    challenge: string;
    rp: {
        name: string;
        id: string;
    };
    user: {
        id: string;
        name: string;
        displayName: string;
    };
    pubKeyCredParams: Array<{
        type: string;
        alg: number;
    }>;
    timeout?: number;
    excludeCredentials?: Array<{
        type: string;
        id: string;
        transports?: AuthenticatorTransport[];
    }>;
    authenticatorSelection?: {
        authenticatorAttachment?: string;
        requireResidentKey?: boolean;
        userVerification?: string;
    };
    attestation?: string;
    extensions?: any;
}

export interface WebAuthnAuthenticationRequest {
    challenge: string;
    timeout?: number;
    rpId?: string;
    allowCredentials?: Array<{
        type: string;
        id: string;
        transports?: AuthenticatorTransport[];
    }>;
    userVerification?: string;
    extensions?: any;
}