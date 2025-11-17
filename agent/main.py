import os
from dotenv import load_dotenv
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate


def build_chain():
    prompt = ChatPromptTemplate.from_messages(
        [
            (
                "system",
                "너는 LangChain 기반 백엔드 에이전트다. 응답은 간결하게 한국어로 해라.",
            ),
            ("human", "{input}"),
        ]
    )
    llm = ChatOpenAI(
        model="gpt-4o-mini",  # 필요 시 원하는 모델로 변경
        temperature=0.2,
    )
    return prompt | llm


def main():
    load_dotenv()
    if not os.getenv("OPENAI_API_KEY"):
        raise RuntimeError("환경변수 OPENAI_API_KEY가 설정되어 있지 않습니다.")

    chain = build_chain()
    res = chain.invoke({"input": "LangChain Python 에이전트 초기화 완료 확인해줘."})
    print(res.content)


if __name__ == "__main__":
    main()
