import 'dart:typed_data';

/// XOR混淆层
/// 
/// 固定offset，每帧从0开始，不wrap（超出keyfile长度会抛异常）

Uint8List xorNoWrap(Uint8List plain, Uint8List keyfile, int offset) {
  if (offset < 0 || offset >= keyfile.length) {
    throw ArgumentError('offset out of range');
  }
  if (offset + plain.length > keyfile.length) {
    throw ArgumentError('offset+plainLen exceeds keyLen (no wrap)');
  }
  final out = Uint8List(plain.length);
  for (int i = 0; i < plain.length; i++) {
    out[i] = plain[i] ^ keyfile[offset + i];
  }
  return out;
}
