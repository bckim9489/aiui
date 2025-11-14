import { type FormEvent, useState } from 'react'
import './App.css'
import { SandboxFrame } from './components/SandboxFrame'

type UiState = 'idle' | 'loading' | 'rendering' | 'error'

function App() {
  const [prompt, setPrompt] = useState('')
  const [uiState, setUiState] = useState<UiState>('idle')
  const [error, setError] = useState<string | null>(null)
  const [code, setCode] = useState<string | null>(null)
  const [resetKey, setResetKey] = useState(0)

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const trimmed = prompt.trim()
    if (!trimmed || uiState === 'loading') {
      return
    }

    setUiState('loading')
    setError(null)

    try {
      const response = await fetch('/ui/code', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ prompt: trimmed })
      })

      if (!response.ok) {
        throw new Error('UI 생성을 요청하는 중 문제가 발생했습니다.')
      }

      const payload = (await response.json()) as { code?: unknown }
      if (typeof payload.code !== 'string' || !payload.code.trim()) {
        throw new Error('서버에서 올바른 코드가 전달되지 않았습니다.')
      }

      setCode(payload.code)
      setUiState('rendering')
      setResetKey((previous) => previous + 1)
    } catch (err) {
      const message =
        err instanceof Error ? err.message : '알 수 없는 오류가 발생했습니다.'
      setError(message)
      setUiState('error')
      setCode(null)
    }
  }

  const handleReset = () => {
    setPrompt('')
    setCode(null)
    setError(null)
    setUiState('idle')
    setResetKey((previous) => previous + 1)
  }

  const isLoading = uiState === 'loading'

  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="branding">
          <span className="branding-dot" />
          <div>
            <p className="branding-label">Dynamic UI MVP</p>
            <h1>AI UI 실험실</h1>
          </div>
        </div>
        <button
          type="button"
          className="secondary-btn"
          onClick={handleReset}
          disabled={isLoading}
        >
          초기화
        </button>
      </header>

      <main className="app-main">
        <form className="prompt-form" onSubmit={handleSubmit}>
          <input
            className="prompt-input"
            placeholder="예) 재고 보여줘, 비밀번호 변경 화면 만들어줘"
            value={prompt}
            onChange={(event) => setPrompt(event.target.value)}
            disabled={isLoading}
          />
          <button
            type="submit"
            className="primary-btn"
            disabled={isLoading || !prompt.trim()}
          >
            {isLoading ? '생성 중...' : '실행'}
          </button>
        </form>

        {error && <p className="status status-error">{error}</p>}
        {uiState === 'loading' && (
          <p className="status status-info">AI가 UI를 생성하고 있습니다…</p>
        )}

        <section className="preview-panel">
          {uiState === 'rendering' && code ? (
            <SandboxFrame key={resetKey} code={code} />
          ) : (
            <div className="preview-placeholder">
              <h2>어떤 화면을 보고 싶나요?</h2>
              <p>프롬프트를 입력하면 실시간으로 UI가 생성됩니다.</p>
              <ul>
                <li>“재고 현황 대시보드 보여줘”</li>
                <li>“비밀번호 변경 화면 만들어줘”</li>
                <li>“사용자 관리 리스트 생성해줘”</li>
              </ul>
            </div>
          )}
        </section>
      </main>
    </div>
  )
}

export default App
