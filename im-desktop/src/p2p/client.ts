import type protobuf from "protobufjs"
import { loadProtoRoot } from "./proto.js"
import { decodeFrame, encodeFrame, type WireHeader } from "./frame.js"
import { decodeWrapper, encodeWrapper, type P2PWrapper } from "./wrapper.js"
import { xorNoWrap } from "./xor.js"
import { generateRsaKeyPair, exportPublicKeySpki, rsaOaepSha256Decrypt } from "./handshake.js"

export type ImMessage = {
  type: string
  messageId?: string
  senderId?: string
  receiverId?: string
  groupId?: string | null
  conversationId?: string
  timestamp?: number
  payload?: Record<string, any>
  [key: string]: any
}

export type P2PClientOptions = {
  url: string
  token?: string
  userId?: string
  deviceId?: string
  keyfile?: Uint8Array
  magic?: number
  version?: number
  flagsPlain?: number
  flagsEncrypted?: number
  maxFramePayload?: number
  onConnect?: () => void
  onDisconnect?: (reason: string) => void
  onMessage?: (msg: ImMessage) => void
  onError?: (err: any) => void
  reconnect?: boolean
  reconnectInterval?: number
  maxReconnectAttempts?: number
}

export class ImP2PClient {
  private ws: WebSocket | null = null
  private root: protobuf.Root | null = null
  private keyPair: CryptoKeyPair | null = null
  private privateKey: CryptoKey | null = null

  private seq = 1
  private offset = -1
  private encrypted = false
  private keyId: Uint8Array | null = null

  private reconnectAttempts = 0
  private reconnectTimer: number | null = null

  private options: P2PClientOptions

  constructor(options: P2PClientOptions) {
    this.options = {
      magic: 0x1234,
      version: 1,
      flagsPlain: 4,
      flagsEncrypted: 5,
      maxFramePayload: 4 * 1024 * 1024,
      reconnect: true,
      reconnectInterval: 5000,
      maxReconnectAttempts: 10,
      ...options,
    }
  }

  async connect(): Promise<void> {
    if (this.ws?.readyState === WebSocket.OPEN) return

    this.root = await loadProtoRoot()
    this.keyPair = await generateRsaKeyPair()
    this.privateKey = this.keyPair.privateKey

    // 计算 keyId = SHA256(keyfile)
    if (this.options.keyfile) {
      const hash = await crypto.subtle.digest("SHA-256", this.options.keyfile)
      this.keyId = new Uint8Array(hash)
    }

    const url = new URL(this.options.url)
    if (this.options.token) url.searchParams.set("token", this.options.token)
    if (this.options.userId) url.searchParams.set("userId", this.options.userId)
    if (this.options.deviceId) url.searchParams.set("deviceId", this.options.deviceId)

    this.ws = new WebSocket(url.toString())
    this.ws.binaryType = "arraybuffer"

    this.ws.onopen = () => {
      this.reconnectAttempts = 0
      this.sendHand().catch((e) => this.options.onError?.(e))
    }

    this.ws.onmessage = async (event) => {
      try {
        const data = new Uint8Array(event.data as ArrayBuffer)
        await this.handleFrame(data)
      } catch (e) {
        this.options.onError?.(e)
      }
    }

    this.ws.onclose = (event) => {
      this.options.onDisconnect?.(event.reason || "Connection closed")
      this.scheduleReconnect()
    }

    this.ws.onerror = (error) => {
      this.options.onError?.(error)
    }
  }

  disconnect(): void {
    if (this.reconnectTimer) {
      window.clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    this.ws?.close()
    this.ws = null
    this.encrypted = false
    this.reconnectAttempts = this.options.maxReconnectAttempts || 10
  }

  isConnected(): boolean {
    return this.ws?.readyState === WebSocket.OPEN && this.encrypted
  }

  sendImMessage(command: number, msg: ImMessage): void {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      this.options.onError?.(new Error("WebSocket not connected"))
      return
    }
    const json = JSON.stringify(msg)
    const plain = new TextEncoder().encode(json)
    const wrapper: P2PWrapper = {
      seq: this.seq++,
      command,
      data: plain,
    }
    const payload = this.encrypted && this.options.keyfile && this.offset >= 0
      ? xorNoWrap(plain, this.options.keyfile, this.offset)
      : plain

    const header: WireHeader = {
      length: payload.length,
      magic: this.options.magic!,
      version: this.options.version!,
      flags: this.encrypted ? this.options.flagsEncrypted! : this.options.flagsPlain!,
    }
    const frame = encodeFrame(header, payload)
    this.ws.send(frame)
  }

