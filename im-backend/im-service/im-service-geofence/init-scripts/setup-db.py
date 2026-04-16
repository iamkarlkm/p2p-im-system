#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import mysql.connector

password = 'karl1972'

try:
    conn = mysql.connector.connect(
        host='localhost',
        user='root',
        password=password
    )
    print('Connected to MySQL successfully!')
    
    cursor = conn.cursor()
    
    # Create database
    cursor.execute('CREATE DATABASE IF NOT EXISTS im_geofence CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci')
    conn.commit()
    print('Database im_geofence created/verified!')
    
    # Use database
    cursor.execute('USE im_geofence')
    
    # Create geofence table
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS im_geofence (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            geofence_id VARCHAR(64) UNIQUE,
            name VARCHAR(100) NOT NULL,
            description VARCHAR(500),
            fence_type VARCHAR(20),
            merchant_id VARCHAR(64),
            poi_id VARCHAR(64),
            center_latitude DOUBLE,
            center_longitude DOUBLE,
            radius INT,
            coordinates TEXT,
            geo_hash VARCHAR(20),
            level INT DEFAULT 1,
            parent_id VARCHAR(64),
            trigger_condition VARCHAR(20),
            dwell_time INT,
            effective_start_time DATETIME,
            effective_end_time DATETIME,
            business_hours VARCHAR(500),
            effective_weekdays VARCHAR(50),
            status VARCHAR(20) DEFAULT 'ACTIVE',
            is_enabled BOOLEAN DEFAULT TRUE,
            create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
            update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            INDEX idx_merchant_id (merchant_id),
            INDEX idx_geo_hash (geo_hash),
            INDEX idx_status (status)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    ''')
    print('Table im_geofence created!')
    
    # Create location share table
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS im_location_share (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            share_id VARCHAR(64) UNIQUE,
            user_id VARCHAR(64) NOT NULL,
            recipient_id VARCHAR(64),
            latitude DOUBLE NOT NULL,
            longitude DOUBLE NOT NULL,
            accuracy DOUBLE,
            address VARCHAR(200),
            share_type VARCHAR(20) DEFAULT 'REALTIME',
            is_active BOOLEAN DEFAULT TRUE,
            expires_at DATETIME,
            duration_minutes INT DEFAULT 1440,
            create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
            update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            INDEX idx_user_id (user_id),
            INDEX idx_recipient_id (recipient_id),
            INDEX idx_is_active (is_active)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    ''')
    print('Table im_location_share created!')
    
    # Create geofence event table
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS im_geofence_event (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            event_id VARCHAR(64) UNIQUE,
            geofence_id VARCHAR(64) NOT NULL,
            user_id VARCHAR(64) NOT NULL,
            event_type VARCHAR(20),
            latitude DOUBLE NOT NULL,
            longitude DOUBLE NOT NULL,
            accuracy DOUBLE,
            speed DOUBLE,
            bearing DOUBLE,
            device_id VARCHAR(64),
            app_version VARCHAR(20),
            event_time DATETIME,
            create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
            INDEX idx_geofence_id (geofence_id),
            INDEX idx_user_id (user_id),
            INDEX idx_event_time (event_time)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    ''')
    print('Table im_geofence_event created!')
    
    conn.commit()
    print('All tables created successfully!')
    
    # Verify
    cursor.execute('SHOW TABLES')
    tables = cursor.fetchall()
    print('Tables in im_geofence:')
    for t in tables:
        print(f'  - {t[0]}')
    
    cursor.close()
    conn.close()
    print('Database setup completed!')
    
except Exception as e:
    print(f'Error: {e}')
