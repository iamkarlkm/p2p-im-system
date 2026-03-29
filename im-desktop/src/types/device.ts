// Device Management Types

export interface Device {
  id: number;
  userId: string;
  deviceToken: string;
  deviceType: DeviceType;
  deviceName: string;
  deviceModel: string;
  osVersion: string;
  appVersion: string;
  browserInfo: string;
  ipAddress: string;
  location: string;
  createdAt: string;
  lastActiveAt: string;
  isCurrent: boolean;
  lastLoginAt: string;
  isActive: boolean;
  isTrusted: boolean;
}

export type DeviceType = 'DESKTOP' | 'MOBILE' | 'TABLET' | 'WEB' | 'OTHER';

export interface DeviceRegistrationRequest {
  deviceToken?: string;
  deviceType: DeviceType;
  deviceName: string;
  deviceModel?: string;
  osVersion?: string;
  appVersion?: string;
  browserInfo?: string;
  ipAddress?: string;
  location?: string;
  latitude?: number;
  longitude?: number;
  isTrusted?: boolean;
}

export interface DeviceUpdateRequest {
  deviceId: number;
  deviceName?: string;
  isTrusted?: boolean;
}

export interface DevicePage {
  items: Device[];
  page: number;
  size: number;
  total: number;
  totalPages: number;
}

export interface LoginHistoryEntry {
  id: number;
  userId: string;
  deviceId: number;
  deviceToken: string;
  deviceType: DeviceType;
  deviceName: string;
  ipAddress: string;
  location: string;
  loginTime: string;
  logoutTime: string;
  action: LoginAction;
  loginStatus: 'SUCCESS' | 'FAILED';
}

export type LoginAction = 'LOGIN' | 'LOGOUT' | 'REMOVE';

export interface LoginHistoryPage {
  items: LoginHistoryEntry[];
  page: number;
  size: number;
  total: number;
  totalPages: number;
}

export interface DeviceStats {
  totalDevices: number;
  activeDevices: number;
  trustedDevices: number;
  mostUsedDeviceType: DeviceType;
  activeSessions: number;
}

export interface DeviceFilter {
  isActive?: boolean;
  isTrusted?: boolean;
  deviceType?: DeviceType;
  sortBy?: 'lastActiveAt' | 'createdAt' | 'deviceName';
  sortOrder?: 'asc' | 'desc';
}

export interface RemoteLogoutRequest {
  deviceId: number;
  reason?: string;
}

export interface DeviceAlert {
  type: 'NEW_DEVICE' | 'SUSPICIOUS_LOGIN' | 'UNKNOWN_LOCATION' | 'MULTIPLE_FAILURES';
  deviceId: number;
  deviceName: string;
  ipAddress: string;
  location: string;
  timestamp: string;
  acknowledged: boolean;
}
