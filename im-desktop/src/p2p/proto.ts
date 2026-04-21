import protobuf from "protobufjs"
import { P2P_WRAPPER_PROTO, P2P_CONTROL_PROTO, P2P_DATA_PROTO } from "./proto_strings.js"

export async function loadProtoRoot(): Promise<protobuf.Root> {
  const root = new protobuf.Root()
  await root.load([P2P_WRAPPER_PROTO, P2P_CONTROL_PROTO, P2P_DATA_PROTO], { keepCase: true })
  root.resolveAll()
  return root
}
