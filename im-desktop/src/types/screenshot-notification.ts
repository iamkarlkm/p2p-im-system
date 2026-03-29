export interface ScreenshotEvent {
  eventId: string;
  conversationId: number;
  conversationType: 'private' | 'group';
  capturedByUserId: number;
  capturedByUsername: string;
  screenshotTime: string;
  deviceType: string;
  message: string;
}

export interface ScreenshotSettings {
  userId: number;
  enableScreenshotNotification: boolean;
  notifyOnCapture: boolean;
  receiveScreenshotAlerts: boolean;
  alertForContacts: boolean;
  alertForGroups: boolean;
  silentMode: boolean;
}

export interface ScreenshotEventRequest {
  conversationId: number;
  conversationType: 'private' | 'group';
  deviceType: string;
  deviceInfo: string;
}

export const defaultScreenshotSettings: ScreenshotSettings = {
  userId: 0,
  enableScreenshotNotification: true,
  notifyOnCapture: true,
  receiveScreenshotAlerts: true,
  alertForContacts: true,
  alertForGroups: true,
  silentMode: false,
};
