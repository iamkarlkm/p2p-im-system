export interface LoginAnomalyAlert {
  id: number;
  userId: number;
  alertType: string;
  deviceId?: string;
  deviceName?: string;
  deviceType?: string;
  ipAddress?: string;
  location?: string;
  loginTime: string;
  isConfirmed: boolean;
  confirmedAt?: string;
  isDismissed: boolean;
  dismissedAt?: string;
  riskScore?: number;
  riskFactors?: string;
  actionTaken?: string;
  createdAt: string;
}

export interface LoginAnomalySettings {
  userId: number;
  alertEnabled: boolean;
  crossRegionAlert: boolean;
  newDeviceAlert: boolean;
  abnormalFrequencyAlert: boolean;
  unknownDeviceAlert: boolean;
  alertChannels: string[];
  knownIps?: string[];
  knownLocations?: string[];
  maxLoginAttemptsPerHour: number;
  autoLockThreshold: number;
}

export type AlertType = 'NEW_DEVICE' | 'CROSS_REGION' | 'ABNORMAL_FREQUENCY' | 'UNKNOWN_DEVICE' | 'NORMAL';
