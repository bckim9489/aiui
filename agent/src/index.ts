import 'dotenv/config'
import { ChatOpenAI } from '@langchain/openai'
import { ChatPromptTemplate } from '@langchain/core/prompts'

// 기본적인 LangChain 파이프라인 예제 (ChatGPT 호환 API 사용)
async function main() {
  const apiKey = process.env.OPENAI_API_KEY
  if (!apiKey) {
    console.error('환경변수 OPENAI_API_KEY가 설정되어 있지 않습니다.')
    return
  }

  const llm = new ChatOpenAI({
    apiKey,
    model: 'gpt-4o-mini',
    temperature: 0.2
  })

  const prompt = ChatPromptTemplate.fromMessages([
    ['system', '너는 LangChain 기반의 실험 에이전트다. 응답은 간단히.'],
    ['human', '{input}']
  ])

  const chain = prompt.pipe(llm)
  const res = await chain.invoke({ input: 'LangChain 에이전트 프로젝트 초기화 완료 확인해줘.' })
  console.log(res.content)
}

main().catch((err) => {
  console.error(err)
  process.exit(1)
})
