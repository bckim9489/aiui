from model import build_model
from prompt import build_prompt


def main():
    prompt = build_prompt()
    llm = build_model()
    chain = prompt | llm

    res = chain.invoke({"input": "LangChain Python 에이전트 구조를 분리했어. 확인해줘."})
    print(res.content)


if __name__ == "__main__":
    main()
