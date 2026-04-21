export async function generateRsaKeyPair(): Promise<CryptoKeyPair> {
  return crypto.subtle.generateKey(
    {
      name: "RSA-OAEP",
      modulusLength: 2048,
      publicExponent: new Uint8Array([1, 0, 1]),
      hash: "SHA-256",
    },
    true,
    ["encrypt", "decrypt"]
  )
}

export async function exportPublicKeySpki(publicKey: CryptoKey): Promise<Uint8Array> {
  return new Uint8Array(await crypto.subtle.exportKey("spki", publicKey))
}

export async function rsaOaepSha256Decrypt(privateKey: CryptoKey, cipher: Uint8Array): Promise<Uint8Array> {
  return new Uint8Array(
    await crypto.subtle.decrypt({ name: "RSA-OAEP" }, privateKey, cipher)
  )
}
