import os
from typing import Optional

from dotenv import load_dotenv
from langchain_openai import ChatOpenAI


def build_model(model: str = "gpt-4o-mini", temperature: float = 0.2) -> ChatOpenAI:
    """환경 변수에서 키를 읽어 모델 인스턴스를 생성."""
    load_dotenv()
    api_key: Optional[str] = os.getenv("OPENAI_API_KEY")
    if not api_key:
        raise RuntimeError("환경변수 OPENAI_API_KEY가 설정되어 있지 않습니다.")

    return ChatOpenAI(
        model=model,
        temperature=temperature,
        api_key=api_key,
    )