  sendChat(msg: ImMessage): void {
    this.sendImMessage(20001, msg)
  }

  sendGroupChat(msg: ImMessage): void {
    this.sendImMessage(20002, msg)
  }

  sendReadReceipt(msg: ImMessage): void {
    this.sendImMessage(20003, msg)
  }

  sendTyping(msg: ImMessage): void {
    this.sendImMessage(20005, msg)
  }

  sendPresence(status: string): void {
    this.sendImMessage(20006, { type: "presence", payload: { status }, timestamp: Date.now() })
  }

  private async sendHand(): Promise<void> {
    if (!this.root || !this.keyPair) return
    const pubDer = await exportPublicKeySpki(this.keyPair.publicKey)
    const Hand = this.root.lookupType("p2pws.Hand")
    const handMsg = Hand.encode({
      clientPubkey: pubDer,
      keyIds: this.keyId ? [this.keyId] : [],
      maxFramePayload: this.options.maxFramePayload,
      clientId: this.options.userId || "",
    }).finish()

    const wrapper: P2PWrapper = {
      seq: this.seq++,
      command: -10001,
      data: handMsg,
    }
    const payload = encodeWrapper(this.root, wrapper)
    const header: WireHeader = {
      length: payload.length,
      magic: this.options.magic!,
      version: this.options.version!,
      flags: this.options.flagsPlain!,
    }
    const frame = encodeFrame(header, payload)
    this.ws?.send(frame)
  }

  private async handleFrame(data: Uint8Array): Promise<void> {
    if (!this.root) throw new Error("proto root not loaded")

    const f = decodeFrame(data)
    const payload = f.cipherPayload
    const plain = this.encrypted && this.options.keyfile && this.offset >= 0
      ? xorNoWrap(payload, this.options.keyfile, this.offset)
      : payload

    const wrapper = decodeWrapper(this.root, plain)

    if (wrapper.command === -10002) {
      await this.handleHandAck(wrapper)
      return
    }

    if (!this.encrypted) {
      this.options.onError?.(new Error("HANDSHAKE_REQUIRED"))
      return
    }

    if (wrapper.command >= 20001 && wrapper.command <= 20012) {
      const json = new TextDecoder().decode(wrapper.data)
      const msg: ImMessage = JSON.parse(json)
      this.options.onMessage?.(msg)
    }
  }

  private async handleHandAck(wrapper: P2PWrapper): Promise<void> {
    if (!this.root || !this.privateKey) return
    const encryptedAck = wrapper.data
    if (!encryptedAck) return

    const plain = await rsaOaepSha256Decrypt(this.privateKey, encryptedAck)
    const HandAckPlain = this.root.lookupType("p2pws.HandAckPlain")
    const ack = HandAckPlain.decode(plain) as any

    this.offset = Number(ack.offset)
    this.encrypted = true

    this.options.onConnect?.()

    // 发送 connected 确认消息（可选，用于兼容现有 UI 逻辑）
    this.sendImMessage(20010, {
      type: "heartbeat",
      timestamp: Date.now(),
      payload: { serverTime: Date.now() },
    })
  }

  private scheduleReconnect(): void {
    if (!this.options.reconnect || this.reconnectAttempts >= (this.options.maxReconnectAttempts || 10)) return
    this.reconnectAttempts++
    this.reconnectTimer = window.setTimeout(() => {
      this.connect().catch((e) => this.options.onError?.(e))
    }, (this.options.reconnectInterval || 5000) * this.reconnectAttempts)
  }
}
