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
    if (!trimmed || uiState === 'loading') return

    setUiState('loading')
    setError(null)

    try {
      const response = await fetch('http://localhost:9595/ui/code', {
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
      setResetKey((prev) => prev + 1)
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
    setResetKey((prev) => prev + 1)
  }

  const showPrompt = uiState === 'idle' || uiState === 'error'
  const showFrame = uiState === 'rendering' && !!code
  const showLoading = uiState === 'loading'

  return (
    <div className="app-shell">
      {showPrompt && (
        <section className="center-stage">
          <form className="prompt-form prompt-form-large" onSubmit={handleSubmit}>
            <input
              className="prompt-input"
              placeholder="예) 재고 보여줘, 비밀번호 변경 화면 만들어줘"
              value={prompt}
              onChange={(event) => setPrompt(event.target.value)}
              disabled={showLoading}
            />
            <button
              type="submit"
              className="primary-btn"
              disabled={showLoading || !prompt.trim()}
            >
              {showLoading ? '생성 중...' : '실행'}
            </button>
          </form>
          {error && <p className="status status-error">{error}</p>}
        </section>
      )}

      {showLoading && (
        <div className="loader-panel">
          <div className="spinner" aria-label="로딩 중" />
          <p className="status status-info">AI가 UI를 생성하고 있습니다…</p>
        </div>
      )}

      {showFrame && code && (
        <section className="preview-panel">
          <SandboxFrame key={resetKey} code={code} />
        </section>
      )}

      <button type="button" className="floating-reset" onClick={handleReset}>
        초기화
      </button>
    </div>
  )
}

export default App
