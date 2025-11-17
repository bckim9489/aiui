import * as Babel from '@babel/standalone'
import * as React from 'react'
import * as ReactJsxRuntime from 'react/jsx-runtime'
import { useEffect, useRef, useState } from 'react'
import { createRoot, type Root } from 'react-dom/client'

type SandboxFrameProps = {
  code: string
}

export function SandboxFrame({ code }: SandboxFrameProps) {
  const hostRef = useRef<HTMLDivElement | null>(null)
  const reactRootRef = useRef<Root | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const host = hostRef.current
    if (!host) return

    // Tear down previous render
    if (reactRootRef.current) {
      reactRootRef.current.unmount()
      reactRootRef.current = null
    }

    try {
      const compiled = Babel.transform(code, {
        filename: 'generated.tsx',
        presets: ['react', 'typescript'],
        plugins: ['transform-modules-commonjs'],
        sourceType: 'module'
      }).code

      const module: { exports: any } = { exports: {} }
      // eslint-disable-next-line @typescript-eslint/no-implied-eval
      const fn = new Function('require', 'module', 'exports', compiled as string)
      const requireShim = (name: string) => {
        switch (name) {
          case 'react':
            return React
          case 'react/jsx-runtime':
            return ReactJsxRuntime
          default:
            throw new Error(`지원하지 않는 모듈입니다: ${name}`)
        }
      }
      fn(requireShim, module, module.exports)
      const Component = module.exports?.default ?? module.exports

      if (typeof Component !== 'function') {
        throw new Error('React 컴포넌트를 찾을 수 없습니다.')
      }

      const root = createRoot(host)
      reactRootRef.current = root
      setError(null)

      const api = {
        get: (url: string) =>
          fetch(url).then((res) => {
            if (!res.ok) throw new Error('API 요청 실패')
            return res.json()
          }),
        post: (url: string, body?: unknown) =>
          fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body ?? {})
          }).then((res) => {
            if (!res.ok) throw new Error('API 요청 실패')
            return res.json().catch(() => ({}))
          })
      }

      root.render(<Component api={api} />)
    } catch (err) {
      const message =
        err instanceof Error ? err.message : '코드를 실행하는 중 오류가 발생했습니다.'
      setError(message)
    }

    return () => {
      if (reactRootRef.current) {
        reactRootRef.current.unmount()
        reactRootRef.current = null
      }
    }
  }, [code])

  return (
    <div className="runner-shell">
      {error && <div className="runner-error">{error}</div>}
      <div ref={hostRef} className="runner-host" />
    </div>
  )
}
