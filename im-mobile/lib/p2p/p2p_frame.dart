import 'dart:typed_data';

/// P2P Wire Protocol Frame Layer
/// 
/// Frame format:
/// | length (4 bytes) | magic (2 bytes) | version (1 byte) | flags (1 byte) | cipherPayload (N bytes) |

class WireHeader {
  final int length;
  final int magic;
  final int version;
  final int flags;

  WireHeader({
    required this.length,
    required this.magic,
    required this.version,
    required this.flags,
  });

  static const int headerLength = 8;

  static WireHeader decode(Uint8List data) {
    if (data.length < headerLength) {
      throw FormatException('Invalid frame: too short for header');
    }
    final byteData = ByteData.sublistView(data);
    final length = byteData.getUint32(0, Endian.big);
    final magic = byteData.getUint16(4, Endian.big);
    final version = byteData.getUint8(6);
    final flags = byteData.getUint8(7);
    return WireHeader(length: length, magic: magic, version: version, flags: flags);
  }

  Uint8List encode() {
    final buffer = Uint8List(headerLength);
    final byteData = ByteData.sublistView(buffer);
    byteData.setUint32(0, length, Endian.big);
    byteData.setUint16(4, magic & 0xFFFF, Endian.big);
    byteData.setUint8(6, version & 0xFF);
    byteData.setUint8(7, flags & 0xFF);
    return buffer;
  }
}

class WireFrame {
  final WireHeader header;
  final Uint8List cipherPayload;

  WireFrame({required this.header, required this.cipherPayload});

  static WireFrame decode(Uint8List data) {
    final header = WireHeader.decode(data);
    final cipherPayload = Uint8List.sublistView(data, WireHeader.headerLength);
    return WireFrame(header: header, cipherPayload: cipherPayload);
  }

  Uint8List encode() {
    final headerBytes = header.encode();
    final result = Uint8List(headerBytes.length + cipherPayload.length);
    result.setRange(0, headerBytes.length, headerBytes);
    result.setRange(headerBytes.length, result.length, cipherPayload);
    return result;
  }
}
