import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:geolocator/geolocator.dart';

class LocationShare {
  final String id;
  final String userId;
  final String recipientId;
  final double latitude;
  final double longitude;
  final double? accuracy;
  final String? address;
  final String shareType;
  final bool isActive;
  final DateTime? expiresAt;
  final int? durationMinutes;
  final DateTime createdAt;
  
  LocationShare({
    required this.id,
    required this.userId,
    required this.recipientId,
    required this.latitude,
    required this.longitude,
    this.accuracy,
    this.address,
    required this.shareType,
    required this.isActive,
    this.expiresAt,
    this.durationMinutes,
    required this.createdAt,
  });
  
  factory LocationShare.fromJson(Map<String, dynamic> json) {
    return LocationShare(
      id: json['id'],
      userId: json['userId'],
      recipientId: json['recipientId'],
      latitude: json['latitude'],
      longitude: json['longitude'],
      accuracy: json['accuracy'],
      address: json['address'],
      shareType: json['shareType'],
      isActive: json['isActive'],
      expiresAt: json['expiresAt'] != null ? DateTime.parse(json['expiresAt']) : null,
      durationMinutes: json['durationMinutes'],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }
}

class Geofence {
  final String id;
  final String userId;
  final String name;
  final String? description;
  final double latitude;
  final double longitude;
  final double radiusMeters;
  final String geofenceType;
  final String triggerType;
  final bool isActive;
  final bool notifyOnEnter;
  final bool notifyOnExit;
  final DateTime createdAt;
  
  Geofence({
    required this.id,
    required this.userId,
    required this.name,
    this.description,
    required this.latitude,
    required this.longitude,
    required this.radiusMeters,
    required this.geofenceType,
    required this.triggerType,
    required this.isActive,
    required this.notifyOnEnter,
    required this.notifyOnExit,
    required this.createdAt,
  });
  
  factory Geofence.fromJson(Map<String, dynamic> json) {
    return Geofence(
      id: json['id'],
      userId: json['userId'],
      name: json['name'],
      description: json['description'],
      latitude: json['latitude'],
      longitude: json['longitude'],
      radiusMeters: json['radiusMeters'],
      geofenceType: json['geofenceType'],
      triggerType: json['triggerType'],
      isActive: json['isActive'],
      notifyOnEnter: json['notifyOnEnter'],
      notifyOnExit: json['notifyOnExit'],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }
}

class LocationService {
  static final LocationService _instance = LocationService._internal();
  final String _baseUrl = 'http://localhost:8080/api/v1/location';
  final String _geofenceUrl = 'http://localhost:8080/api/v1/geofence';
  
  factory LocationService() {
    return _instance;
  }
  
  LocationService._internal();
  
  // Location Sharing
  Future<LocationShare> startLocationShare(Map<String, dynamic> shareData) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/share/start'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(shareData),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return LocationShare.fromJson(responseData['data']);
      } else {
        throw Exception('Failed to start location share');
      }
    } catch (e) {
      print('Error starting location share: $e');
      throw Exception('Failed to start location sharing: $e');
    }
  }
  
  Future<LocationShare> updateLocation(String id, double latitude, double longitude) async {
    try {
      final response = await http.put(
        Uri.parse('$_baseUrl/share/$id/update'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'latitude': latitude, 'longitude': longitude}),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return LocationShare.fromJson(responseData['data']);
      } else {
        throw Exception('Failed to update location');
      }
    } catch (e) {
      print('Error updating location: $e');
      throw Exception('Failed to update location: $e');
    }
  }
  
  Future<void> stopLocationShare(String id) async {
    try {
      await http.post(Uri.parse('$_baseUrl/share/$id/stop'));
    } catch (e) {
      print('Error stopping location share: $e');
      throw Exception('Failed to stop location sharing: $e');
    }
  }
  
  Future<List<LocationShare>> getActiveShares(String userId) async {
    try {
      final response = await http.get(Uri.parse('$_baseUrl/share/user/$userId/active'));
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        final List<dynamic> sharesJson = responseData['data'];
        return sharesJson.map((json) => LocationShare.fromJson(json)).toList();
      } else {
        throw Exception('Failed to get active shares');
      }
    } catch (e) {
      print('Error getting active shares: $e');
      throw Exception('Failed to get active shares: $e');
    }
  }
  
  Future<void> stopAllShares(String userId) async {
    try {
      await http.post(Uri.parse('$_baseUrl/share/user/$userId/stop-all'));
    } catch (e) {
      print('Error stopping all shares: $e');
      throw Exception('Failed to stop all shares: $e');
    }
  }
  
  // Geofencing
  Future<Geofence> createGeofence(Map<String, dynamic> geofenceData) async {
    try {
      final response = await http.post(
        Uri.parse(_geofenceUrl),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(geofenceData),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return Geofence.fromJson(responseData['data']);
      } else {
        throw Exception('Failed to create geofence');
      }
    } catch (e) {
      print('Error creating geofence: $e');
      throw Exception('Failed to create geofence: $e');
    }
  }
  
  Future<List<Geofence>> getUserGeofences(String userId) async {
    try {
      final response = await http.get(Uri.parse('$_geofenceUrl/user/$userId'));
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        final List<dynamic> geofencesJson = responseData['data'];
        return geofencesJson.map((json) => Geofence.fromJson(json)).toList();
      } else {
        throw Exception('Failed to get geofences');
      }
    } catch (e) {
      print('Error getting geofences: $e');
      throw Exception('Failed to get geofences: $e');
    }
  }
  
  Future<void> deleteGeofence(String id) async {
    try {
      await http.delete(Uri.parse('$_geofenceUrl/$id'));
    } catch (e) {
      print('Error deleting geofence: $e');
      throw Exception('Failed to delete geofence: $e');
    }
  }
  
  Future<void> deactivateGeofence(String id) async {
    try {
      await http.post(Uri.parse('$_geofenceUrl/$id/deactivate'));
    } catch (e) {
      print('Error deactivating geofence: $e');
      throw Exception('Failed to deactivate geofence: $e');
    }
  }
  
  Future<void> activateGeofence(String id) async {
    try {
      await http.post(Uri.parse('$_geofenceUrl/$id/activate'));
    } catch (e) {
      print('Error activating geofence: $e');
      throw Exception('Failed to activate geofence: $e');
    }
  }
  
  // Geolocation helper
  Future<Position> getCurrentPosition() async {
    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        throw Exception('Location permissions are denied');
      }
    }
    
    return await Geolocator.getCurrentPosition(
      desiredAccuracy: LocationAccuracy.high,
    );
  }
  
  Stream<Position> watchPosition() {
    return Geolocator.getPositionStream(
      locationSettings: const LocationSettings(
        accuracy: LocationAccuracy.high,
        distanceFilter: 10,
      ),
    );
  }
  
  // Calculate distance
  double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    return Geolocator.distanceBetween(lat1, lon1, lat2, lon2);
  }
  
  // Permission check
  Future<bool> hasPermission() async {
    LocationPermission permission = await Geolocator.checkPermission();
    return permission == LocationPermission.whileInUse || 
           permission == LocationPermission.always;
  }
  
  Future<bool> requestPermission() async {
    LocationPermission permission = await Geolocator.requestPermission();
    return permission == LocationPermission.whileInUse || 
           permission == LocationPermission.always;
  }
}