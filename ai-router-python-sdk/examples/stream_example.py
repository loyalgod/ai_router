"""Stream SDK usage example."""

from __future__ import annotations

import os

from ai_router_sdk import ChatChunk, StreamCallback, AiRouterClient


class PrintStreamCallback(StreamCallback):
    def on_message(self, chunk: ChatChunk) -> None:
        if chunk.reasoning_content:
            print(f"[思考] {chunk.reasoning_content}")
        if chunk.content:
            print(chunk.content, end="", flush=True)

    def on_complete(self) -> None:
        print("\n\n✅ 完成")

    def on_error(self, error: Exception) -> None:
        print(f"\n❌ 错误: {error}")


def main() -> None:
    api_key = os.getenv("AI_ROUTER_API_KEY", "")
    base_url = os.getenv("AI_ROUTER_BASE_URL", "http://localhost:8123/api")
    client = AiRouterClient.builder().api_key(api_key).base_url(base_url).build()
    try:
        print("开始流式调用...\n")
        client.chat_stream("写一首关于春天的诗", PrintStreamCallback())
    finally:
        client.close()


if __name__ == "__main__":
    main()
