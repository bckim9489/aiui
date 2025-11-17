from langchain_core.prompts import ChatPromptTemplate


def build_prompt() -> ChatPromptTemplate:
    """프롬프트 템플릿만 생성해서 반환."""
    return ChatPromptTemplate.from_messages(
        [
            (
                "system",
                "너는 LangChain 기반 백엔드 에이전트다. 응답은 간결하게 한국어로 해라.",
            ),
            ("human", "{input}"),
        ]
    )
