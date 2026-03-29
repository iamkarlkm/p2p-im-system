// 2FA Types for IM Desktop

export interface TwoFactorSetupRequest {
  password: string;
  issuerName?: string;
  accountName?: string;
}

export interface TwoFactorSetupResponse {
  secret: string;
  qrCodeUrl: string;
  manualEntryKey: string;
  backupCodes: string[];
  provisioningUri: string;
  appInfo: {
    name: string;
    account: string;
    algorithm: string;
    digits: number;
    period: number;
  };
}

export interface TwoFactorVerifyRequest {
  code: string;
  isBackupCode?: boolean;
  deviceName?: string;
}

export interface TwoFactorVerifyResponse {
  success: boolean;
  token?: string;
  remainingBackupCodes?: number;
  message?: string;
  expiresIn?: number;
}

export interface TwoFactorStatusResponse {
  isEnabled: boolean;
  isVerified: boolean;
  backupCodesRemaining: number;
  lastVerifiedAt?: number;
  issuerName?: string;
  accountName?: string;
  isRequired: boolean;
}

export interface TwoFactorBackupCode {
  code: string;
  used: boolean;
  usedAt?: number;
}

export interface TwoFactorState {
  isEnabled: boolean;
  isVerified: boolean;
  setupInProgress: boolean;
  verifyPending: boolean;
  backupCodes: TwoFactorBackupCode[];
  lastVerifiedAt?: number;
}

export type TwoFactorStep = 'idle' | 'scan_qr' | 'verify_code' | 'backup_codes' | 'complete';
