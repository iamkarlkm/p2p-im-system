import { apiClient } from '../utils/apiClient';

export interface LocationShare {
    id: string;
    userId: string;
    recipientId: string;
    latitude: number;
    longitude: number;
    accuracy?: number;
    address?: string;
    shareType: 'STATIC' | 'REALTIME' | 'GEOFENCE';
    isActive: boolean;
    expiresAt?: string;
    durationMinutes?: number;
    createdAt: string;
}

export interface Geofence {
    id: string;
    userId: string;
    name: string;
    description?: string;
    latitude: number;
    longitude: number;
    radiusMeters: number;
    geofenceType: 'CIRCLE' | 'POLYGON';
    triggerType: 'ENTER' | 'EXIT' | 'DWELL';
    isActive: boolean;
    notifyOnEnter: boolean;
    notifyOnExit: boolean;
    createdAt: string;
}

export class LocationService {
    private static instance: LocationService;
    private readonly baseUrl = '/api/v1/location';
    private readonly geofenceUrl = '/api/v1/geofencing/geofences';
    
    private constructor() {}
    
    public static getInstance(): LocationService {
        if (!LocationService.instance) {
            LocationService.instance = new LocationService();
        }
        return LocationService.instance;
    }
    
    // Location Sharing
    public async startLocationShare(share: Partial<LocationShare>): Promise<LocationShare> {
        try {
            const response = await apiClient.post(`${this.baseUrl}/share/start`, share);
            return response.data.data;
        } catch (error) {
            console.error('Failed to start location share:', error);
            throw new Error(`Failed to start location sharing: ${error.message}`);
        }
    }
    
    public async updateLocation(id: string, latitude: number, longitude: number): Promise<LocationShare> {
        try {
            const response = await apiClient.put(`${this.baseUrl}/share/${id}/update`, { latitude, longitude });
            return response.data.data;
        } catch (error) {
            console.error('Failed to update location:', error);
            throw new Error(`Failed to update location: ${error.message}`);
        }
    }
    
    public async stopLocationShare(id: string): Promise<void> {
        try {
            await apiClient.post(`${this.baseUrl}/share/${id}/stop`);
        } catch (error) {
            console.error('Failed to stop location share:', error);
            throw new Error(`Failed to stop location sharing: ${error.message}`);
        }
    }
    
    public async getActiveShares(userId: string): Promise<LocationShare[]> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/share/user/${userId}/active`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get active shares:', error);
            throw new Error(`Failed to get active shares: ${error.message}`);
        }
    }
    
    public async getSharesForRecipient(recipientId: string): Promise<LocationShare[]> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/share/recipient/${recipientId}/active`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get shares for recipient:', error);
            throw new Error(`Failed to get shares: ${error.message}`);
        }
    }
    
    public async stopAllShares(userId: string): Promise<void> {
        try {
            await apiClient.post(`${this.baseUrl}/share/user/${userId}/stop-all`);
        } catch (error) {
            console.error('Failed to stop all shares:', error);
            throw new Error(`Failed to stop all shares: ${error.message}`);
        }
    }
    
    // Geofencing
    public async createGeofence(geofence: Partial<Geofence>): Promise<Geofence> {
        try {
            const response = await apiClient.post(this.geofenceUrl, geofence);
            return response.data.data;
        } catch (error) {
            console.error('Failed to create geofence:', error);
            throw new Error(`Failed to create geofence: ${error.message}`);
        }
    }
    
    public async getGeofenceById(id: string): Promise<Geofence> {
        try {
            const response = await apiClient.get(`${this.geofenceUrl}/${id}`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get geofence:', error);
            throw new Error(`Failed to get geofence: ${error.message}`);
        }
    }
    
    public async updateGeofence(id: string, updates: Partial<Geofence>): Promise<Geofence> {
        try {
            const response = await apiClient.put(`${this.geofenceUrl}/${id}`, updates);
            return response.data.data;
        } catch (error) {
            console.error('Failed to update geofence:', error);
            throw new Error(`Failed to update geofence: ${error.message}`);
        }
    }
    
    public async deleteGeofence(id: string): Promise<void> {
        try {
            await apiClient.delete(`${this.geofenceUrl}/${id}`);
        } catch (error) {
            console.error('Failed to delete geofence:', error);
            throw new Error(`Failed to delete geofence: ${error.message}`);
        }
    }
    
    public async getUserGeofences(userId: string): Promise<Geofence[]> {
        try {
            const response = await apiClient.get(`${this.geofenceUrl}/user/${userId}`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get user geofences:', error);
            throw new Error(`Failed to get geofences: ${error.message}`);
        }
    }
    
    public async getActiveGeofences(userId: string): Promise<Geofence[]> {
        try {
            const response = await apiClient.get(`${this.geofenceUrl}/user/${userId}/active`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to get active geofences:', error);
            throw new Error(`Failed to get active geofences: ${error.message}`);
        }
    }
    
    public async enableGeofence(id: string): Promise<void> {
        try {
            await apiClient.post(`${this.geofenceUrl}/${id}/enable`);
        } catch (error) {
            console.error('Failed to enable geofence:', error);
            throw new Error(`Failed to enable geofence: ${error.message}`);
        }
    }
    
    public async disableGeofence(id: string): Promise<void> {
        try {
            await apiClient.post(`${this.geofenceUrl}/${id}/disable`);
        } catch (error) {
            console.error('Failed to disable geofence:', error);
            throw new Error(`Failed to disable geofence: ${error.message}`);
        }
    }
    
    // Geolocation helper
    public getCurrentPosition(): Promise<GeolocationPosition> {
        return new Promise((resolve, reject) => {
            if (!navigator.geolocation) {
                reject(new Error('Geolocation not supported'));
                return;
            }
            navigator.geolocation.getCurrentPosition(
                (position) => resolve(position),
                (error) => reject(error),
                { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
            );
        });
    }
    
    public watchPosition(
        callback: (position: GeolocationPosition) => void,
        error?: (error: GeolocationPositionError) => void
    ): number {
        if (!navigator.geolocation) {
            throw new Error('Geolocation not supported');
        }
        return navigator.geolocation.watchPosition(callback, error, {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
        });
    }
    
    public clearWatch(watchId: number): void {
        navigator.geolocation.clearWatch(watchId);
    }
    
    // Reverse geocoding (simplified)
    public async reverseGeocode(lat: number, lon: number): Promise<string> {
        // In real implementation, use a geocoding service
        return `${lat.toFixed(6)}, ${lon.toFixed(6)}`;
    }
    
    // Calculate distance between two points (Haversine formula)
    public calculateDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
        const R = 6371000; // Earth's radius in meters
        const dLat = this.toRad(lat2 - lat1);
        const dLon = this.toRad(lon2 - lon1);
        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(this.toRad(lat1)) * Math.cos(this.toRad(lat2)) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    private toRad(degrees: number): number {
        return degrees * Math.PI / 180;
    }
}

export default LocationService.getInstance();