import 'dart:convert';
import 'dart:typed_data';

import 'package:pointycastle/asymmetric/api.dart';
import 'package:pointycastle/export.dart';

/// RSA-OAEP(SHA-256) 握手工具

class P2PHandshake {
  /// 生成 RSA-2048 密钥对
  static AsymmetricKeyPair<RSAPublicKey, RSAPrivateKey> generateKeyPair() {
    final keyGen = RSAKeyGenerator()
      ..init(ParametersWithRandom(
        RSAKeyGeneratorParameters(BigInt.parse('65537'), 2048, 64),
        SecureRandom('Fortuna')..seed(KeyParameter(Uint8List.fromList(
          DateTime.now().millisecondsSinceEpoch.toString().codeUnits
        ))),
      ));
    return keyGen.generateKeyPair() as AsymmetricKeyPair<RSAPublicKey, RSAPrivateKey>;
  }

  /// 导出公钥为 SPKI (DER) 格式
  static Uint8List exportPublicKeySpki(RSAPublicKey publicKey) {
    final algorithmSeq = ASN1Sequence()
      ..add(ASN1ObjectIdentifier.fromName('rsaEncryption'))
      ..add(ASN1Null());

    final publicKeySeq = ASN1Sequence()
      ..add(ASN1Integer(publicKey.modulus!))
      ..add(ASN1Integer(publicKey.exponent!));

    final subjectPublicKeyInfo = ASN1Sequence()
      ..add(algorithmSeq)
      ..add(ASN1BitString(publicKeySeq.encodedBytes));

    return Uint8List.fromList(subjectPublicKeyInfo.encodedBytes);
  }

  /// RSA-OAEP(SHA-256) 解密
  static Uint8List rsaOaepSha256Decrypt(RSAPrivateKey privateKey, Uint8List cipher) {
    final decryptor = OAEPEncoding.withSHA256(RSAEngine())
      ..init(false, PrivateKeyParameter<RSAPrivateKey>(privateKey));
    return _processInBlocks(decryptor, cipher);
  }

  /// RSA-OAEP(SHA-256) 加密
  static Uint8List rsaOaepSha256Encrypt(RSAPublicKey publicKey, Uint8List plain) {
    final encryptor = OAEPEncoding.withSHA256(RSAEngine())
      ..init(true, PublicKeyParameter<RSAPublicKey>(publicKey));
    return _processInBlocks(encryptor, plain);
  }

  static Uint8List _processInBlocks(AsymmetricBlockCipher engine, Uint8List input) {
    final numBlocks = (input.length + engine.inputBlockSize - 1) ~/ engine.inputBlockSize;
    final output = BytesBuilder();
    for (int i = 0; i < numBlocks; i++) {
      final start = i * engine.inputBlockSize;
      final end = (start + engine.inputBlockSize < input.length) 
          ? start + engine.inputBlockSize 
          : input.length;
      output.add(engine.process(Uint8List.sublistView(input, start, end)));
    }
    return output.toBytes();
  }
}

/// ASN1 helper classes for SPKI encoding
class ASN1Object {
  final int tag;
  final List<int> value;

  ASN1Object(this.tag, this.value);

  List<int> get encodedBytes {
    final lengthBytes = _encodeLength(value.length);
    return [tag, ...lengthBytes, ...value];
  }

  List<int> _encodeLength(int length) {
    if (length < 128) return [length];
    final bytes = <int>[];
    var temp = length;
    while (temp > 0) {
      bytes.insert(0, temp & 0xFF);
      temp >>= 8;
    }
    return [0x80 | bytes.length, ...bytes];
  }
}

class ASN1Sequence extends ASN1Object {
  final List<ASN1Object> objects = [];

  ASN1Sequence() : super(0x30, []);

  void add(ASN1Object obj) => objects.add(obj);

  @override
  List<int> get value {
    final result = BytesBuilder();
    for (final obj in objects) {
      result.add(obj.encodedBytes);
    }
    return result.toBytes();
  }
}

class ASN1Integer extends ASN1Object {
  ASN1Integer(BigInt value) 
    : super(0x02, _encodeBigInt(value));

  static List<int> _encodeBigInt(BigInt value) {
    var bytes = value.toByteArray();
    // Ensure positive encoding
    if (bytes.isNotEmpty && bytes[0] & 0x80 != 0) {
      bytes = [0x00, ...bytes];
    }
    return bytes;
  }
}

class ASN1Null extends ASN1Object {
  ASN1Null() : super(0x05, []);
}

class ASN1BitString extends ASN1Object {
  ASN1BitString(List<int> data) : super(0x03, [0x00, ...data]);
}

class ASN1ObjectIdentifier extends ASN1Object {
  ASN1ObjectIdentifier._(List<int> bytes) : super(0x06, bytes);

  static ASN1ObjectIdentifier fromName(String name) {
    // rsaEncryption OID: 1.2.840.113549.1.1.1
    if (name == 'rsaEncryption') {
      return ASN1ObjectIdentifier._([0x2A, 0x86, 0x48, 0x86, 0xF7, 0x0D, 0x01, 0x01, 0x01]);
    }
    throw UnsupportedError('Unknown OID: $name');
  }
}
